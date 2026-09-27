package com.kayan.verifier

import com.ai.assistance.operit.data.model.ToolParameter

/**
 * سجل تحقق واحد — غير قابل للتغيير (immutable) لأغراض التدقيق.
 *
 * @param sequence رقم تسلسلي متزايد (لترتيب الحلقة عند التزامن).
 */
data class VerificationRecord(
    val toolName: String,
    val verdict: VerificationVerdict,
    val timestamp: Long,
    val parameters: List<ToolParameter>,
    val sequence: Long = 0L
) {
    val isFailure: Boolean get() = verdict.isFailure

    /** ملخّص نصّي للبث/العرض. */
    fun summary(): String = when (val v = verdict) {
        is VerificationVerdict.Pass    -> "PASS  [$toolName] ${v.evidence}"
        is VerificationVerdict.Fail    -> "FAIL  [$toolName] (${v.severity}) ${v.reason}"
        is VerificationVerdict.Blocked -> "BLOCK [$toolName] ${v.reason}"
    }
}