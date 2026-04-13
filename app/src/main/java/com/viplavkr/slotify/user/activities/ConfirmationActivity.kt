package com.viplavkr.slotify.user.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.models.BookingStatus
import com.viplavkr.slotify.common.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var ivQrCode: ImageView
    private lateinit var tvBookingId: TextView
    private lateinit var tvSlotInfo: TextView
    private lateinit var tvTimeRange: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var tvInstruction: TextView
    private lateinit var btnDone: Button

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        val bookingId = intent.getStringExtra(Constants.EXTRA_BOOKING_ID)
            ?: run { finish(); return }

        val booking = MockParkingRepository.getBookingById(bookingId)
            ?: run { finish(); return }

        initViews()
        populateDetails(booking)
        generateQrCode(booking)
    }

    private fun initViews() {
        ivQrCode = findViewById(R.id.ivQrCode)
        tvBookingId = findViewById(R.id.tvBookingId)
        tvSlotInfo = findViewById(R.id.tvSlotInfo)
        tvTimeRange = findViewById(R.id.tvTimeRange)
        tvAmount = findViewById(R.id.tvAmount)
        tvStatus = findViewById(R.id.tvStatus)
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod)
        tvInstruction = findViewById(R.id.tvInstruction)
        btnDone = findViewById(R.id.btnDone)

        btnDone.setOnClickListener {
            finish() // or navigate to bookings screen later
        }
    }

    private fun populateDetails(booking: Booking) {

        tvBookingId.text = "Booking #${booking.id.takeLast(8).uppercase()}"
        tvSlotInfo.text = "${booking.slotNumber} • ${booking.locationName}"

        tvTimeRange.text =
            "${dateFormat.format(booking.startTime)}\n→ ${dateFormat.format(booking.endTime)}"

        tvAmount.text = "₹${booking.totalAmount.toInt()}"
        tvPaymentMethod.text = "Paid via ${booking.paymentMethod ?: "N/A"}"

        tvStatus.text = booking.getStatusDisplay()

        val statusColor = when (booking.status) {
            BookingStatus.CONFIRMED -> R.color.status_available
            BookingStatus.ACTIVE -> R.color.status_active
            BookingStatus.COMPLETED -> R.color.status_completed
            else -> R.color.text_secondary
        }

        tvStatus.setTextColor(ContextCompat.getColor(this, statusColor))

        tvInstruction.text =
            "Show this QR code at the parking entrance.\nAdmin will scan it to verify your booking."
    }

    private fun generateQrCode(booking: Booking) {
        try {
            val qrData = "SLOTIFY|${booking.id}|${System.currentTimeMillis()}"
            val size = 512

            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                qrData,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

            val yellowColor = ContextCompat.getColor(this, R.color.yellow_primary)
            val darkColor = ContextCompat.getColor(this, R.color.black_primary)

            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) darkColor else yellowColor)
                }
            }

            ivQrCode.setImageBitmap(bitmap)
            ivQrCode.visibility = View.VISIBLE

        } catch (e: Exception) {
            ivQrCode.setImageResource(R.drawable.ic_qr)
            ivQrCode.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // (future safe cleanup if needed)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}