package com.ai.assistance.operit.kayan.verifier

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class VerificationHistory(
    private val ringCapacity: Int = RING_CAPACITY
) {
    private val ring = arrayOfNulls<VerificationRecord>(ringCapacity)
    private val writeIndex = AtomicInteger(0)
    private val totalWritten = AtomicLong(0)
    private val ringLock = Any()

    private val consecutiveFailures = ConcurrentHashMap<String, AtomicInteger>()
    private val totalFailures = ConcurrentHashMap<String, AtomicInteger>()
    private val totalCalls = ConcurrentHashMap<String, AtomicInteger>()

    fun record(record: VerificationRecord) {
        totalCalls.getOrPut(record.toolName) { AtomicInteger(0) }.incrementAndGet()
        if (record.isFailure) {
            consecutiveFailures.getOrPut(record.toolName) { AtomicInteger(0) }.incrementAndGet()
            totalFailures.getOrPut(record.toolName) { AtomicInteger(0) }.incrementAndGet()
        } else {
            consecutiveFailures[record.toolName]?.set(0)
        }
        synchronized(ringLock) {
            val idx = writeIndex.getAndUpdate { (it + 1) % ringCapacity }
            ring[idx] = record
            totalWritten.incrementAndGet()
        }
    }

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

    fun consecutiveFailures(toolName: String): Int = consecutiveFailures[toolName]?.get() ?: 0
    fun totalFailures(toolName: String): Int = totalFailures[toolName]?.get() ?: 0
    fun totalCalls(toolName: String): Int = totalCalls[toolName]?.get() ?: 0
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
        const val RING_CAPACITY: Int = 200
    }
}
