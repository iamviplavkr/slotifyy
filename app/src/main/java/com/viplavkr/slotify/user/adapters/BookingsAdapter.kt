package com.viplavkr.slotify.user.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.models.BookingStatus
import com.viplavkr.slotify.databinding.ItemBookingBinding

class BookingsAdapter(
    private val onBookingClick: (Booking) -> Unit
) : ListAdapter<Booking, BookingsAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateBookings(bookings: List<Booking>) {
        submitList(bookings)
    }

    inner class BookingViewHolder(
        private val binding: ItemBookingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onBookingClick(getItem(position))
                }
            }
        }

        fun bind(booking: Booking) {
            binding.apply {
                tvBookingId.text = booking.bookingId
                tvSlotNumber.text = booking.slotNumber
                tvLevel.text = "Level ${booking.level}"
                tvLocation.text = booking.locationName.ifEmpty { "Slotify Parking" }
                tvStartTime.text = booking.startTime
                tvDuration.text = "${booking.duration} hr${if (booking.duration > 1) "s" else ""}"
                tvTotalAmount.text = "₹${booking.totalAmount.toInt()}"

                // Set status badge
                when (booking.status) {
                    BookingStatus.ACTIVE -> {
                        tvStatus.text = "Active"
                        tvStatus.setBackgroundResource(R.drawable.bg_status_active)
                    }
                    BookingStatus.COMPLETED -> {
                        tvStatus.text = "Completed"
                        tvStatus.setBackgroundResource(R.drawable.bg_status_completed)
                    }
                    BookingStatus.CANCELLED -> {
                        tvStatus.text = "Cancelled"
                        tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled)
                    }
                }
            }
        }
    }

    class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem == newItem
        }
    }
}