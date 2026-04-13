package com.viplavkr.slotify.common.data

import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.models.BookingStatus
import com.viplavkr.slotify.common.models.Location
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.common.models.User
import com.viplavkr.slotify.common.models.Role

/**
 * Centralized mock data for parking locations, slots, and bookings.
 * Acts as the in-memory backend for the demo.
 */
object MockParkingRepository {

    // ───────────────── USERS (AUTH SYSTEM) ─────────────────
    private val users = listOf(
        User(
            id = "admin-001",
            name = "Admin User",
            email = "admin@slotify.com",
            role = Role.ADMIN,
            password = "Admin@123",
            phone = "9999999999"
        ),
        User(
            id = "user-001",
            name = "Viplav Kumar",
            email = "user@slotify.com",
            role = Role.USER,
            password = "User@123",
            phone = "8888888888"
        )
    )

    // ───────────────── LOGIN FUNCTION ─────────────────
    fun login(email: String, password: String): User? {
        return users.find {
            it.email == email && it.password == password
        }
    }

    // ── Locations ───────────────────────────────────────────────────

    private val locations = mutableListOf(
        Location(
            id = "loc-001",
            name = "Phoenix Mall Parking",
            address = "Whitefield Main Road, Whitefield",
            city = "Bangalore",
            totalSlots = 12,
            rating = 4.5f
        ),
        Location(
            id = "loc-002",
            name = "Orion Mall Basement",
            address = "Brigade Gateway, Rajajinagar",
            city = "Bangalore",
            totalSlots = 8,
            rating = 4.2f
        ),
        Location(
            id = "loc-003",
            name = "City Center Parking",
            address = "MG Road, Central Business District",
            city = "Bangalore",
            totalSlots = 10,
            rating = 3.8f
        )
    )

    // ── Parking Slots ───────────────────────────────────────────────

    private val slots = mutableListOf(
        ParkingSlot("slot-001", "loc-001", "A-01", "Ground", Constants.VEHICLE_COMPACT, 30.0),
        ParkingSlot("slot-002", "loc-001", "A-02", "Ground", Constants.VEHICLE_COMPACT, 30.0),
        ParkingSlot("slot-003", "loc-001", "A-03", "Ground", Constants.VEHICLE_STANDARD, 40.0),
        ParkingSlot("slot-004", "loc-001", "A-04", "Ground", Constants.VEHICLE_STANDARD, 40.0),
        ParkingSlot("slot-005", "loc-001", "B-01", "Level 1", Constants.VEHICLE_STANDARD, 40.0),
        ParkingSlot("slot-006", "loc-001", "B-02", "Level 1", Constants.VEHICLE_LARGE, 60.0),
        ParkingSlot("slot-007", "loc-001", "B-03", "Level 1", Constants.VEHICLE_LARGE, 60.0),
        ParkingSlot("slot-008", "loc-001", "C-01", "Level 2", Constants.VEHICLE_COMPACT, 25.0),
        ParkingSlot("slot-009", "loc-001", "C-02", "Level 2", Constants.VEHICLE_STANDARD, 35.0),
        ParkingSlot("slot-010", "loc-001", "C-03", "Level 2", Constants.VEHICLE_STANDARD, 35.0),
        ParkingSlot("slot-011", "loc-001", "C-04", "Level 2", Constants.VEHICLE_LARGE, 55.0),
        ParkingSlot("slot-012", "loc-001", "C-05", "Level 2", Constants.VEHICLE_COMPACT, 25.0),
        ParkingSlot("slot-013", "loc-002", "P-01", "Basement 1", Constants.VEHICLE_COMPACT, 35.0),
        ParkingSlot("slot-014", "loc-002", "P-02", "Basement 1", Constants.VEHICLE_STANDARD, 45.0),
        ParkingSlot("slot-015", "loc-002", "P-03", "Basement 1", Constants.VEHICLE_STANDARD, 45.0),
        ParkingSlot("slot-016", "loc-002", "P-04", "Basement 1", Constants.VEHICLE_LARGE, 65.0),
        ParkingSlot("slot-017", "loc-002", "P-05", "Basement 2", Constants.VEHICLE_COMPACT, 30.0),
        ParkingSlot("slot-018", "loc-002", "P-06", "Basement 2", Constants.VEHICLE_STANDARD, 40.0),
        ParkingSlot("slot-019", "loc-002", "P-07", "Basement 2", Constants.VEHICLE_LARGE, 60.0),
        ParkingSlot("slot-020", "loc-002", "P-08", "Basement 2", Constants.VEHICLE_COMPACT, 30.0),
        ParkingSlot("slot-021", "loc-003", "G-01", "Open Air", Constants.VEHICLE_COMPACT, 20.0),
        ParkingSlot("slot-022", "loc-003", "G-02", "Open Air", Constants.VEHICLE_COMPACT, 20.0),
        ParkingSlot("slot-023", "loc-003", "G-03", "Open Air", Constants.VEHICLE_STANDARD, 30.0),
        ParkingSlot("slot-024", "loc-003", "G-04", "Open Air", Constants.VEHICLE_STANDARD, 30.0),
        ParkingSlot("slot-025", "loc-003", "G-05", "Open Air", Constants.VEHICLE_LARGE, 45.0),
        ParkingSlot("slot-026", "loc-003", "G-06", "Covered", Constants.VEHICLE_COMPACT, 25.0),
        ParkingSlot("slot-027", "loc-003", "G-07", "Covered", Constants.VEHICLE_STANDARD, 35.0),
        ParkingSlot("slot-028", "loc-003", "G-08", "Covered", Constants.VEHICLE_STANDARD, 35.0),
        ParkingSlot("slot-029", "loc-003", "G-09", "Covered", Constants.VEHICLE_LARGE, 50.0),
        ParkingSlot("slot-030", "loc-003", "G-10", "Covered", Constants.VEHICLE_LARGE, 50.0)
    )

    // ── Bookings ────────────────────────────────────────────────────

    private val bookings = mutableListOf<Booking>()

    init {
        val now = System.currentTimeMillis()
        val oneHour = 3_600_000L

        bookings.add(
            Booking(
                id = "bk-demo-001",
                userId = "user-001",
                userName = "Viplav Kumar",
                slotId = "slot-003",
                slotNumber = "A-03",
                locationId = "loc-001",
                locationName = "Phoenix Mall Parking",
                vehicleType = Constants.VEHICLE_STANDARD,
                startTime = now - oneHour,
                endTime = now + 2 * oneHour,
                totalAmount = 120.0,
                status = BookingStatus.CONFIRMED,
                confirmedAt = now - oneHour,
                paymentMethod = "UPI",
                transactionId = "TXN${System.currentTimeMillis()}"
            )
        )

        bookings.add(
            Booking(
                id = "bk-demo-002",
                userId = "user-001",
                userName = "Viplav Kumar",
                slotId = "slot-014",
                slotNumber = "P-02",
                locationId = "loc-002",
                locationName = "Orion Mall Basement",
                vehicleType = Constants.VEHICLE_STANDARD,
                startTime = now - 5 * oneHour,
                endTime = now - 3 * oneHour,
                totalAmount = 90.0,
                status = BookingStatus.COMPLETED,
                confirmedAt = now - 5 * oneHour,
                completedAt = now - 3 * oneHour,
                paymentMethod = "CARD",
                transactionId = "TXN${System.currentTimeMillis() - 100}"
            )
        )

        bookings.add(
            Booking(
                id = "bk-demo-003",
                userId = "user-002",
                userName = "Priya Sharma",
                slotId = "slot-005",
                slotNumber = "B-01",
                locationId = "loc-001",
                locationName = "Phoenix Mall Parking",
                vehicleType = Constants.VEHICLE_STANDARD,
                startTime = now,
                endTime = now + 3 * oneHour,
                totalAmount = 120.0,
                status = BookingStatus.CONFIRMED,
                confirmedAt = now,
                paymentMethod = "UPI",
                transactionId = "TXN${System.currentTimeMillis() - 50}"
            )
        )
    }

    // ── Location Queries ────────────────────────────────────────────

    fun getAllLocations(): List<Location> = locations.toList()

    fun getLocationById(id: String): Location? = locations.find { it.id == id }

    // ── Slot Queries ────────────────────────────────────────────────

    fun getSlotsByLocation(locationId: String): List<ParkingSlot> =
        slots.filter { it.locationId == locationId && it.isActive }

    fun getSlotById(id: String): ParkingSlot? = slots.find { it.id == id }

    fun getAllSlots(): List<ParkingSlot> = slots.toList()

    // ── Booking CRUD ────────────────────────────────────────────────

    fun getAllBookings(): List<Booking> = bookings.toList()

    fun getBookingById(id: String): Booking? = bookings.find { it.id == id }

    fun getBookingsByUser(userId: String): List<Booking> =
        bookings.filter { it.userId == userId }.sortedByDescending { it.createdAt }

    fun getActiveBookings(): List<Booking> =
        bookings.filter { it.status in listOf(BookingStatus.CONFIRMED, BookingStatus.ACTIVE, BookingStatus.LOCKED) }

    fun getBookingsByLocation(locationId: String): List<Booking> =
        bookings.filter { it.locationId == locationId }

    fun isSlotAvailable(slotId: String, startTime: Long, endTime: Long): Boolean {
        releaseExpiredLocks()
        val conflicting = bookings.any { booking ->
            booking.slotId == slotId &&
                    booking.status in listOf(BookingStatus.LOCKED, BookingStatus.CONFIRMED, BookingStatus.ACTIVE) &&
                    booking.startTime < endTime && startTime < booking.endTime
        }
        return !conflicting
    }

    fun getUnavailableSlotIds(locationId: String, startTime: Long, endTime: Long): Set<String> {
        releaseExpiredLocks()
        return bookings.filter { booking ->
            booking.locationId == locationId &&
                    booking.status in listOf(BookingStatus.LOCKED, BookingStatus.CONFIRMED, BookingStatus.ACTIVE) &&
                    booking.startTime < endTime && startTime < booking.endTime
        }.map { it.slotId }.toSet()
    }

    fun lockSlot(
        userId: String,
        userName: String,
        slot: ParkingSlot,
        locationName: String,
        startTime: Long,
        endTime: Long,
        totalAmount: Double
    ): Booking? {
        if (!isSlotAvailable(slot.id, startTime, endTime)) return null
        val booking = Booking(
            userId = userId,
            userName = userName,
            slotId = slot.id,
            slotNumber = slot.slotNumber,
            locationId = slot.locationId,
            locationName = locationName,
            vehicleType = slot.vehicleType,
            startTime = startTime,
            endTime = endTime,
            totalAmount = totalAmount,
            status = BookingStatus.LOCKED,
            lockedAt = System.currentTimeMillis()
        )
        bookings.add(booking)
        return booking
    }

    fun confirmBooking(bookingId: String, paymentMethod: String, transactionId: String): Boolean {
        val booking = bookings.find { it.id == bookingId } ?: return false

        // ❌ Prevent confirming cancelled booking
        if (booking.status == BookingStatus.CANCELLED) return false

        val index = bookings.indexOf(booking)

        bookings[index] = booking.copy(
            status = BookingStatus.CONFIRMED,
            confirmedAt = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            transactionId = transactionId
        )

        return true
    }

    fun completeBooking(bookingId: String): Boolean {
        val booking = bookings.find { it.id == bookingId } ?: return false
        if (booking.status !in listOf(BookingStatus.CONFIRMED, BookingStatus.ACTIVE)) return false
        val index = bookings.indexOf(booking)
        bookings[index] = booking.copy(
            status = BookingStatus.COMPLETED,
            completedAt = System.currentTimeMillis()
        )
        return true
    }

    fun cancelBooking(bookingId: String): Boolean {
        val booking = bookings.find { it.id == bookingId } ?: return false
        if (booking.status in listOf(BookingStatus.COMPLETED, BookingStatus.CANCELLED)) return false
        val index = bookings.indexOf(booking)
        bookings[index] = booking.copy(status = BookingStatus.CANCELLED)
        return true
    }

    fun extendBooking(bookingId: String, additionalHours: Int): Result<Booking> {
        val booking = bookings.find { it.id == bookingId }
            ?: return Result.failure(Exception("Booking not found"))

        if (booking.status !in listOf(BookingStatus.CONFIRMED, BookingStatus.ACTIVE)) {
            return Result.failure(Exception("Only active bookings can be extended"))
        }

        val newEndTime = booking.endTime + (additionalHours * 3_600_000L)

        val conflicts = bookings.any { other ->
            other.id != bookingId &&
                    other.slotId == booking.slotId &&
                    other.status in listOf(BookingStatus.LOCKED, BookingStatus.CONFIRMED, BookingStatus.ACTIVE) &&
                    other.startTime < newEndTime && booking.endTime < other.endTime
        }
        if (conflicts) {
            return Result.failure(Exception("Cannot extend - slot is booked by someone else after your current end time"))
        }

        val slot = getSlotById(booking.slotId)
        val extensionCost = (slot?.pricePerHour ?: 40.0) * additionalHours + Constants.EXTENSION_SURCHARGE

        val index = bookings.indexOf(booking)
        val updated = booking.copy(
            endTime = newEndTime,
            totalAmount = booking.totalAmount + extensionCost
        )
        bookings[index] = updated
        return Result.success(updated)
    }

    private fun releaseExpiredLocks() {
        val now = System.currentTimeMillis()
        bookings.forEachIndexed { index, booking ->
            if (
                booking.status == BookingStatus.LOCKED &&
                booking.lockedAt != null &&
                booking.confirmedAt == null
            ) {
                if (now - booking.lockedAt > Constants.SLOT_LOCK_DURATION_MS + 5000){
                    bookings[index] = booking.copy(status = BookingStatus.CANCELLED)
                }
            }
        }
    }

    // ── Stats ───────────────────────────────────────────────────────

    fun getTotalRevenue(): Double =
        bookings.filter { it.status in listOf(BookingStatus.CONFIRMED, BookingStatus.ACTIVE, BookingStatus.COMPLETED) }
            .sumOf { it.totalAmount }

    fun getTodayBookingsCount(): Int {
        val todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % 86_400_000)
        return bookings.count { it.createdAt >= todayStart }
    }

    fun getActiveBookingsCount(): Int =
        bookings.count { it.status in listOf(BookingStatus.CONFIRMED, BookingStatus.ACTIVE) }

    fun getOccupancyRate(locationId: String): Float {
        val totalSlots = slots.count { it.locationId == locationId }
        if (totalSlots == 0) return 0f
        val occupied = getActiveBookings().count {
            it.locationId == locationId && it.isCurrentlyActive()
        }
        return (occupied.toFloat() / totalSlots) * 100
    }
}
