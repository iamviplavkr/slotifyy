package com.viplavkr.slotify.models


import java.io.Serializable

/**
 * Data model representing a parking slot
 * Serializable to allow passing between activities
 */
data class ParkingSlot(
    val id: String,
    val name: String,
    val location: String,
    val address: String,
    val distance: Double, // in km
    val pricePerHour: Double,
    val totalSlots: Int,
    val availableSlots: Int,
    val rating: Float,
    val latitude: Double,
    val longitude: Double,
    val features: List<String> = listOf(), // e.g., "Covered", "EV Charging", "Security"
    val imageUrl: String = ""
) : Serializable {

    /**
     * Check if parking slot is available
     */
    fun isAvailable(): Boolean = availableSlots > 0

    /**
     * Calculate occupancy percentage
     */
    fun getOccupancyPercentage(): Int {
        return if (totalSlots > 0) {
            ((totalSlots - availableSlots) * 100) / totalSlots
        } else 0
    }

    /**
     * Get formatted distance string
     */
    fun getFormattedDistance(): String = String.format("%.1f km away", distance)

    /**
     * Get formatted price string
     */
    fun getFormattedPrice(): String = String.format("$%.2f/hr", pricePerHour)
}