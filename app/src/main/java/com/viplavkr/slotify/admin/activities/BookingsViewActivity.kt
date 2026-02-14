package com.viplavkr.slotify.admin.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.models.BookingStatus
import com.viplavkr.slotify.common.utils.gone
import com.viplavkr.slotify.common.utils.visible
import com.viplavkr.slotify.databinding.ActivityBookingsViewBinding
import com.viplavkr.slotify.admin.adapters.AdminBookingsAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BookingsViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingsViewBinding
    private lateinit var adapter: AdminBookingsAdapter
    private var allBookings = listOf<Booking>()
    private var selectedStatus: BookingStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingsViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupStatusFilter()
        setupRecyclerView()
        loadBookings()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupStatusFilter() {
        val statuses = listOf("All Bookings", "Active", "Completed", "Cancelled")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = spinnerAdapter

        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedStatus = when (position) {
                    1 -> BookingStatus.ACTIVE
                    2 -> BookingStatus.COMPLETED
                    3 -> BookingStatus.CANCELLED
                    else -> null
                }
                filterBookings()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminBookingsAdapter { booking ->
            // Show booking details
        }

        binding.rvBookings.apply {
            layoutManager = LinearLayoutManager(this@BookingsViewActivity)
            adapter = this@BookingsViewActivity.adapter
        }
    }

    private fun loadBookings() {
        lifecycleScope.launch {
            binding.progressBar.visible()
            delay(500)

            allBookings = generateMockBookings()

            binding.progressBar.gone()
            filterBookings()
        }
    }

    private fun generateMockBookings(): List<Booking> {
        return listOf(
            Booking("1", "BK10001", "u1", "John Doe", "+919876543210", "s1", "A-01", "A", "Mall Parking", "15 Jun, 10:00 AM", "15 Jun, 12:00 PM", 2, 100.0, BookingStatus.ACTIVE, "CARD", "15 Jun 2025"),
            Booking("2", "BK10002", "u2", "Jane Smith", "+919876543211", "s5", "A-05", "A", "Mall Parking", "15 Jun, 09:00 AM", "15 Jun, 11:00 AM", 2, 140.0, BookingStatus.ACTIVE, "UPI", "15 Jun 2025"),
            Booking("3", "BK10003", "u3", "Bob Wilson", "+919876543212", "s10", "B-05", "B", "Central Plaza", "14 Jun, 02:00 PM", "14 Jun, 05:00 PM", 3, 180.0, BookingStatus.COMPLETED, "CARD", "14 Jun 2025"),
            Booking("4", "BK10004", "u4", "Alice Brown", "+919876543213", "s15", "P1-05", "P1", "Tech Park", "14 Jun, 10:00 AM", "14 Jun, 06:00 PM", 8, 440.0, BookingStatus.COMPLETED, "CARD", "14 Jun 2025"),
            Booking("5", "BK10005", "u1", "John Doe", "+919876543210", "s20", "P2-05", "P2", "Tech Park", "13 Jun, 11:00 AM", "13 Jun, 01:00 PM", 2, 110.0, BookingStatus.COMPLETED, "UPI", "13 Jun 2025"),
            Booking("6", "BK10006", "u5", "Charlie Davis", "+919876543214", "s2", "A-02", "A", "Mall Parking", "12 Jun, 03:00 PM", "12 Jun, 04:00 PM", 1, 50.0, BookingStatus.CANCELLED, "CARD", "12 Jun 2025")
        )
    }

    private fun filterBookings() {
        val filtered = if (selectedStatus == null) {
            allBookings
        } else {
            allBookings.filter { it.status == selectedStatus }
        }

        adapter.updateBookings(filtered)
        binding.tvBookingsCount.text = "${filtered.size} Bookings"

        // Calculate stats
        val active = allBookings.count { it.status == BookingStatus.ACTIVE }
        val completed = allBookings.count { it.status == BookingStatus.COMPLETED }
        val totalRevenue = allBookings.filter { it.status == BookingStatus.COMPLETED }.sumOf { it.totalAmount }

        binding.tvActiveCount.text = active.toString()
        binding.tvCompletedCount.text = completed.toString()
        binding.tvTotalRevenue.text = "₹${totalRevenue.toInt()}"

        if (filtered.isEmpty()) {
            binding.emptyState.visible()
            binding.rvBookings.gone()
        } else {
            binding.emptyState.gone()
            binding.rvBookings.visible()
        }
    }
}