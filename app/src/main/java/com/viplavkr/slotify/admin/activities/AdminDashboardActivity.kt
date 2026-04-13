package com.viplavkr.slotify.admin.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.activities.LoginActivity
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.data.MockDataRepository
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.user.activities.ScannerActivity

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var tvAdminName: TextView
    private lateinit var tvTotalBookings: TextView
    private lateinit var tvActiveBookings: TextView
    private lateinit var tvTotalRevenue: TextView
    private lateinit var tvTotalUsers: TextView
    private lateinit var tvTodayBookings: TextView

    private lateinit var cardBookings: CardView
    private lateinit var cardSlots: CardView
    private lateinit var cardUsers: CardView
    private lateinit var cardLocations: CardView
    private lateinit var cardRevenue: CardView
    private lateinit var cardScanner: CardView
    private lateinit var cardLogout: CardView

    private val refreshRunnable = object : Runnable {
        override fun run() {
            updateStats()
            handler.postDelayed(this, 10000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        authManager = AuthManager(this)

        initViews()
        setupNavigation()
        updateStats()
    }

    private fun initViews() {
        tvAdminName = findViewById(R.id.tvAdminName)
        tvTotalBookings = findViewById(R.id.tvTotalBookings)
        tvActiveBookings = findViewById(R.id.tvActiveBookings)
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue)
        tvTotalUsers = findViewById(R.id.tvTotalUsers)
        tvTodayBookings = findViewById(R.id.tvTodayBookings)

        cardBookings = findViewById(R.id.cardBookings)
        cardSlots = findViewById(R.id.cardSlots)
        cardUsers = findViewById(R.id.cardUsers)
        cardLocations = findViewById(R.id.cardLocations)
        cardRevenue = findViewById(R.id.cardRevenue)
        cardScanner = findViewById(R.id.cardScanner) // ✅ FIXED
        cardLogout = findViewById(R.id.cardLogout)

        tvAdminName.text = "Hello, ${authManager.getUserName() ?: "Admin"}"
    }

    private fun setupNavigation() {
        cardBookings.setOnClickListener {
            startActivity(Intent(this, BookingsViewActivity::class.java))
        }
        cardSlots.setOnClickListener {
            startActivity(Intent(this, SlotsManagementActivity::class.java))
        }
        cardUsers.setOnClickListener {
            startActivity(Intent(this, UsersManagementActivity::class.java))
        }
        cardLocations.setOnClickListener {
            startActivity(Intent(this, LocationsManagementActivity::class.java))
        }
        cardRevenue.setOnClickListener {
            startActivity(Intent(this, RevenueActivity::class.java))
        }
        cardScanner.setOnClickListener {
            startActivity(Intent(this, ScannerActivity::class.java))
        }
        cardLogout.setOnClickListener {
            authManager.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun updateStats() {
        val bookings = MockParkingRepository.getAllBookings()
        tvTotalBookings.text = bookings.size.toString()
        tvActiveBookings.text = MockParkingRepository.getActiveBookingsCount().toString()
        tvTotalRevenue.text = "₹${MockParkingRepository.getTotalRevenue().toInt()}"
        tvTotalUsers.text = MockDataRepository.getActiveUserCount().toString()
        tvTodayBookings.text = MockParkingRepository.getTodayBookingsCount().toString()
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }
}