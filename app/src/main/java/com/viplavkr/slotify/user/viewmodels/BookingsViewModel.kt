package com.viplavkr.slotify.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.Booking

/**
 * Manages the user's booking history and active bookings.
 * Supports extending and cancelling bookings.
 */
class BookingsViewModel : ViewModel() {

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _extensionResult = MutableLiveData<Result<Booking>?>()
    val extensionResult: LiveData<Result<Booking>?> = _extensionResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadBookings(userId: String) {
        _isLoading.value = true
        _bookings.value = MockParkingRepository.getBookingsByUser(userId)
        _isLoading.value = false
    }

    fun extendBooking(bookingId: String, additionalHours: Int) {
        _isLoading.value = true
        val result = MockParkingRepository.extendBooking(bookingId, additionalHours)
        _extensionResult.value = result
        _isLoading.value = false
    }

    fun cancelBooking(bookingId: String, userId: String) {
        MockParkingRepository.cancelBooking(bookingId)
        loadBookings(userId) // Refresh list
    }

    fun clearExtensionResult() {
        _extensionResult.value = null
    }
}
