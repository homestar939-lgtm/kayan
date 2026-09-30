package com.ai.assistance.operit.kayan.verifier

import com.ai.assistance.operit.data.model.ToolParameter

data class VerificationRecord(
    val toolName: String,
    val verdict: VerificationVerdict,
    val timestamp: Long,
    val parameters: List<ToolParameter>,
    val sequence: Long = 0L
) {
    val isFailure: Boolean get() = verdict.isFailure

    fun summary(): String = when (val v = verdict) {
        is VerificationVerdict.Pass    -> "PASS  [$toolName] ${v.evidence}"
        is VerificationVerdict.Fail    -> "FAIL  [$toolName] (${v.severity}) ${v.reason}"
        is VerificationVerdict.Blocked -> "BLOCK [$toolName] ${v.reason}"
    }
}
