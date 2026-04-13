package com.viplavkr.slotify.common.models

/**
 * A single parking slot within a Location.
 * vehicleType determines which car icon to show.
 * pricePerHour is calculated from base price + vehicle multiplier.
 */
data class ParkingSlot(
    val id: String,
    val locationId: String,
    val slotNumber: String,         // e.g., "A-01", "B-12"
    val floor: String = "Ground",   // e.g., "Ground", "Level 1", "Level 2"
val vehicleType: String,        // COMPACT, STANDARD, LARGE
val pricePerHour: Double,
val isActive: Boolean = true
) : java.io.Serializable
