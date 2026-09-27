package com.kayan.verifier

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * سجل الأنماط + حلقة تدقيق (Ring Buffer) بحجم ثابت.
 *
 * - thread-safe بالكامل.
 * - يحتفظ بآخر [ringCapacity] سجل فقط (الأقدم يُستبدل).
 * - يحسب الفشل المتتالي لكل أداة (لقاطع الدائرة).
 */
class VerificationHistory(
    private val ringCapacity: Int = RING_CAPACITY
) {
    // ── حلقة التدقيق (Ring Buffer) ──
    private val ring = arrayOfNulls<VerificationRecord>(ringCapacity)
    private val writeIndex = AtomicInteger(0)
    private val totalWritten = AtomicLong(0)
    private val ringLock = Any()

    // ── عدّادات الأنماط (thread-safe) ──
    private val consecutiveFailures = ConcurrentHashMap<String, AtomicInteger>()
    private val totalFailures = ConcurrentHashMap<String, AtomicInteger>()
    private val totalCalls = ConcurrentHashMap<String, AtomicInteger>()

    /** تسجيل قرار في الحلقة + تحديث العدّادات. */
    fun record(record: VerificationRecord) {
        // (1) العدّادات
        totalCalls.getOrPut(record.toolName) { AtomicInteger(0) }.incrementAndGet()
        if (record.isFailure) {
            consecutiveFailures.getOrPut(record.toolName) { AtomicInteger(0) }.incrementAndGet()
            totalFailures.getOrPut(record.toolName) { AtomicInteger(0) }.incrementAndGet()
        } else {
            consecutiveFailures[record.toolName]?.set(0) // نجاح يُصفّر التتالي
        }

        // (2) الحلقة
        synchronized(ringLock) {
            val idx = writeIndex.getAndUpdate { (it + 1) % ringCapacity }
            ring[idx] = record
            totalWritten.incrementAndGet()
        }
    }

    /** لقطة مرتّبة زمنياً (الأقدم → الأحدث). */
    fun snapshot(): List<VerificationRecord> = synchronized(ringLock) {
        val written = totalWritten.get()
        val filled = minOf(written, ringCapacity.toLong()).toInt()
        if (filled == 0) return@synchronized emptyList()

        val start = if (written < ringCapacity) 0 else writeIndex.get()
        val out = ArrayList<VerificationRecord>(filled)
        for (i in 0 until filled) {
            ring[(start + i) % ringCapacity]?.let { out.add(it) }
        }
        out
    }

    fun consecutiveFailures(toolName: String): Int =
        consecutiveFailures[toolName]?.get() ?: 0

    fun totalFailures(toolName: String): Int =
        totalFailures[toolName]?.get() ?: 0

    fun totalCalls(toolName: String): Int =
        totalCalls[toolName]?.get() ?: 0

    fun size(): Int = minOf(totalWritten.get(), ringCapacity.toLong()).toInt()

    fun clear() {
        synchronized(ringLock) {
            ring.fill(null)
            writeIndex.set(0)
            totalWritten.set(0)
        }
        consecutiveFailures.clear()
        totalFailures.clear()
        totalCalls.clear()
    }

    companion object {
        /** حجم حلقة التدقيق الافتراضي. */
        const val RING_CAPACITY: Int = 200
    }
}