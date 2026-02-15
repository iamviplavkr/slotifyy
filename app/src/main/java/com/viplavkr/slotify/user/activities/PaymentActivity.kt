package com.viplavkr.slotify.user.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.common.utils.getCurrentDateTime
import com.viplavkr.slotify.common.utils.getEndTime
import com.viplavkr.slotify.common.utils.showToast
import com.viplavkr.slotify.databinding.ActivityPaymentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var slot: ParkingSlot? = null
    private var duration = Constants.DEFAULT_DURATION
    private var selectedPaymentMethod = "CARD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        slot = intent.getSerializableExtra(Constants.EXTRA_SLOT) as? ParkingSlot

        if (slot == null) {
            showToast("Error loading slot details")
            finish()
            return
        }

        setupToolbar()
        setupSlotDetails()
        setupDurationSelector()
        setupPaymentMethods()
        setupPayButton()
        updateTotalAmount()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupSlotDetails() {
        slot?.let {
            binding.tvSlotNumber.text = it.slotNumber
            binding.tvLevel.text = "Level ${it.level}"
            binding.tvSlotType.text = it.type.name
            binding.tvPricePerHour.text = "₹${it.pricePerHour.toInt()}/hr"
            binding.tvStartTime.text = getCurrentDateTime()
        }
    }

    private fun setupDurationSelector() {
        updateDurationDisplay()

        binding.btnDecrease.setOnClickListener {
            if (duration > Constants.MIN_DURATION) {
                duration--
                updateDurationDisplay()
                updateTotalAmount()
            }
        }

        binding.btnIncrease.setOnClickListener {
            if (duration < Constants.MAX_DURATION) {
                duration++
                updateDurationDisplay()
                updateTotalAmount()
            }
        }
    }

    private fun updateDurationDisplay() {
        binding.tvDuration.text = "$duration hr${if (duration > 1) "s" else ""}"
        binding.tvEndTime.text = getEndTime(duration)
    }

    private fun setupPaymentMethods() {
        binding.cardCard.isSelected = true

        binding.cardCard.setOnClickListener {
            selectedPaymentMethod = "CARD"
            binding.cardCard.isSelected = true
            binding.cardUpi.isSelected = false
            binding.cardDetailsContainer.visibility = View.VISIBLE
            binding.upiDetailsContainer.visibility = View.GONE
        }

        binding.cardUpi.setOnClickListener {
            selectedPaymentMethod = "UPI"
            binding.cardUpi.isSelected = true
            binding.cardCard.isSelected = false
            binding.cardDetailsContainer.visibility = View.GONE
            binding.upiDetailsContainer.visibility = View.VISIBLE
        }
    }

    private fun setupPayButton() {
        binding.btnPay.setOnClickListener {
            processPayment()
        }
    }

    private fun updateTotalAmount() {
        val total = (slot?.pricePerHour ?: 0.0) * duration
        binding.tvTotalAmount.text = "₹${total.toInt()}"
        binding.btnPay.text = "Pay ₹${total.toInt()}"
    }

    private fun processPayment() {
        // Validate payment details
        if (selectedPaymentMethod == "CARD") {
            val cardNumber = binding.etCardNumber.text.toString().trim()
            val expiry = binding.etExpiry.text.toString().trim()
            val cvv = binding.etCvv.text.toString().trim()

            if (cardNumber.length < 16) {
                showToast("Please enter valid card number")
                return
            }
            if (expiry.length < 5) {
                showToast("Please enter valid expiry date")
                return
            }
            if (cvv.length < 3) {
                showToast("Please enter valid CVV")
                return
            }
        } else {
            val upiId = binding.etUpiId.text.toString().trim()
            if (!upiId.contains("@")) {
                showToast("Please enter valid UPI ID")
                return
            }
        }

        showLoading(true)

        // Mock payment processing
        lifecycleScope.launch {
            delay(Constants.PAYMENT_DELAY)

            showLoading(false)

            // Navigate to confirmation
            val totalAmount = (slot?.pricePerHour ?: 0.0) * duration
            val bookingId = "BK${System.currentTimeMillis().toString().takeLast(8)}"

            val intent = Intent(this@PaymentActivity, ConfirmationActivity::class.java).apply {
                putExtra(Constants.EXTRA_SLOT, slot)
                putExtra(Constants.EXTRA_DURATION, duration)
                putExtra(Constants.EXTRA_TOTAL_AMOUNT, totalAmount)
                putExtra(Constants.EXTRA_PAYMENT_METHOD, selectedPaymentMethod)
                putExtra(Constants.EXTRA_BOOKING_ID, bookingId)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnPay.isEnabled = !show
        binding.btnPay.text = if (show) "Processing..." else "Pay ₹${((slot?.pricePerHour ?: 0.0) * duration).toInt()}"
    }
}