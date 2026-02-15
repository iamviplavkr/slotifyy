package com.viplavkr.slotify.models

import java.io.Serializable
import java.util.Date

/**
 * Data model representing a parking booking
 */
data class Booking(
    val id: String,
    val userId: String,
    val parkingSlot: ParkingSlot,
    val startTime: Date,
    val endTime: Date,
    val durationHours: Int,
    val totalAmount: Double,
    val status: BookingStatus,
    val bookingDate: Date = Date(),
    val vehicleNumber: String
) : Serializable {

    /**
     * Get formatted booking time
     */
    fun getFormattedStartTime(): String {
        val format = java.text.SimpleDateFormat("hh:mm a, MMM dd", java.util.Locale.getDefault())
        return format.format(startTime)
    }

    fun getFormattedEndTime(): String {
        val format = java.text.SimpleDateFormat("hh:mm a, MMM dd", java.util.Locale.getDefault())
        return format.format(endTime)
    }

    /**
     * Get formatted total amount
     */
    fun getFormattedAmount(): String = String.format("$%.2f", totalAmount)
}

/**
 * Booking status enum
 */
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}