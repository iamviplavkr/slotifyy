package com.viplavkr.slotify.common.models

import java.io.Serializable

data class Location(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val totalSlots: Int = 0,
    val availableSlots: Int = 0,
    val isActive: Boolean = true,
    val pricePerHour: Double = 50.0,
    val openTime: String = "06:00",
    val closeTime: String = "22:00",
    val amenities: List<String> = emptyList(),
    val imageUrl: String = ""
) : Serializable