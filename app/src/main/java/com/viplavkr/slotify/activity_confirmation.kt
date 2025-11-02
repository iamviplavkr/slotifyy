package com.viplavkr.slotify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.models.Booking
import com.viplavkr.slotify.utils.Constants


class ConfirmationActivity : AppCompatActivity() {

    private lateinit var booking: Booking

    private lateinit var bookingIdText: TextView
    private lateinit var slotNameText: TextView
    private lateinit var slotAddressText: TextView
    private lateinit var bookingTimeText: TextView
    private lateinit var durationText: TextView
    private lateinit var vehicleNumberText: TextView
    private lateinit var totalAmountText: TextView
    private lateinit var qrCodePlaceholder: TextView
    private lateinit var doneButton: Button
    private lateinit var viewDirectionsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)


        supportActionBar?.hide()


        booking = intent.getSerializableExtra(Constants.EXTRA_BOOKING) as? Booking
            ?: run {
                finish()
                return
            }

        initializeViews()
        displayConfirmationDetails()
        setupClickListeners()
    }


    private fun initializeViews() {
        bookingIdText = findViewById(R.id.bookingIdText)
        slotNameText = findViewById(R.id.slotNameText)
        slotAddressText = findViewById(R.id.slotAddressText)
        bookingTimeText = findViewById(R.id.bookingTimeText)
        durationText = findViewById(R.id.durationText)
        vehicleNumberText = findViewById(R.id.vehicleNumberText)
        totalAmountText = findViewById(R.id.totalAmountText)
        qrCodePlaceholder = findViewById(R.id.qrCodePlaceholder)
        doneButton = findViewById(R.id.doneButton)
        viewDirectionsButton = findViewById(R.id.viewDirectionsButton)
    }


    private fun displayConfirmationDetails() {
        bookingIdText.text = "Booking ID: ${booking.id.substring(0, 8).uppercase()}"
        slotNameText.text = booking.parkingSlot.name
        slotAddressText.text = booking.parkingSlot.address
        bookingTimeText.text =
            "${booking.getFormattedStartTime()} - ${booking.getFormattedEndTime()}"
        durationText.text =
            "${booking.durationHours} ${if (booking.durationHours == 1) "hour" else "hours"}"
        vehicleNumberText.text = booking.vehicleNumber


        val formattedAmount = String.format("%.2f", booking.totalAmount)
        totalAmountText.text = "Paid: Rs $formattedAmount"


        qrCodePlaceholder.text = "[QR Code]\nScan at entry gate"
    }


    private fun setupClickListeners() {
        doneButton.setOnClickListener {
            navigateToHome()
        }

        viewDirectionsButton.setOnClickListener {
            openMapsNavigation()
        }
    }


    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }


    private fun openMapsNavigation() {
        val uri =
            "geo:${booking.parkingSlot.latitude},${booking.parkingSlot.longitude}?q=${booking.parkingSlot.latitude},${booking.parkingSlot.longitude}(${booking.parkingSlot.name})"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {

            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=${booking.parkingSlot.latitude},${booking.parkingSlot.longitude}")
            )
            startActivity(browserIntent)
        }
    }

    override fun onBackPressed() {

        navigateToHome()
    }
}
