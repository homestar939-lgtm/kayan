package com.kayan.verifier

import com.ai.assistance.operit.data.model.AITool

/** قرار السياسة القبلية. */
sealed class PolicyDecision {
    object Allow : PolicyDecision()
    data class Block(val reason: String) : PolicyDecision()
}

/**
 * قواعد المنع القبلي — تُقيَّم في onToolCallIntercept قبل التنفيذ.
 *
 * @param blockedTools أدوات محظورة صراحةً (فارغة افتراضياً — لا حجب أدوات).
 * @param dangerousPatterns أنماط خطر في قيم المعاملات.
 * @param maxConsecutiveFailures حدّ قاطع الدائرة.
 */
class VerificationPolicy(
    private val blockedTools: Set<String> = DEFAULT_BLOCKED_TOOLS,
    private val dangerousPatterns: List<Regex> = DEFAULT_DANGEROUS_PATTERNS,
    private val maxConsecutiveFailures: Int = DEFAULT_MAX_CONSECUTIVE_FAILURES
) {

    fun evaluate(tool: AITool, history: VerificationHistory): PolicyDecision {
        // (1) أداة محظورة صراحةً (فارغة افتراضياً)
        if (blockedTools.isNotEmpty() && tool.name in blockedTools) {
            return PolicyDecision.Block("Tool '${tool.name}' is on the deny-list")
        }

        // (2) نمط خطر في المعاملات
        for (param in tool.parameters) {
            for (pattern in dangerousPatterns) {
                if (pattern.containsMatchIn(param.value)) {
                    return PolicyDecision.Block(
                        "Parameter '${param.name}' matches dangerous pattern [${pattern.pattern}]"
                    )
                }
            }
        }

        // (3) قاطع الدائرة: فشل متتالي
        val fails = history.consecutiveFailures(tool.name)
        if (fails >= maxConsecutiveFailures) {
            return PolicyDecision.Block(
                "Circuit breaker: '${tool.name}' failed $fails consecutive times"
            )
        }

        return PolicyDecision.Allow
    }

    companion object {
        /** لا حجب أدوات — فقط الأنماط. */
        val DEFAULT_BLOCKED_TOOLS: Set<String> = emptySet()

        /** حدّ قاطع الدائرة. */
        const val DEFAULT_MAX_CONSECUTIVE_FAILURES: Int = 3

        /** 17 نمط خطر: تدمير / تصعيد صلاحيات / تنفيذ عن بُعد / تعطيل النظام. */
        val DEFAULT_DANGEROUS_PATTERNS: List<Regex> = listOf(
            // ── تدمير الملفات ──
            Regex("""rm\s+(-[a-zA-Z]+\s+)*-[a-zA-Z]*[rR][a-zA-Z]*[fF]"""), // rm -rf
            Regex("""rm\s+.*\s+/\s*($|;|&)"""), // rm ... /
            Regex("""\bmkfs(\.[a-z0-9]+)?\b"""), // mkfs
            Regex("""\bdd\s+.*\bof=/dev/"""), // dd to device

            // ── الكتابة على أجهزة الكتلة ──
            Regex(""">\s*/dev/(sd|mmcblk|block)/"""), // > /dev/sda

            // ── قنابل الفورك ──
            Regex(""":\(\)\s*\{\s*:\|:&\s*\}\s*;\s*:"""), // :(){ :|:& };:

            // ── تنفيذ عن بُعد ──
            Regex("""(curl|wget)\s+[^\n|]*\|\s*(sh|bash|zsh)"""), // curl ... | sh
            Regex("""base64\s+(-d|--decode)\s*\|\s*(sh|bash)"""), // base64 -d | sh
            Regex("""\bnc\s+.*(-e|--exec)\b"""), // netcat reverse shell
            Regex("""\bncat\s+.*--sh-exec\b"""), // ncat reverse shell

            // ── تصعيد الصلاحيات ──
            Regex("""\bsudo\s+rm\b"""), // sudo rm
            Regex("""\bchmod\s+(-R\s+)?777\s+/\s*($|;|&)"""), // chmod 777 /
            Regex("""\bchown\s+-R\s+root\s*:"""), // chown -R root:
            Regex("""\bpasswd\b"""), // passwd

            // ── تعطيل النظام ──
            Regex("""\b(shutdown|reboot|halt|poweroff)\b"""), // إيقاف النظام
            Regex("""\biptables\s+-F\b"""), // مسح الجدار الناري
            Regex("""\bhistory\s+-c\b""") // محو السجل
        )
    }
}