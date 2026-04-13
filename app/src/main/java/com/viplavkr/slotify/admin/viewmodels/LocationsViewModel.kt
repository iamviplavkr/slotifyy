package com.viplavkr.slotify.admin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.Location

class LocationsViewModel : ViewModel() {
    private val _locations = MutableLiveData<List<Location>>()
    val locations: LiveData<List<Location>> = _locations

    fun loadLocations() {
        _locations.value = MockParkingRepository.getAllLocations()
    }
}
