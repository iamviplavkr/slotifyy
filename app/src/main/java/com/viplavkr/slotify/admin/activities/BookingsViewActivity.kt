package com.viplavkr.slotify.admin.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.admin.adapters.AdminBookingsAdapter
import com.viplavkr.slotify.common.data.MockParkingRepository

/**
 * Admin view of all bookings across all locations.
 * Sorted by most recent. Admin can filter by status.
 */
class BookingsViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val rvList = findViewById<RecyclerView>(R.id.rvList)

        tvTitle.text = "All Bookings"
        ivBack.setOnClickListener { finish(); overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) }

        val bookings = MockParkingRepository.getAllBookings().sortedByDescending { it.createdAt }
        val adapter = AdminBookingsAdapter(bookings) { booking ->
            MockParkingRepository.completeBooking(booking.id)
            // Refresh
            recreate()
        }
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter
    }
}
