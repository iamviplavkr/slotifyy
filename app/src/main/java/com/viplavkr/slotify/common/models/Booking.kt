package com.viplavkr.slotify.common.models

import java.util.UUID

/**
 * Booking status enum (type-safe)
 */


/**
 * Core booking entity
 */
data class Booking(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val slotId: String,
    val slotNumber: String,
    val locationId: String,
    val locationName: String,
    val vehicleType: String,
    val startTime: Long,
    val endTime: Long,
    val totalAmount: Double,

    var status: BookingStatus = BookingStatus.PENDING,

    val lockedAt: Long? = null,
    val confirmedAt: Long? = null,
    val completedAt: Long? = null,
    val paymentMethod: String? = null,
    val transactionId: String? = null,

    val createdAt: Long = System.currentTimeMillis()

) : java.io.Serializable {

    fun isLockExpired(): Boolean {
        if (status != BookingStatus.LOCKED || lockedAt == null) return false
        return System.currentTimeMillis() - lockedAt > 5 * 60 * 1000L
    }

    fun isCurrentlyActive(): Boolean {
        val now = System.currentTimeMillis()
        return status == BookingStatus.CONFIRMED && now in startTime..endTime
    }

    fun getDurationHours(): Int {
        val diffMs = endTime - startTime
        return ((diffMs + 3_599_999) / 3_600_000).toInt()
    }

    fun getStatusDisplay(): String = when (status) {
        BookingStatus.PENDING -> "Pending"
        BookingStatus.LOCKED -> "Reserved (Payment Pending)"
        BookingStatus.CONFIRMED -> if (isCurrentlyActive()) "Active" else "Confirmed"
        BookingStatus.ACTIVE -> "Active"
        BookingStatus.COMPLETED -> "Completed"
        BookingStatus.CANCELLED -> "Cancelled"
        BookingStatus.EXPIRED -> "Expired"
    }
}

