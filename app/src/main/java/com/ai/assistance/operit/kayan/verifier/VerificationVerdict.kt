package com.ai.assistance.operit.kayan.verifier

sealed class VerificationVerdict {
    data class Pass(val evidence: String) : VerificationVerdict()
    data class Fail(val reason: String, val severity: Severity) : VerificationVerdict()
    data class Blocked(val reason: String) : VerificationVerdict()
}

enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }

val VerificationVerdict.isFailure: Boolean
    get() = this is VerificationVerdict.Fail || this is VerificationVerdict.Blocked
