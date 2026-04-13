package com.viplavkr.slotify.admin.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.BookingStatus

/**
 * Revenue overview screen for admin.
 * Shows total revenue, breakdown by location, and booking counts.
 */
class RevenueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revenue)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvTotalRevenue = findViewById<TextView>(R.id.tvTotalRevenue)
        val tvTotalBookings = findViewById<TextView>(R.id.tvRevenueBookings)
        val tvAvgBooking = findViewById<TextView>(R.id.tvAvgBooking)

        ivBack.setOnClickListener { finish(); overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) }
        val bookings = MockParkingRepository.getAllBookings()
            .filter {
                it.status in listOf(
                    BookingStatus.CONFIRMED,
                    BookingStatus.ACTIVE,
                    BookingStatus.COMPLETED
                )
            }
        val totalRevenue = bookings.sumOf { it.totalAmount }
                val avgBooking = if (bookings.isNotEmpty()) totalRevenue / bookings.size else 0.0

                tvTotalRevenue.text = "₹${totalRevenue.toInt()}"
                tvTotalBookings.text = "${bookings.size} bookings"
                tvAvgBooking.text = "Avg: ₹${avgBooking.toInt()}/booking"

                // Per-location breakdown
                val locations = MockParkingRepository.getAllLocations()
                val layoutBreakdown = findViewById<android.widget.LinearLayout>(R.id.layoutBreakdown)

                locations.forEach { loc ->
                    val locBookings = bookings.filter { it.locationId == loc.id }
                    val locRevenue = locBookings.sumOf { it.totalAmount }
                    val occupancy = MockParkingRepository.getOccupancyRate(loc.id)

                    val tv = TextView(this).apply {
                        text = "${loc.name}: ₹${locRevenue.toInt()} (${locBookings.size} bookings, ${occupancy.toInt()}% occupied)"
                        setTextColor(getColor(R.color.text_secondary))
                        textSize = 13f
                        setPadding(0, 12, 0, 0)
                    }
                    layoutBreakdown.addView(tv)
                }
            }
    }
