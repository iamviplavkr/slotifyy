package com.viplavkr.slotify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.adapters.ParkingSlotAdapter
import com.viplavkr.slotify.data.MockData
import com.viplavkr.slotify.models.ParkingSlot
import com.viplavkr.slotify.utils.Constants

/**
 * Home Activity
 * Main screen showing nearby parking slots and search functionality
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var parkingSlotsRecyclerView: RecyclerView
    private lateinit var logoutButton: Button

    private lateinit var parkingSlotAdapter: ParkingSlotAdapter
    private var parkingSlots: List<ParkingSlot> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Hide action bar
        supportActionBar?.hide()

        initializeViews()
        setupRecyclerView()
        loadParkingSlots()
        setupClickListeners()
        displayWelcomeMessage()
    }

    /** Initialize view references */
    private fun initializeViews() {
        welcomeText = findViewById(R.id.welcomeText)
        searchInput = findViewById(R.id.searchInput)
        searchButton = findViewById(R.id.searchButton)
        parkingSlotsRecyclerView = findViewById(R.id.parkingSlotsRecyclerView)
        logoutButton = findViewById(R.id.logoutButton)
    }

    /** Setup RecyclerView for parking slots */
    private fun setupRecyclerView() {
        parkingSlotAdapter = ParkingSlotAdapter { parkingSlot ->
            navigateToBooking(parkingSlot)
        }

        parkingSlotsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = parkingSlotAdapter
        }
    }

    /** Load parking slots (MockData for now) */
    private fun loadParkingSlots() {
        Thread {
            parkingSlots = MockData.getMockParkingSlots()

            runOnUiThread {
                parkingSlotAdapter.submitList(parkingSlots)
            }
        }.start()
    }

    /** Setup click listeners */
    private fun setupClickListeners() {
        searchButton.setOnClickListener {
            handleSearch()
        }

        logoutButton.setOnClickListener {
            handleLogout()
        }
    }

    /** Display welcome message with user name */
    private fun displayWelcomeMessage() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val userName = prefs.getString(Constants.KEY_USER_NAME, "User")
        welcomeText.text = "Welcome, $userName!"
    }

    /** Handle search functionality */
    private fun handleSearch() {
        val query = searchInput.text.toString().trim()

        if (query.isEmpty()) {
            loadParkingSlots()
            return
        }

        Thread {
            val filteredSlots = MockData.searchParkingSlots(query)

            runOnUiThread {
                if (filteredSlots.isEmpty()) {
                    Toast.makeText(this, "No parking slots found", Toast.LENGTH_SHORT).show()
                    parkingSlotAdapter.submitList(emptyList())
                } else {
                    parkingSlotAdapter.submitList(filteredSlots)
                }
            }
        }.start()
    }

    /** Navigate to booking screen */
    private fun navigateToBooking(parkingSlot: ParkingSlot) {
        if (!parkingSlot.isAvailable()) {
            Toast.makeText(this, "No slots available at this location", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, BookingActivity::class.java)
        intent.putExtra(Constants.EXTRA_PARKING_SLOT, parkingSlot)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /** Handle logout */
    private fun handleLogout() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onBackPressed() {
        // Exit or minimize app
        finish()
    }
}
