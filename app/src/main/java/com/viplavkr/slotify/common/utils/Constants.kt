package com.viplavkr.slotify.common.utils

/**
 * App-wide constants. Single source of truth for keys, defaults, and config.
 */
object Constants {

    // SharedPreferences
    const val PREFS_NAME = "slotify_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_PHONE = "user_phone"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_TOKEN_EXPIRY = "token_expiry"

    // Intent Extras
    const val EXTRA_BOOKING = "extra_booking"
    const val EXTRA_BOOKING_ID = "extra_booking_id"
    const val EXTRA_PARKING_SLOT = "extra_parking_slot"
    const val EXTRA_LOCATION = "extra_location"
    const val EXTRA_SLOT_ID = "extra_slot_id"
    const val EXTRA_START_TIME = "extra_start_time"
    const val EXTRA_END_TIME = "extra_end_time"
    const val EXTRA_AMOUNT = "extra_amount"

    // Booking Status
    const val STATUS_PENDING = "PENDING"
    const val STATUS_LOCKED = "LOCKED"
    const val STATUS_CONFIRMED = "CONFIRMED"
    const val STATUS_ACTIVE = "ACTIVE"
    const val STATUS_COMPLETED = "COMPLETED"
    const val STATUS_CANCELLED = "CANCELLED"
    const val STATUS_EXPIRED = "EXPIRED"

    // Slot lock duration (5 minutes in millis)
    // Slot lock duration (5 minutes)
    const val SLOT_LOCK_DURATION_MS = 5 * 60 * 1000L

    // Token validity (24 hours in millis)
    const val TOKEN_VALIDITY_MS = 24 * 60 * 60 * 1000L

    // Mock UPI ID for demo
    const val DEMO_UPI_ID = "slotify@upi"

    // Pricing
    const val BASE_PRICE_PER_HOUR = 40.0
    const val PREMIUM_MULTIPLIER = 1.5
    const val EXTENSION_SURCHARGE = 10.0

    // Vehicle Types
    const val VEHICLE_COMPACT = "COMPACT"
    const val VEHICLE_STANDARD = "STANDARD"
    const val VEHICLE_LARGE = "LARGE"

    // Splash
    const val SPLASH_DELAY = 1500L
}
