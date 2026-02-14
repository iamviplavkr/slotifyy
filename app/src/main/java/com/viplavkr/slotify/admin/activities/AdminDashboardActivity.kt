package com.viplavkr.slotify.admin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.viplavkr.slotify.R
import com.viplavkr.slotify.activities.LoginActivity
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.databinding.ActivityAdminDashboardBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupDashboardStats()
        setupMenuItems()
        loadDashboardData()
    }

    private fun setupHeader() {
        val user = AuthManager.getUser()
        binding.tvAdminName.text = user?.name ?: "Admin"
        binding.tvAdminEmail.text = user?.email ?: "admin@slotify.com"

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun setupDashboardStats() {
        // Initial loading state
        binding.tvTotalLocations.text = "--"
        binding.tvTotalSlots.text = "--"
        binding.tvActiveBookings.text = "--"
        binding.tvTodayRevenue.text = "--"
    }

    private fun setupMenuItems() {
        // Locations Management
        binding.cardLocations.setOnClickListener {
            startActivity(Intent(this, LocationsManagementActivity::class.java))
        }

        // Slots Management
        binding.cardSlots.setOnClickListener {
            startActivity(Intent(this, SlotsManagementActivity::class.java))
        }

        // Bookings View
        binding.cardBookings.setOnClickListener {
            startActivity(Intent(this, BookingsViewActivity::class.java))
        }

        // Revenue
        binding.cardRevenue.setOnClickListener {
            startActivity(Intent(this, RevenueActivity::class.java))
        }

        // Users Management
        binding.cardUsers.setOnClickListener {
            startActivity(Intent(this, UsersManagementActivity::class.java))
        }
    }

    private fun loadDashboardData() {
        lifecycleScope.launch {
            // Simulate loading
            delay(500)

            // Mock dashboard data
            binding.tvTotalLocations.text = "5"
            binding.tvTotalSlots.text = "248"
            binding.tvActiveBookings.text = "42"
            binding.tvTodayRevenue.text = "₹12,450"
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        AuthManager.logout()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to dashboard
        loadDashboardData()
    }
}