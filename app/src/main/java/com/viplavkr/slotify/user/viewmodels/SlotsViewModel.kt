package com.viplavkr.slotify.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.models.SlotType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SlotsViewModel : ViewModel() {

    private val _slots = MutableLiveData<List<ParkingSlot>>()
    val slots: LiveData<List<ParkingSlot>> = _slots

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadSlots(locationId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Simulate network delay
                delay(500)

                // Mock data - in production, call API
                val mockSlots = generateMockSlots()
                _slots.value = mockSlots

            } catch (e: Exception) {
                _error.value = "Failed to load slots: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateMockSlots(): List<ParkingSlot> {
        return listOf(
            // Level A
            ParkingSlot("1", "A-01", "A", SlotType.COMPACT, 50.0, true, true),
            ParkingSlot("2", "A-02", "A", SlotType.COMPACT, 50.0, true, true),
            ParkingSlot("3", "A-03", "A", SlotType.STANDARD, 60.0, false, true),
            ParkingSlot("4", "A-04", "A", SlotType.STANDARD, 60.0, true, true),
            ParkingSlot("5", "A-05", "A", SlotType.LARGE, 70.0, true, true),

            // Level B
            ParkingSlot("6", "B-01", "B", SlotType.COMPACT, 50.0, true, true),
            ParkingSlot("7", "B-02", "B", SlotType.STANDARD, 60.0, true, true),
            ParkingSlot("8", "B-03", "B", SlotType.STANDARD, 60.0, false, true),
            ParkingSlot("9", "B-04", "B", SlotType.LARGE, 70.0, true, true),

            // Level C
            ParkingSlot("10", "C-01", "C", SlotType.COMPACT, 50.0, true, true),
            ParkingSlot("11", "C-02", "C", SlotType.STANDARD, 60.0, true, true),
            ParkingSlot("12", "C-03", "C", SlotType.LARGE, 70.0, false, true)
        )
    }
}