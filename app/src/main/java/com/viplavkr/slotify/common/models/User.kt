package com.viplavkr.slotify.common.models

import java.util.UUID

/**
 * Core User data class used across the app.
 * Serializable so it can be passed between Activities via Intent extras.
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: Role = Role.USER,
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : java.io.Serializable
