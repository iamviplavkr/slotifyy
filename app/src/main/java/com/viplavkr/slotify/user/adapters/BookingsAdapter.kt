package com.viplavkr.slotify.user.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.models.BookingStatus
import java.text.SimpleDateFormat
import java.util.Locale

class BookingsAdapter(
    private val onExtendClicked: (Booking) -> Unit,
    private val onViewQrClicked: (Booking) -> Unit,
    private val onCancelClicked: (Booking) -> Unit
) : ListAdapter<Booking, BookingsAdapter.BookingViewHolder>(BookingDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvSlotNumber: TextView = itemView.findViewById(R.id.tvSlotNumber)
        private val tvLocationName: TextView = itemView.findViewById(R.id.tvLocationName)
        private val tvTimeRange: TextView = itemView.findViewById(R.id.tvTimeRange)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnExtend: Button = itemView.findViewById(R.id.btnExtend)
        private val btnViewQr: Button = itemView.findViewById(R.id.btnViewQr)
        private val btnCancel: Button = itemView.findViewById(R.id.btnCancel)

        fun bind(booking: Booking) {

            tvSlotNumber.text = booking.slotNumber
            tvLocationName.text = booking.locationName
            tvTimeRange.text =
                "${dateFormat.format(booking.startTime)} → ${dateFormat.format(booking.endTime)}"
            tvAmount.text = "₹${booking.totalAmount.toInt()}"

            // ✅ STATUS TEXT
            tvStatus.text = booking.getStatusDisplay()

            // ✅ STATUS COLOR (FIXED)
            val statusColor = when (booking.status) {
                BookingStatus.CONFIRMED -> R.color.status_available
                BookingStatus.ACTIVE -> R.color.status_active
                BookingStatus.LOCKED -> R.color.status_locked
                BookingStatus.COMPLETED -> R.color.status_completed
                BookingStatus.CANCELLED -> R.color.status_cancelled
                else -> R.color.text_secondary
            }

            tvStatus.setTextColor(
                ContextCompat.getColor(itemView.context, statusColor)
            )

            // ✅ BUTTON VISIBILITY (FIXED)
            val isActive = booking.status in listOf(
                BookingStatus.CONFIRMED,
                BookingStatus.ACTIVE
            )

            btnExtend.visibility = if (isActive) View.VISIBLE else View.GONE
            btnViewQr.visibility = if (isActive) View.VISIBLE else View.GONE

            btnCancel.visibility =
                if (booking.status in listOf(
                        BookingStatus.CONFIRMED,
                        BookingStatus.LOCKED
                    )
                ) View.VISIBLE else View.GONE

            // Clicks
            btnExtend.setOnClickListener { onExtendClicked(booking) }
            btnViewQr.setOnClickListener { onViewQrClicked(booking) }
            btnCancel.setOnClickListener { onCancelClicked(booking) }
        }
    }

    class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(old: Booking, new: Booking) = old.id == new.id
        override fun areContentsTheSame(old: Booking, new: Booking) = old == new
    }
}
