package com.viplavkr.slotify.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.models.BookingStatus
import java.text.SimpleDateFormat
import java.util.Locale

class AdminBookingsAdapter(
    private val bookings: List<Booking>,
    private val onComplete: (Booking) -> Unit
) : RecyclerView.Adapter<AdminBookingsAdapter.VH>() {

    private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUser: TextView = itemView.findViewById(R.id.tvAdminBookingUser)
        val tvSlot: TextView = itemView.findViewById(R.id.tvAdminBookingSlot)
        val tvTime: TextView = itemView.findViewById(R.id.tvAdminBookingTime)
        val tvStatus: TextView = itemView.findViewById(R.id.tvAdminBookingStatus)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAdminBookingAmount)
        val btnComplete: Button = itemView.findViewById(R.id.btnAdminComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_booking, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val b = bookings[position]
        holder.tvUser.text = b.userName
        holder.tvSlot.text = "${b.slotNumber} • ${b.locationName}"
        holder.tvTime.text = "${dateFormat.format(b.startTime)} → ${dateFormat.format(b.endTime)}"
        holder.tvAmount.text = "₹${b.totalAmount.toInt()}"
        holder.tvStatus.text = b.getStatusDisplay()

        val color = when (b.status) {
            BookingStatus.CONFIRMED, BookingStatus.ACTIVE -> R.color.status_available
            BookingStatus.COMPLETED -> R.color.status_completed
            BookingStatus.CANCELLED -> R.color.status_cancelled
            BookingStatus.LOCKED -> R.color.status_locked
            else -> R.color.text_secondary
        }
        holder.tvStatus.setTextColor(holder.itemView.context.getColor(color))

        val canComplete = b.status in listOf(BookingStatus.CONFIRMED, BookingStatus.ACTIVE)
        holder.btnComplete.visibility = if (canComplete) View.VISIBLE else View.GONE
        holder.btnComplete.setOnClickListener { onComplete(b) }
    }

    override fun getItemCount() = bookings.size
}

