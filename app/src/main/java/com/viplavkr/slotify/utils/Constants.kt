package com.viplavkr.slotify.utils

/**
 * Application-wide constants
 */
object Constants {

    // TODO: Replace with your actual API base URL
    const val API_BASE_URL = "https://api.slotify.com/"

    // Shared preferences keys
    const val PREFS_NAME = "SlotifyPrefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_VEHICLE_NUMBER = "vehicle_number"

    // Intent extras
    const val EXTRA_PARKING_SLOT = "extra_parking_slot"
    const val EXTRA_BOOKING = "extra_booking"
    const val EXTRA_DURATION = "extra_duration"

    // Animation durations
    const val SPLASH_DELAY = 3000L // 3 seconds
    const val ANIMATION_DURATION = 300L

    // Booking duration options (in hours)
    val DURATION_OPTIONS = listOf(1, 2, 3, 4, 5, 8, 12, 24)

    // Map configuration
    const val DEFAULT_ZOOM = 15f
    const val MAP_ANIMATION_DURATION = 1000
}