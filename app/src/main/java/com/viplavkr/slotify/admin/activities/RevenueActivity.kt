package com.viplavkr.slotify.admin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.viplavkr.slotify.common.utils.gone
import com.viplavkr.slotify.common.utils.visible
import com.viplavkr.slotify.databinding.ActivityRevenueBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RevenueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRevenueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRevenueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadRevenueData()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadRevenueData() {
        lifecycleScope.launch {
            binding.progressBar.visible()
            delay(500)

            // Mock revenue data
            binding.tvTotalRevenue.text = "₹2,45,680"
            binding.tvTodayRevenue.text = "₹12,450"
            binding.tvWeekRevenue.text = "₹78,320"
            binding.tvMonthRevenue.text = "₹2,45,680"

            binding.tvTotalBookings.text = "1,248"
            binding.tvActiveBookings.text = "42"
            binding.tvCompletedBookings.text = "1,186"
            binding.tvCancelledBookings.text = "20"

            binding.tvAvgBookingValue.text = "₹197"
            binding.tvAvgDuration.text = "2.5 hrs"
            binding.tvPeakHour.text = "10 AM - 12 PM"
            binding.tvBusiestDay.text = "Saturday"

            // Top locations
            binding.tvLocation1Name.text = "Mall Parking"
            binding.tvLocation1Revenue.text = "₹98,450"
            binding.tvLocation2Name.text = "Tech Park"
            binding.tvLocation2Revenue.text = "₹85,230"
            binding.tvLocation3Name.text = "Central Plaza"
            binding.tvLocation3Revenue.text = "₹62,000"

            binding.progressBar.gone()
        }
    }
}