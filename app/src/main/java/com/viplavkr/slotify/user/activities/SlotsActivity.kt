package com.viplavkr.slotify.user.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.user.adapters.SlotsAdapter
import com.viplavkr.slotify.user.viewmodels.SlotsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Displays available parking slots for a chosen location and time range.
 * User selects a slot → locks it for 5 min → proceeds to PaymentActivity.
 *
 * Receives via Intent extras:
 *   - EXTRA_LOCATION (locationId)
 *   - location name (for display)
 */
class SlotsActivity : AppCompatActivity() {

    private lateinit var viewModel: SlotsViewModel
    private lateinit var authManager: AuthManager
    private lateinit var slotsAdapter: SlotsAdapter

    // Views
    private lateinit var rvSlots: RecyclerView
    private lateinit var tvLocationName: TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var btnPickStart: View
    private lateinit var btnPickEnd: View
    private lateinit var btnProceed: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoSlots: TextView
    private lateinit var ivBack: ImageView

    private var locationId: String = ""
    private var locationName: String = ""
    private var selectedSlot: ParkingSlot? = null

    // Default: next hour, 2-hour duration
    private var startTime: Long = 0L
    private var endTime: Long = 0L

    private val dateTimeFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slots)

        authManager = AuthManager(this)
        viewModel = ViewModelProvider(this)[SlotsViewModel::class.java]

        locationId = intent.getStringExtra(Constants.EXTRA_LOCATION) ?: ""
        locationName = intent.getStringExtra("location_name") ?: "Parking"

        // Set default times: start = next hour, end = start + 2 hours
        val cal = Calendar.getInstance()
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.add(Calendar.HOUR_OF_DAY, 1)
        startTime = cal.timeInMillis
        cal.add(Calendar.HOUR_OF_DAY, 2)
        endTime = cal.timeInMillis

        initViews()
        setupRecyclerView()
        observeViewModel()
        loadSlots()
    }

    private fun initViews() {
        rvSlots = findViewById(R.id.rvSlots)
        tvLocationName = findViewById(R.id.tvLocationName)
        tvStartTime = findViewById(R.id.tvStartTime)
        tvEndTime = findViewById(R.id.tvEndTime)
        btnPickStart = findViewById(R.id.btnPickStart)
        btnPickEnd = findViewById(R.id.btnPickEnd)
        btnProceed = findViewById(R.id.btnProceed)
        progressBar = findViewById(R.id.progressBar)
        tvNoSlots = findViewById(R.id.tvNoSlots)
        ivBack = findViewById(R.id.ivBack)

        tvLocationName.text = locationName
        updateTimeDisplay()

        btnPickStart.setOnClickListener { pickDateTime(isStart = true) }
        btnPickEnd.setOnClickListener { pickDateTime(isStart = false) }
        btnProceed.setOnClickListener { proceedToPayment() }
        ivBack.setOnClickListener { onBackPressed() }

        btnProceed.isEnabled = false
        btnProceed.alpha = 0.5f
    }

    private fun setupRecyclerView() {
        slotsAdapter = SlotsAdapter { slot ->
            onSlotTapped(slot)
        }
        rvSlots.layoutManager = GridLayoutManager(this, 2)
        rvSlots.adapter = slotsAdapter
    }

    private fun observeViewModel() {
        viewModel.slots.observe(this) { slots ->
            slotsAdapter.submitList(slots)
            tvNoSlots.visibility = if (slots.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.unavailableSlotIds.observe(this) { ids ->
            slotsAdapter.setUnavailableSlots(ids)
        }

        viewModel.lockedBooking.observe(this) { booking ->
            if (booking != null) {
                // Successfully locked — go to payment
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOKING, booking)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                selectedSlot = null
                slotsAdapter.setSelectedSlot(null)
                btnProceed.isEnabled = false
                btnProceed.alpha = 0.5f
                viewModel.clearError()
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun loadSlots() {
        viewModel.loadSlots(locationId, startTime, endTime)
    }

    private fun onSlotTapped(slot: ParkingSlot) {
        selectedSlot = slot
        slotsAdapter.setSelectedSlot(slot.id)
        btnProceed.isEnabled = true
        btnProceed.alpha = 1.0f

        val hours = ((endTime - startTime + 3_599_999) / 3_600_000).toInt()
        val total = slot.pricePerHour * hours
        btnProceed.text = "Book ${slot.slotNumber} — ₹${total.toInt()}"
    }

    private fun proceedToPayment() {
        val slot = selectedSlot ?: return
        val userId = authManager.getUserId() ?: return
        val userName = authManager.getUserName() ?: "User"

        viewModel.lockSlotForCheckout(
            userId = userId,
            userName = userName,
            slot = slot,
            locationName = locationName,
            startTime = startTime,
            endTime = endTime
        )
    }

    private fun pickDateTime(isStart: Boolean) {
        val cal = Calendar.getInstance()
        if (isStart) cal.timeInMillis = startTime else cal.timeInMillis = endTime

        DatePickerDialog(this, R.style.SlotifyDatePicker, { _, year, month, day ->
            cal.set(year, month, day)

            TimePickerDialog(this, R.style.SlotifyTimePicker, { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                if (isStart) {
                    startTime = cal.timeInMillis
                    // Auto-adjust end time if it's before start
                    if (endTime <= startTime) {
                        endTime = startTime + 2 * 3_600_000L
                    }
                } else {
                    if (cal.timeInMillis <= startTime) {
                        Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show()
                        return@TimePickerDialog
                    }
                    endTime = cal.timeInMillis
                }

                updateTimeDisplay()
                selectedSlot = null
                slotsAdapter.setSelectedSlot(null)
                btnProceed.isEnabled = false
                btnProceed.alpha = 0.5f
                btnProceed.text = "Select a Slot"
                loadSlots() // Re-check availability for new time range
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateTimeDisplay() {
        tvStartTime.text = dateTimeFormat.format(startTime)
        tvEndTime.text = dateTimeFormat.format(endTime)
    }

    override fun onResume() {
        super.onResume()
        // Refresh availability when returning from payment
        viewModel.refreshAvailability(locationId, startTime, endTime)
        // Reset selection state
        selectedSlot = null
        slotsAdapter.setSelectedSlot(null)
        btnProceed.isEnabled = false
        btnProceed.alpha = 0.5f
        btnProceed.text = "Select a Slot"
        viewModel.clearError()
    }

    override fun onBackPressed() {
        // Cancel any pending lock
        viewModel.cancelLock()
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
