package com.viplavkr.slotify.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.models.BookingStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BookingsViewModel : ViewModel() {

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Simulate network delay
                delay(500)

                // Mock data - in production, call API
                val mockBookings = generateMockBookings()
                _bookings.value = mockBookings

            } catch (e: Exception) {
                _error.value = "Failed to load bookings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateMockBookings(): List<Booking> {
        return listOf(
            Booking(
                id = "1",
                bookingId = "BK12345678",
                userId = "user1",
                slotId = "1",
                slotNumber = "A-01",
                level = "A",
                locationName = "Slotify Mall Parking",
                startTime = "15 Jun 2025, 10:00 AM",
                endTime = "15 Jun 2025, 12:00 PM",
                duration = 2,
                totalAmount = 100.0,
                status = BookingStatus.ACTIVE,
                paymentMethod = "CARD"
            ),
            Booking(
                id = "2",
                bookingId = "BK87654321",
                userId = "user1",
                slotId = "5",
                slotNumber = "A-05",
                level = "A",
                locationName = "Slotify Mall Parking",
                startTime = "10 Jun 2025, 02:00 PM",
                endTime = "10 Jun 2025, 05:00 PM",
                duration = 3,
                totalAmount = 210.0,
                status = BookingStatus.COMPLETED,
                paymentMethod = "UPI"
            ),
            Booking(
                id = "3",
                bookingId = "BK11223344",
                userId = "user1",
                slotId = "7",
                slotNumber = "B-02",
                level = "B",
                locationName = "Central Plaza Parking",
                startTime = "05 Jun 2025, 09:00 AM",
                endTime = "05 Jun 2025, 11:00 AM",
                duration = 2,
                totalAmount = 120.0,
                status = BookingStatus.COMPLETED,
                paymentMethod = "CARD"
            )
        )
    }
}