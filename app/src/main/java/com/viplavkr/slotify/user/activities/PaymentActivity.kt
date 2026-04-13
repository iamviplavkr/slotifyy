package com.viplavkr.slotify.user.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var booking: Booking
    private var countDownTimer: CountDownTimer? = null

    // Views
    private lateinit var tvSlotInfo: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvTimeRange: TextView
    private lateinit var tvCountdown: TextView
    private lateinit var rgPaymentMethod: RadioGroup
    private lateinit var rbUpi: RadioButton
    private lateinit var rbCard: RadioButton
    private lateinit var btnPay: Button
    private lateinit var ivBack: ImageView

    private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Get booking safely
        booking = intent.getSerializableExtra(Constants.EXTRA_BOOKING) as? Booking
            ?: run {
                Toast.makeText(this, "Invalid booking", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        initViews()
        populateBookingDetails()
        setupPaymentMethods()
        startCountdown()
    }

    private fun initViews() {
        tvSlotInfo = findViewById(R.id.tvSlotInfo)
        tvAmount = findViewById(R.id.tvAmount)
        tvTimeRange = findViewById(R.id.tvTimeRange)
        tvCountdown = findViewById(R.id.tvCountdown)

        rgPaymentMethod = findViewById(R.id.rgPaymentMethod)
        rbUpi = findViewById(R.id.rbUpi)
        rbCard = findViewById(R.id.rbCard)

        btnPay = findViewById(R.id.btnPay)
        ivBack = findViewById(R.id.ivBack)

        ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun populateBookingDetails() {
        tvSlotInfo.text = "${booking.slotNumber} • ${booking.locationName}"
        tvAmount.text = "₹${booking.totalAmount.toInt()}"
        tvTimeRange.text =
            "${dateFormat.format(booking.startTime)} → ${dateFormat.format(booking.endTime)}"

        updateButtonText()
    }

    private fun setupPaymentMethods() {
        rgPaymentMethod.setOnCheckedChangeListener { _, _ ->
            updateButtonText()
        }

        btnPay.setOnClickListener {
            processPayment()
        }
    }

    private fun processPayment() {
        when {
            rbUpi.isChecked -> processUpi()
            rbCard.isChecked -> simulateSuccess("CARD")
            else -> Toast.makeText(this, "Select payment method", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processUpi() {
        val uri = Uri.parse(
            "upi://pay?pa=${Constants.DEMO_UPI_ID}&pn=Slotify&am=${booking.totalAmount}&cu=INR"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            simulateSuccess("UPI")
        }
    }

    private fun simulateSuccess(method: String) {
        btnPay.isEnabled = false

        btnPay.postDelayed({

            val txnId = "TXN${UUID.randomUUID().toString().take(6)}"

            val success = MockParkingRepository.confirmBooking(
                booking.id,
                method,
                txnId
            )

            countDownTimer?.cancel()

            if (success) {
                val intent = Intent(this, ConfirmationActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOKING_ID, booking.id)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
                btnPay.isEnabled = true
            }

        }, 1500)
    }

    private fun updateButtonText() {
        val method = when {
            rbCard.isChecked -> "Card"
            rbUpi.isChecked -> "UPI"
            else -> "UPI"
        }

        btnPay.text = "Pay ₹${booking.totalAmount.toInt()} via $method"
    }

    private fun startCountdown() {
        val remaining = Constants.SLOT_LOCK_DURATION_MS

        countDownTimer = object : CountDownTimer(remaining, 1000) {

            override fun onTick(ms: Long) {
                val minutes = ms / 60000
                val seconds = (ms % 60000) / 1000
                tvCountdown.text = "Slot reserved for $minutes:${String.format("%02d", seconds)}"
            }

            override fun onFinish() {
                Toast.makeText(this@PaymentActivity, "Slot expired", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.start()
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}