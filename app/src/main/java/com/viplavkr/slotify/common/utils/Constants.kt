package com.viplavkr.slotify.common.utils

object Constants {
    // Intent Keys
    const val EXTRA_SLOT = "extra_slot"
    const val EXTRA_BOOKING = "extra_booking"
    const val EXTRA_LOCATION = "extra_location"
    const val EXTRA_USER = "extra_user"
    const val EXTRA_DURATION = "extra_duration"
    const val EXTRA_TOTAL_AMOUNT = "extra_total_amount"
    const val EXTRA_PAYMENT_METHOD = "extra_payment_method"
    const val EXTRA_BOOKING_ID = "extra_booking_id"

    // Request Codes
    const val REQUEST_CAMERA_PERMISSION = 100
    const val REQUEST_LOCATION_PERMISSION = 101

    // Time Constants
    const val SPLASH_DELAY = 2000L
    const val PAYMENT_DELAY = 2000L
    const val OTP_TIMEOUT = 60000L

    // Default Values
    const val DEFAULT_DURATION = 1
    const val MAX_DURATION = 24
    const val MIN_DURATION = 1
}