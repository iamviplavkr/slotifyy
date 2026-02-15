package com.viplavkr.slotify.user.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.common.utils.getCurrentDateTime
import com.viplavkr.slotify.common.utils.getEndTime
import com.viplavkr.slotify.common.utils.showToast
import com.viplavkr.slotify.data.remote.PaymentRequest
import com.viplavkr.slotify.data.remote.RetrofitClient
import com.viplavkr.slotify.databinding.ActivityPaymentBinding
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

        setupUI()
    }

    private fun setupUI() {
        setupSlotDetails()
        setupDurationSelector()
        setupPaymentMethods()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnPay.setOnClickListener {
            processPayment()
        }

        updateTotalAmount()
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
        binding.btnDecrease.setOnClickListener {
            if (duration > Constants.MIN_DURATION) {
                duration--
                updateTotalAmount()
            }
        }

        binding.btnIncrease.setOnClickListener {
            if (duration < Constants.MAX_DURATION) {
                duration++
                updateTotalAmount()
            }
        }
    }

    private fun setupPaymentMethods() {
        binding.cardCard.setOnClickListener {
            selectedPaymentMethod = "CARD"
        }

        binding.cardUpi.setOnClickListener {
            selectedPaymentMethod = "UPI"
        }
    }

    private fun updateTotalAmount() {
        val total = (slot?.pricePerHour ?: 0.0) * duration
        binding.tvTotalAmount.text = "₹${total.toInt()}"
        binding.btnPay.text = "Pay ₹${total.toInt()}"
    }

    // ====================================
    // 🔥 REAL BACKEND PAYMENT CALL
    // ====================================
    private fun processPayment() {

        val total = (slot?.pricePerHour ?: 0.0) * duration

        val prefs = getSharedPreferences("slotify_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if (token == null) {
            Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show()
            return
        }

        val request = PaymentRequest(
            bookingId = 1, // replace with real booking ID
            amount = total,
            paymentMode = selectedPaymentMethod
        )

        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.paymentApi
                    .createPayment("Bearer $token", request)

                showLoading(false)

                if (response.isSuccessful) {

                    val bookingId =
                        "BK${System.currentTimeMillis().toString().takeLast(8)}"

                    val intent =
                        Intent(this@PaymentActivity, ConfirmationActivity::class.java).apply {
                            putExtra(Constants.EXTRA_SLOT, slot)
                            putExtra(Constants.EXTRA_DURATION, duration)
                            putExtra(Constants.EXTRA_TOTAL_AMOUNT, total)
                            putExtra(Constants.EXTRA_PAYMENT_METHOD, selectedPaymentMethod)
                            putExtra(Constants.EXTRA_BOOKING_ID, bookingId)
                        }

                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Payment Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                showLoading(false)
                e.printStackTrace()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnPay.isEnabled = !show
    }
}
