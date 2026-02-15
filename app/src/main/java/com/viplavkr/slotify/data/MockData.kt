package com.viplavkr.slotify.data


import com.viplavkr.slotify.models.Booking
import com.viplavkr.slotify.models.BookingStatus
import com.viplavkr.slotify.models.ParkingSlot
import com.viplavkr.slotify.models.User
import java.util.Date
import java.util.UUID

/**
 * Mock data for testing and development
 * Replace these with actual API calls in production
 */
object MockData {

    /**
     * Mock user data
     * TODO: Replace with actual user authentication API
     */
    fun getMockUser(): User {
        return User(
            id = UUID.randomUUID().toString(),
            name = "John Doe",
            email = "john.doe@example.com",
            phone = "+1 234 567 8900",
            vehicleNumber = "ABC-1234",
            vehicleType = "Car"
        )
    }

    /**
     * Mock parking slots data
     * TODO: Replace with API call to GET /api/parking-slots
     */
    fun getMockParkingSlots(): List<ParkingSlot> {
        return listOf(
            ParkingSlot(
                id = "1",
                name = "Manipal Parking",
                location = "Dehmi Kalan",
                address = "Jaipur-Ajmer Express Highway, Dehmi Kalan",
                distance = 0.5,
                pricePerHour = 15.0,
                totalSlots = 50,
                availableSlots = 12,
                rating = 4.5f,
                latitude = 37.7749,
                longitude = -122.4194,
                features = listOf("Covered", "Security Camera", "24/7 Access")
            ),
            ParkingSlot(
                id = "2",
                name = "Mall Of Jaipur",
                location = "Business District",
                address = "Vaishali Nagar, Jaipur",
                distance = 28.0,
                pricePerHour = 25.0,
                totalSlots = 100,
                availableSlots = 35,
                rating = 4.8f,
                latitude = 37.7849,
                longitude = -122.4094,
                features = listOf("Covered", "EV Charging", "Security")
            ),
            ParkingSlot(
                id = "3",
                name = "Jaipur Airport",
                location = "Airport",
                address = "Airport Road",
                distance = 35.0,
                pricePerHour = 40.0,
                totalSlots = 200,
                availableSlots = 88,
                rating = 4.2f,
                latitude = 37.6189,
                longitude = -122.3750,
                features = listOf("Shuttle Service", "Security", "Long-term")
            ),
            ParkingSlot(
                id = "4",
                name = "Jaipur railway station",
                location = "Travel",
                address = "Jaipur, Rajasthan",
                distance = 22.1,
                pricePerHour = 20.0,
                totalSlots = 150,
                availableSlots = 45,
                rating = 4.0f,
                latitude = 37.7649,
                longitude = -122.4294,
                features = listOf("Covered", "Security", "Valet Available")
            ),
            ParkingSlot(
                id = "5",
                name = "Cricket Stadium",
                location = "Sports Complex",
                address = "555 Victory Way",
                distance = 3.5,
                pricePerHour = 8.0,
                totalSlots = 80,
                availableSlots = 5,
                rating = 4.7f,
                latitude = 37.7949,
                longitude = -122.3894,
                features = listOf("VIP Access", "Security", "Reserved")
            ),
            ParkingSlot(
                id = "6",
                name = "World Trade Park",
                location = "Jaipur",
                address = "Malviya Nagar, Jaipur",
                distance = 24.2,
                pricePerHour = 50.0,
                totalSlots = 60,
                availableSlots = 0,
                rating = 4.3f,
                latitude = 37.8049,
                longitude = -122.4594,
                features = listOf("Beach Access", "Open Air")
            )
        )
    }

    /**
     * Search parking slots by location
     * TODO: Replace with API call to GET /api/parking-slots/search?query={location}
     */
    fun searchParkingSlots(query: String): List<ParkingSlot> {
        return getMockParkingSlots().filter { slot ->
            slot.name.contains(query, ignoreCase = true) ||
                    slot.location.contains(query, ignoreCase = true) ||
                    slot.address.contains(query, ignoreCase = true)
        }
    }

    /**
     * Create a mock booking
     * TODO: Replace with API call to POST /api/bookings
     */
    fun createMockBooking(
        parkingSlot: ParkingSlot,
        durationHours: Int,
        vehicleNumber: String
    ): Booking {
        val startTime = Date()
        val endTime = Date(startTime.time + (durationHours * 60 * 60 * 1000))

        return Booking(
            id = UUID.randomUUID().toString(),
            userId = getMockUser().id,
            parkingSlot = parkingSlot,
            startTime = startTime,
            endTime = endTime,
            durationHours = durationHours,
            totalAmount = parkingSlot.pricePerHour * durationHours,
            status = BookingStatus.CONFIRMED,
            vehicleNumber = vehicleNumber
        )
    }

    /**
     * Simulate payment processing
     * TODO: Replace with actual payment gateway integration
     */
    fun processPayment(amount: Double, cardNumber: String): Boolean {
        // Simulate payment processing delay
        Thread.sleep(2000)

        // Mock: All payments succeed for testing
        return true
    }

    /**
     * Validate user credentials
     * TODO: Replace with API call to POST /api/auth/login
     */
    fun validateLogin(email: String, password: String): Boolean {
        // Mock: Accept any email/password combination for testing
        return email.isNotEmpty() && password.length >= 6
    }

    /**
     * Register new user
     * TODO: Replace with API call to POST /api/auth/register
     */
    fun registerUser(name: String, email: String, password: String, phone: String): Boolean {
        // Mock: All registrations succeed for testing
        return name.isNotEmpty() && email.isNotEmpty() &&
                password.length >= 6 && phone.isNotEmpty()
    }
}