package com.viplavkr.slotify.common.models

import java.io.Serializable

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val email: String = "",
    val role: String = "USER",
    val isActive: Boolean = true,
    val createdAt: String = "",
    val totalBookings: Int = 0,
    val totalSpent: Double = 0.0
) : Serializable