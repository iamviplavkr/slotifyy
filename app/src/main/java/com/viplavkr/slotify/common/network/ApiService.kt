package com.viplavkr.slotify.common.network

import com.viplavkr.slotify.common.models.*
import retrofit2.Response
import retrofit2.http.*

// Request/Response Models
data class LoginRequest(val phone: String, val otp: String)
data class LoginResponse(val token: String, val user: User)
data class SignupRequest(val name: String, val phone: String, val email: String)
data class OtpResponse(val message: String, val success: Boolean)
data class CreateBookingRequest(
    val slotId: String,
    val duration: Int,
    val paymentMethod: String
)
data class CreateLocationRequest(
    val name: String,
    val address: String,
    val city: String,
    val pricePerHour: Double,
    val openTime: String,
    val closeTime: String
)
data class CreateSlotRequest(
    val locationId: String,
    val slotNumber: String,
    val level: String,
    val type: String,
    val pricePerHour: Double
)
data class BulkCreateSlotsRequest(
    val locationId: String,
    val level: String,
    val type: String,
    val pricePerHour: Double,
    val startNumber: Int,
    val count: Int
)
data class UpdateStatusRequest(val isActive: Boolean)
data class RevenueResponse(
    val totalRevenue: Double,
    val todayRevenue: Double,
    val weekRevenue: Double,
    val monthRevenue: Double,
    val totalBookings: Int,
    val activeBookings: Int
)

interface ApiService {

    // Auth Endpoints
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: Map<String, String>): Response<OtpResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<LoginResponse>

    // User Endpoints
    @GET("slots")
    suspend fun getSlots(@Query("locationId") locationId: String? = null): Response<List<ParkingSlot>>

    @GET("slots/{id}")
    suspend fun getSlotById(@Path("id") slotId: String): Response<ParkingSlot>

    @POST("bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<Booking>

    @GET("bookings/my")
    suspend fun getMyBookings(): Response<List<Booking>>

    @GET("profile")
    suspend fun getProfile(): Response<User>

    @PUT("profile")
    suspend fun updateProfile(@Body user: Map<String, String>): Response<User>

    @GET("locations")
    suspend fun getLocations(): Response<List<Location>>

    // Admin Endpoints
    @GET("admin/locations")
    suspend fun getAdminLocations(): Response<List<Location>>

    @POST("admin/locations")
    suspend fun createLocation(@Body request: CreateLocationRequest): Response<Location>

    @PUT("admin/locations/{id}")
    suspend fun updateLocation(
        @Path("id") locationId: String,
        @Body request: CreateLocationRequest
    ): Response<Location>

    @PUT("admin/locations/{id}/status")
    suspend fun updateLocationStatus(
        @Path("id") locationId: String,
        @Body request: UpdateStatusRequest
    ): Response<Location>

    @GET("admin/slots")
    suspend fun getAdminSlots(@Query("locationId") locationId: String? = null): Response<List<ParkingSlot>>

    @POST("admin/slots")
    suspend fun createSlot(@Body request: CreateSlotRequest): Response<ParkingSlot>

    @POST("admin/slots/bulk")
    suspend fun bulkCreateSlots(@Body request: BulkCreateSlotsRequest): Response<List<ParkingSlot>>

    @PUT("admin/slots/{id}")
    suspend fun updateSlot(
        @Path("id") slotId: String,
        @Body request: CreateSlotRequest
    ): Response<ParkingSlot>

    @PUT("admin/slots/{id}/status")
    suspend fun updateSlotStatus(
        @Path("id") slotId: String,
        @Body request: UpdateStatusRequest
    ): Response<ParkingSlot>

    @GET("admin/bookings")
    suspend fun getAdminBookings(
        @Query("status") status: String? = null,
        @Query("locationId") locationId: String? = null
    ): Response<List<Booking>>

    @GET("admin/revenue")
    suspend fun getRevenue(): Response<RevenueResponse>

    @GET("admin/users")
    suspend fun getUsers(): Response<List<User>>

    @PUT("admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") userId: String,
        @Body request: UpdateStatusRequest
    ): Response<User>

    @GET("admin/users/{id}")
    suspend fun getUserDetails(@Path("id") userId: String): Response<User>
}