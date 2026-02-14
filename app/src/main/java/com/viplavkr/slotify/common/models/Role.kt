package com.viplavkr.slotify.common.models

enum class Role {
    USER,
    ADMIN;

    companion object {
        fun fromString(value: String): Role {
            return when (value.uppercase()) {
                "ADMIN" -> ADMIN
                else -> USER
            }
        }
    }
}