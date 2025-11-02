package com.viplavkr.slotify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.data.MockData
import com.viplavkr.slotify.models.Booking
import com.viplavkr.slotify.utils.Constants


class PaymentActivity : AppCompatActivity() {

    private lateinit var booking: Booking

    private lateinit var slotNameText: TextView
    private lateinit var bookingDetailsText: TextView
    private lateinit var totalAmountText: TextView
    private lateinit var cardNumberInput: EditText
    private lateinit var expiryInput: EditText
    private lateinit var cvvInput: EditText
    private lateinit var cardHolderInput: EditText
    private lateinit var payButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)


        supportActionBar?.hide()


        booking = intent.getSerializableExtra(Constants.EXTRA_BOOKING) as? Booking
            ?: run {
                Toast.makeText(this, "Error loading booking", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        initializeViews()
        displayBookingDetails()
        setupClickListeners()
    }


    private fun initializeViews() {
        slotNameText = findViewById(R.id.slotNameText)
        bookingDetailsText = findViewById(R.id.bookingDetailsText)
        totalAmountText = findViewById(R.id.totalAmountText)
        cardNumberInput = findViewById(R.id.cardNumberInput)
        expiryInput = findViewById(R.id.expiryInput)
        cvvInput = findViewById(R.id.cvvInput)
        cardHolderInput = findViewById(R.id.cardHolderInput)
        payButton = findViewById(R.id.payButton)
        cancelButton = findViewById(R.id.cancelButton)
    }


    private fun displayBookingDetails() {
        slotNameText.text = booking.parkingSlot.name
        bookingDetailsText.text = buildString {
            append("Duration: ${booking.durationHours} ${if (booking.durationHours == 1) "hour" else "hours"}\n")
            append("Start: ${booking.getFormattedStartTime()}\n")
            append("End: ${booking.getFormattedEndTime()}\n")
            append("Vehicle: ${booking.vehicleNumber}")
        }
        val formattedAmount = String.format("%.2f", booking.totalAmount)
        totalAmountText.text = "Total: Rs $formattedAmount"
        payButton.text = "PAY RS $formattedAmount"

    }



    private fun setupClickListeners() {
        payButton.setOnClickListener {
            handlePayment()
        }

        cancelButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }


    private fun handlePayment() {
        val cardNumber = cardNumberInput.text.toString().trim()
        val expiry = expiryInput.text.toString().trim()
        val cvv = cvvInput.text.toString().trim()
        val cardHolder = cardHolderInput.text.toString().trim()


        if (cardNumber.isEmpty()) {
            cardNumberInput.error = "Card number is required"
            return
        }

        if (cardNumber.length != 16) {
            cardNumberInput.error = "Invalid card number"
            return
        }

        if (expiry.isEmpty()) {
            expiryInput.error = "Expiry date is required"
            return
        }

        if (cvv.isEmpty()) {
            cvvInput.error = "CVV is required"
            return
        }

        if (cvv.length != 3) {
            cvvInput.error = "Invalid CVV"
            return
        }

        if (cardHolder.isEmpty()) {
            cardHolderInput.error = "Card holder name is required"
            return
        }


        payButton.isEnabled = false
        payButton.text = "Processing..."


        Thread {
            val isSuccess = MockData.processPayment(booking.totalAmount, cardNumber)

            runOnUiThread {
                payButton.isEnabled = true
                payButton.text = "Pay ${booking.getFormattedAmount()}"

                if (isSuccess) {
                    navigateToConfirmation()
                } else {
                    Toast.makeText(this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }


    private fun navigateToConfirmation() {
        val intent = Intent(this, ConfirmationActivity::class.java)
        intent.putExtra(Constants.EXTRA_BOOKING, booking)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
