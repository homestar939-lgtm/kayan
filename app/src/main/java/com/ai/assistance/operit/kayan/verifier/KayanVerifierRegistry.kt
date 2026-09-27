package com.ai.assistance.operit.kayan.verifier

import android.content.Context
import com.ai.assistance.operit.core.tools.AIToolHandler

object KayanVerifierRegistry {

    @Volatile private var hook: KayanVerifierHook? = null
    private val lock = Any()

    fun install(
        context: Context,
        policy: VerificationPolicy = VerificationPolicy(),
        history: VerificationHistory = VerificationHistory(),
        broadcaster: VerdictBroadcaster = NoOpVerdictBroadcaster
    ): KayanVerifierHook = synchronized(lock) {
        hook?.let { return it }
        val h = KayanVerifierHook(policy, history, broadcaster)
        AIToolHandler.getInstance(context).addToolHook(h)
        hook = h
        h
    }

    fun uninstall(context: Context) = synchronized(lock) {
        hook?.let { AIToolHandler.getInstance(context).removeToolHook(it) }
        hook = null
    }

    fun current(): KayanVerifierHook? = hook
    fun isInstalled(): Boolean = hook != null
}
