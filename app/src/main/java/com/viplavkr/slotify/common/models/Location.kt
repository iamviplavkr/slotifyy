package com.viplavkr.slotify.common.models

/**
 * Represents a physical parking location (e.g., a mall or office building).
 * Each Location contains multiple ParkingSlots.
 */
data class Location(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val totalSlots: Int = 0,
    val imageUrl: String? = null,
    val rating: Float = 4.0f,
    val isActive: Boolean = true
) : java.io.Serializable
