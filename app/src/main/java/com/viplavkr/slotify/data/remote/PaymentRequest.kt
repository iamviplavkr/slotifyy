package com.viplavkr.slotify.data.remote

data class PaymentRequest(
    val bookingId: Int,
    val amount: Double,
    val paymentMode: String
)
