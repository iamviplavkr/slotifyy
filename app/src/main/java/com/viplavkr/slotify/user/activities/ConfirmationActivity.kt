package com.viplavkr.slotify.user.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.common.utils.getCurrentDateTime
import com.viplavkr.slotify.common.utils.getEndTime
import com.viplavkr.slotify.databinding.ActivityConfirmationBinding
import org.json.JSONObject
import java.util.*

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slot = intent.getSerializableExtra(Constants.EXTRA_SLOT) as? ParkingSlot
        val duration = intent.getIntExtra(Constants.EXTRA_DURATION, 1)
        val totalAmount = intent.getDoubleExtra(Constants.EXTRA_TOTAL_AMOUNT, 0.0)
        val paymentMethod = intent.getStringExtra(Constants.EXTRA_PAYMENT_METHOD) ?: "CARD"
        val bookingId = intent.getStringExtra(Constants.EXTRA_BOOKING_ID) ?: "BK00000000"

        setupUI(slot, duration, totalAmount, paymentMethod, bookingId)
        setupButtons()
        generateQRCode(bookingId, slot, duration)
    }

    private fun setupUI(
        slot: ParkingSlot?,
        duration: Int,
        totalAmount: Double,
        paymentMethod: String,
        bookingId: String
    ) {
        binding.tvBookingId.text = bookingId
        binding.tvSlotNumber.text = slot?.slotNumber ?: "--"
        binding.tvLevel.text = "Level ${slot?.level ?: "--"}"
        binding.tvStartTime.text = getCurrentDateTime()
        binding.tvEndTime.text = getEndTime(duration)
        binding.tvDuration.text = "$duration hr${if (duration > 1) "s" else ""}"
        binding.tvTotalAmount.text = "₹${totalAmount.toInt()}"
        binding.tvPaymentMethod.text = paymentMethod
    }

    private fun setupButtons() {
        binding.btnGoHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }

        binding.btnViewBookings.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("navigate_to", "bookings")
            }
            startActivity(intent)
            finish()
        }
    }

    private fun generateQRCode(bookingId: String, slot: ParkingSlot?, duration: Int) {
        try {
            val qrData = JSONObject().apply {
                put("bookingId", bookingId)
                put("slotNumber", slot?.slotNumber ?: "")
                put("level", slot?.level ?: "")
                put("duration", duration)
                put("timestamp", System.currentTimeMillis())
            }.toString()

            val writer = QRCodeWriter()
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 1)
            }

            val bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                    )
                }
            }

            binding.ivQrCode.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        // Navigate to home on back press
        binding.btnGoHome.performClick()
    }
}