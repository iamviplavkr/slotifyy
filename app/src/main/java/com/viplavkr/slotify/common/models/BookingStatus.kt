package com.viplavkr.slotify.common.models

/**
 * All possible states in a booking's lifecycle.
 */
enum class BookingStatus {
    PENDING,
    LOCKED,
    CONFIRMED,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    EXPIRED
}
