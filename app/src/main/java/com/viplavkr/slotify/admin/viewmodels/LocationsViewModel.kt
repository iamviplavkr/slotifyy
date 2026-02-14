package com.viplavkr.slotify.admin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viplavkr.slotify.common.models.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationsViewModel : ViewModel() {

    private val _locations = MutableLiveData<List<Location>>()
    val locations: LiveData<List<Location>> = _locations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success

    private val locationsList = mutableListOf<Location>()

    fun loadLocations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                delay(500)

                // Mock data
                locationsList.clear()
                locationsList.addAll(generateMockLocations())
                _locations.value = locationsList.toList()

            } catch (e: Exception) {
                _error.value = "Failed to load locations: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addLocation(name: String, address: String, city: String, pricePerHour: Double) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                delay(300)

                val newLocation = Location(
                    id = (locationsList.size + 1).toString(),
                    name = name,
                    address = address,
                    city = city,
                    pricePerHour = pricePerHour,
                    isActive = true
                )

                locationsList.add(newLocation)
                _locations.value = locationsList.toList()
                _success.value = "Location added successfully"

            } catch (e: Exception) {
                _error.value = "Failed to add location: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLocation(id: String, name: String, address: String, city: String, pricePerHour: Double) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                delay(300)

                val index = locationsList.indexOfFirst { it.id == id }
                if (index >= 0) {
                    val updated = locationsList[index].copy(
                        name = name,
                        address = address,
                        city = city,
                        pricePerHour = pricePerHour
                    )
                    locationsList[index] = updated
                    _locations.value = locationsList.toList()
                    _success.value = "Location updated successfully"
                }

            } catch (e: Exception) {
                _error.value = "Failed to update location: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLocationStatus(location: Location) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                delay(300)

                val index = locationsList.indexOfFirst { it.id == location.id }
                if (index >= 0) {
                    val updated = location.copy(isActive = !location.isActive)
                    locationsList[index] = updated
                    _locations.value = locationsList.toList()
                    _success.value = "Location ${if (updated.isActive) "activated" else "deactivated"}"
                }

            } catch (e: Exception) {
                _error.value = "Failed to update status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateMockLocations(): List<Location> {
        return listOf(
            Location("1", "Slotify Mall Parking", "123 Main Street, Ground Floor", "Mumbai", 19.0760, 72.8777, 50, 35, true, 50.0, "06:00", "22:00"),
            Location("2", "Central Plaza Parking", "456 Center Avenue, Basement", "Mumbai", 19.0821, 72.8756, 80, 52, true, 60.0, "00:00", "23:59"),
            Location("3", "Tech Park Parking", "789 Tech Boulevard, Multi-level", "Bangalore", 12.9716, 77.5946, 100, 78, true, 55.0, "06:00", "20:00"),
            Location("4", "Airport Parking Zone", "Terminal 2, Level B2", "Delhi", 28.5562, 77.1000, 200, 145, true, 80.0, "00:00", "23:59"),
            Location("5", "City Center Garage", "Downtown Plaza, Underground", "Chennai", 13.0827, 80.2707, 60, 42, false, 45.0, "08:00", "22:00")
        )
    }
}