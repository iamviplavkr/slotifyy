package com.viplavkr.slotify.common.models

import java.io.Serializable

data class Booking(
    val id: String,
    val bookingId: String,
    val userId: String,
    val userName: String = "",
    val userPhone: String = "",
    val slotId: String,
    val slotNumber: String,
    val level: String,
    val locationName: String = "",
    val startTime: String,
    val endTime: String,
    val duration: Int,
    val totalAmount: Double,
    val status: BookingStatus = BookingStatus.ACTIVE,
    val paymentMethod: String = "CARD",
    val createdAt: String = ""
) : Serializable

enum class BookingStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED;

    companion object {
        fun fromString(value: String): BookingStatus {
            return when (value.uppercase()) {
                "COMPLETED" -> COMPLETED
                "CANCELLED" -> CANCELLED
                else -> ACTIVE
            }
        }
    }
}