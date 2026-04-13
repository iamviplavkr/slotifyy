package com.viplavkr.slotify.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.models.ParkingSlot

/**
 * Manages slot listing, availability checks, and the booking/lock flow.
 * Observed by SlotsActivity to keep UI in sync.
 *
 * Key responsibility: prevents overbooking by checking time overlaps
 * before allowing a user to proceed to payment.
 */
class SlotsViewModel : ViewModel() {

    private val _slots = MutableLiveData<List<ParkingSlot>>()
    val slots: LiveData<List<ParkingSlot>> = _slots

    private val _unavailableSlotIds = MutableLiveData<Set<String>>()
    val unavailableSlotIds: LiveData<Set<String>> = _unavailableSlotIds

    private val _lockedBooking = MutableLiveData<Booking?>()
    val lockedBooking: LiveData<Booking?> = _lockedBooking

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Loads all slots for a location and marks which ones
     * are unavailable for the selected time range.
     */
    fun loadSlots(locationId: String, startTime: Long, endTime: Long) {
        _isLoading.value = true
        _error.value = null

        val allSlots = MockParkingRepository.getSlotsByLocation(locationId)
        _slots.value = allSlots

        val unavailable = MockParkingRepository.getUnavailableSlotIds(locationId, startTime, endTime)
        _unavailableSlotIds.value = unavailable

        _isLoading.value = false
    }

    /**
     * Refreshes availability without reloading the full slot list.
     * Call this after a booking or cancellation.
     */
    fun refreshAvailability(locationId: String, startTime: Long, endTime: Long) {
        val unavailable = MockParkingRepository.getUnavailableSlotIds(locationId, startTime, endTime)
        _unavailableSlotIds.value = unavailable
    }

    /**
     * Attempts to lock a slot for 5 minutes while user completes payment.
     * If the slot was taken between selection and lock attempt, shows error.
     */
    fun lockSlotForCheckout(
        userId: String,
        userName: String,
        slot: ParkingSlot,
        locationName: String,
        startTime: Long,
        endTime: Long
    ) {
        _isLoading.value = true
        _error.value = null

        // Calculate total amount
        val durationHours = ((endTime - startTime + 3_599_999) / 3_600_000).toInt()
        val totalAmount = slot.pricePerHour * durationHours

        // Double-check and lock
        if (!MockParkingRepository.isSlotAvailable(slot.id, startTime, endTime)) {
            _error.value = "This slot was just booked by someone else. Please choose another."
            _isLoading.value = false
            return
        }

        val booking = MockParkingRepository.lockSlot(
            userId = userId,
            userName = userName,
            slot = slot,
            locationName = locationName,
            startTime = startTime,
            endTime = endTime,
            totalAmount = totalAmount
        )

        if (booking != null) {
            _lockedBooking.value = booking
        } else {
            _error.value = "Slot Unavailable — someone booked it just now. Try a different slot."
        }

        _isLoading.value = false
    }

    /**
     * Cancels the current lock (e.g., user pressed back from payment).
     */
    fun cancelLock() {
        _lockedBooking.value?.let { booking ->
            MockParkingRepository.cancelBooking(booking.id)
            _lockedBooking.value = null
        }
    }

    fun clearError() {
        _error.value = null
    }
}
