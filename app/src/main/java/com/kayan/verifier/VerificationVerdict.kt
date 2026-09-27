package com.kayan.verifier

/**
 * نتيجة التحقق لأداة واحدة.
 *
 * - [Pass]    : نجحت الأداة واجتازت الفحوص الإضافية.
 * - [Fail]    : فشلت الأداة أو النتيجة غير متسقة.
 * - [Blocked] : مُنعت قبلياً بواسطة السياسة (لم تُنفَّذ أصلاً).
 */
sealed class VerificationVerdict {

    /** نجاح مُوثَّق بدليل نصّي. */
    data class Pass(val evidence: String) : VerificationVerdict()

    /** فشل مع سبب ودرجة خطورة. */
    data class Fail(val reason: String, val severity: Severity) : VerificationVerdict()

    /** منع قبلي بواسطة [VerificationPolicy]. */
    data class Blocked(val reason: String) : VerificationVerdict()
}

/** درجة خطورة الفشل. */
enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }

/** هل القرار يُعدّ فشلاً؟ */
val VerificationVerdict.isFailure: Boolean
    get() = this is VerificationVerdict.Fail || this is VerificationVerdict.Blocked

/** تسمية قصيرة للعرض/البث. */
val VerificationVerdict.label: String
    get() = when (this) {
        is VerificationVerdict.Pass    -> "PASS"
        is VerificationVerdict.Fail    -> "FAIL"
        is VerificationVerdict.Blocked -> "BLOCK"
    }
