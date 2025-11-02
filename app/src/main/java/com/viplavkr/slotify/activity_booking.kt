package com.viplavkr.slotify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.data.MockData
import com.viplavkr.slotify.models.Booking
import com.viplavkr.slotify.models.ParkingSlot
import com.viplavkr.slotify.utils.Constants


class BookingActivity : AppCompatActivity() {

    private lateinit var parkingSlot: ParkingSlot

    private lateinit var slotNameText: TextView
    private lateinit var slotAddressText: TextView
    private lateinit var slotDistanceText: TextView
    private lateinit var slotPriceText: TextView
    private lateinit var availableSlotsText: TextView
    private lateinit var durationGroup: RadioGroup
    private lateinit var vehicleNumberInput: EditText
    private lateinit var totalAmountText: TextView
    private lateinit var proceedButton: Button
    private lateinit var backButton: Button

    private var selectedDuration: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)


        supportActionBar?.hide()


        parkingSlot = intent.getSerializableExtra(Constants.EXTRA_PARKING_SLOT) as? ParkingSlot
            ?: run {
                Toast.makeText(this, "Error loading parking slot", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        initializeViews()
        displaySlotDetails()
        setupDurationSelection()
        setupClickListeners()
        loadSavedVehicleNumber()
    }


    private fun initializeViews() {
        slotNameText = findViewById(R.id.slotNameText)
        slotAddressText = findViewById(R.id.slotAddressText)
        slotDistanceText = findViewById(R.id.slotDistanceText)
        slotPriceText = findViewById(R.id.slotPriceText)
        availableSlotsText = findViewById(R.id.availableSlotsText)
        durationGroup = findViewById(R.id.durationGroup)
        vehicleNumberInput = findViewById(R.id.vehicleNumberInput)
        totalAmountText = findViewById(R.id.totalAmountText)
        proceedButton = findViewById(R.id.proceedButton)
        backButton = findViewById(R.id.backButton)
    }


    private fun displaySlotDetails() {
        slotNameText.text = parkingSlot.name
        slotAddressText.text = parkingSlot.address
        slotDistanceText.text = parkingSlot.getFormattedDistance()


        slotPriceText.text = String.format("Rs %.2f/hr", parkingSlot.pricePerHour)

        availableSlotsText.text = "${parkingSlot.availableSlots} slots available"

        updateTotalAmount()
    }


    private fun setupDurationSelection() {
        Constants.DURATION_OPTIONS.forEach { hours ->
            val radioButton = RadioButton(this).apply {
                text = "$hours ${if (hours == 1) "hour" else "hours"}"
                id = hours
                setTextColor(resources.getColor(R.color.text_primary, null))
            }
            durationGroup.addView(radioButton)
        }


        durationGroup.check(1)

        durationGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedDuration = checkedId
            updateTotalAmount()
        }
    }


    private fun setupClickListeners() {
        proceedButton.setOnClickListener {
            handleProceedToPayment()
        }

        backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }


    private fun loadSavedVehicleNumber() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val savedVehicleNumber = prefs.getString(Constants.KEY_VEHICLE_NUMBER, "")
        vehicleNumberInput.setText(savedVehicleNumber)
    }


    private fun updateTotalAmount() {
        val totalAmount = parkingSlot.pricePerHour * selectedDuration


        totalAmountText.text = String.format("Total: Rs %.2f", totalAmount)
    }


    private fun handleProceedToPayment() {
        val vehicleNumber = vehicleNumberInput.text.toString().trim()


        if (vehicleNumber.isEmpty()) {
            vehicleNumberInput.error = "Vehicle number is required"
            return
        }


        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putString(Constants.KEY_VEHICLE_NUMBER, vehicleNumber).apply()


        val booking = MockData.createMockBooking(parkingSlot, selectedDuration, vehicleNumber)


        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(Constants.EXTRA_BOOKING, booking)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
