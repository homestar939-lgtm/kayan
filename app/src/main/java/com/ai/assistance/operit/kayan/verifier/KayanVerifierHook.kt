package com.ai.assistance.operit.kayan.verifier

import com.ai.assistance.operit.core.tools.AITool
import com.ai.assistance.operit.core.tools.AIToolHook
import com.ai.assistance.operit.core.tools.AIToolHookDecision
import com.ai.assistance.operit.core.tools.StringResultData
import com.ai.assistance.operit.core.tools.ToolResult
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class KayanVerifierHook(
    private val policy: VerificationPolicy = VerificationPolicy(),
    private val history: VerificationHistory = VerificationHistory(),
    private val broadcaster: VerdictBroadcaster = NoOpVerdictBroadcaster
) : AIToolHook {

    private val lastVerdicts = ConcurrentHashMap<String, VerificationVerdict>()
    private val sequence = AtomicLong(0)

    override fun onToolCallRequested(tool: AITool) {}

    override fun onToolCallIntercept(tool: AITool): AIToolHookDecision {
        return when (val decision = policy.evaluate(tool, history)) {
            is PolicyDecision.Block -> {
                record(tool, VerificationVerdict.Blocked(decision.reason))
                AIToolHookDecision.Block(decision.reason)
            }
            PolicyDecision.Allow -> AIToolHookDecision.Allow
        }
    }

    override fun onToolPermissionChecked(tool: AITool, granted: Boolean, reason: String?) {
        if (!granted) {
            record(tool, VerificationVerdict.Fail(
                reason = "Permission denied: ${reason ?: "unknown"}",
                severity = Severity.MEDIUM
            ))
        }
    }

    override fun onToolExecutionStarted(tool: AITool) {}

    override fun onToolExecutionResult(tool: AITool, result: ToolResult) {
        record(tool, evaluateResult(result))
    }

    override fun onToolExecutionError(tool: AITool, throwable: Throwable) {
        record(tool, VerificationVerdict.Fail(
            reason = "Execution error: ${throwable.message ?: throwable::class.java.simpleName}",
            severity = Severity.HIGH
        ))
    }

    override fun onToolExecutionFinished(tool: AITool) {}

    private fun evaluateResult(result: ToolResult): VerificationVerdict {
        if (!result.success) {
            return VerificationVerdict.Fail(
                reason = result.error ?: "Tool reported failure",
                severity = severityFromError(result.error)
            )
        }
        val data = result.result
        if (data is StringResultData && data.value.isBlank()) {
            return VerificationVerdict.Fail("Empty result payload", Severity.LOW)
        }
        return VerificationVerdict.Pass(
            evidence = "success=true, type=${data::class.java.simpleName}"
        )
    }

    private fun severityFromError(error: String?): Severity {
        val e = error?.lowercase() ?: return Severity.MEDIUM
        return when {
            "fatal" in e || "crash" in e       -> Severity.CRITICAL
            "permission" in e || "denied" in e -> Severity.HIGH
            "timeout" in e || "network" in e   -> Severity.MEDIUM
            "not found" in e                   -> Severity.LOW
            else                               -> Severity.MEDIUM
        }
    }

    private fun record(tool: AITool, verdict: VerificationVerdict) {
        val rec = VerificationRecord(
            toolName = tool.name,
            verdict = verdict,
            timestamp = System.currentTimeMillis(),
            parameters = tool.parameters,
            sequence = sequence.incrementAndGet()
        )
        lastVerdicts[rec.toolName] = verdict
        history.record(rec)
        runCatching { broadcaster.broadcast(rec) }
    }

    fun lastVerdict(toolName: String): VerificationVerdict? = lastVerdicts[toolName]
    fun auditLog(): List<VerificationRecord> = history.snapshot()
    fun historySize(): Int = history.size()
    fun failureCount(toolName: String): Int = history.totalFailures(toolName)
    fun consecutiveFailureCount(toolName: String): Int = history.consecutiveFailures(toolName)

    fun clear() {
        lastVerdicts.clear()
        history.clear()
    }
}

interface VerdictBroadcaster {
    fun broadcast(record: VerificationRecord)
}

object NoOpVerdictBroadcaster : VerdictBroadcaster {
    override fun broadcast(record: VerificationRecord) {}
}
