package com.viplavkr.slotify.common.models

import java.io.Serializable

data class ParkingSlot(
    val id: String,
    val slotNumber: String,
    val level: String,
    val type: SlotType = SlotType.STANDARD,
    val pricePerHour: Double = 50.0,
    val isAvailable: Boolean = true,
    val isActive: Boolean = true,
    val locationId: String = "",
    val locationName: String = ""
) : Serializable

enum class SlotType {
    COMPACT,
    STANDARD,
    LARGE;

    companion object {
        fun fromString(value: String): SlotType {
            return when (value.uppercase()) {
                "COMPACT" -> COMPACT
                "LARGE" -> LARGE
                else -> STANDARD
            }
        }
    }
}