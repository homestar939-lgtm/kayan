package com.kayan.verifier

import android.content.Context
import com.ai.assistance.operit.core.tools.AIToolHandler

/**
 * نقطة التسجيل الوحيدة + Lifecycle.
 * Singleton آمن للخيوط — يستخدم addToolHook / removeToolHook الموجودة.
 */
object KayanVerifierRegistry {

    @Volatile private var hook: KayanVerifierHook? = null
    private val lock = Any()

    /** تثبيت الـ hook (idempotent). */
    fun install(
        context: Context,
        policy: VerificationPolicy = VerificationPolicy(),
        history: VerificationHistory = VerificationHistory(),
        broadcaster: VerdictBroadcaster = NoOpVerdictBroadcaster
    ): KayanVerifierHook = synchronized(lock) {
        hook?.let { return it } // مُثبَّت مسبقاً
        val h = KayanVerifierHook(policy, history, broadcaster)
        AIToolHandler.getInstance(context).addToolHook(h)
        hook = h
        h
    }

    /** إزالة الـ hook. */
    fun uninstall(context: Context) = synchronized(lock) {
        hook?.let { AIToolHandler.getInstance(context).removeToolHook(it) }
        hook = null
    }

    fun current(): KayanVerifierHook? = hook
    fun isInstalled(): Boolean = hook != null
}