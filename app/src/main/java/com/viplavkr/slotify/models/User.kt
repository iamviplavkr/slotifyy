package com.viplavkr.slotify.models

import java.io.Serializable

/**
 * Data model representing a user
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val profileImageUrl: String = "",
    val vehicleNumber: String = "",
    val vehicleType: String = "Car" // Car, Bike, SUV, etc.
) : Serializable