package com.ai.assistance.operit.kayan.verifier

import com.ai.assistance.operit.data.model.AITool

sealed class PolicyDecision {
    object Allow : PolicyDecision()
    data class Block(val reason: String) : PolicyDecision()
}

class VerificationPolicy(
    private val blockedTools: Set<String> = DEFAULT_BLOCKED_TOOLS,
    private val dangerousPatterns: List<Regex> = DEFAULT_DANGEROUS_PATTERNS,
    private val maxConsecutiveFailures: Int = DEFAULT_MAX_CONSECUTIVE_FAILURES
) {
    fun evaluate(tool: AITool, history: VerificationHistory): PolicyDecision {
        if (blockedTools.isNotEmpty() && tool.name in blockedTools) {
            return PolicyDecision.Block("Tool '${tool.name}' is on the deny-list")
        }
        for (param in tool.parameters) {
            for (pattern in dangerousPatterns) {
                if (pattern.containsMatchIn(param.value)) {
                    return PolicyDecision.Block(
                        "Parameter '${param.name}' matches dangerous pattern [${pattern.pattern}]"
                    )
                }
            }
        }
        val fails = history.consecutiveFailures(tool.name)
        if (fails >= maxConsecutiveFailures) {
            return PolicyDecision.Block(
                "Circuit breaker: '${tool.name}' failed $fails consecutive times"
            )
        }
        return PolicyDecision.Allow
    }

    companion object {
        val DEFAULT_BLOCKED_TOOLS: Set<String> = emptySet()
        const val DEFAULT_MAX_CONSECUTIVE_FAILURES: Int = 3
        val DEFAULT_DANGEROUS_PATTERNS: List<Regex> = listOf(
            Regex("""rm\s+(-[a-zA-Z]+\s+)*-[a-zA-Z]*[rR][a-zA-Z]*[fF]"""),
            Regex("""rm\s+.*\s+/\s*($|;|&)"""),
            Regex("""\bmkfs(\.[a-z0-9]+)?\b"""),
            Regex("""\bdd\s+.*\bof=/dev/"""),
            Regex(""">\s*/dev/(sd|mmcblk|block)/"""),
            Regex(""":\(\)\s*\{\s*:\|:&\s*\}\s*;\s*:"""),
            Regex("""(curl|wget)\s+[^\n|]*\|\s*(sh|bash|zsh)"""),
            Regex("""base64\s+(-d|--decode)\s*\|\s*(sh|bash)"""),
            Regex("""\bnc\s+.*(-e|--exec)\b"""),
            Regex("""\bncat\s+.*--sh-exec\b"""),
            Regex("""\bsudo\s+rm\b"""),
            Regex("""\bchmod\s+(-R\s+)?777\s+/\s*($|;|&)"""),
            Regex("""\bchown\s+-R\s+root\s*:"""),
            Regex("""\bpasswd\b"""),
            Regex("""\b(shutdown|reboot|halt|poweroff)\b"""),
            Regex("""\biptables\s+-F\b"""),
            Regex("""\bhistory\s+-c\b""")
        )
    }
}
