package com.viplavkr.slotify.user.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.utils.Constants

/**
 * Displays parking slots in a grid/list.
 * Visually distinguishes available vs. unavailable slots using
 * the existing drawable backgrounds (bg_status_available, bg_status_unavailable).
 *
 * Unavailable slots are dimmed and non-clickable.
 */
class SlotsAdapter(
    private val onSlotSelected: (ParkingSlot) -> Unit
) : ListAdapter<ParkingSlot, SlotsAdapter.SlotViewHolder>(SlotDiffCallback()) {

    private var unavailableIds: Set<String> = emptySet()
    private var selectedSlotId: String? = null

    fun setUnavailableSlots(ids: Set<String>) {
        unavailableIds = ids
        notifyDataSetChanged()
    }

    fun setSelectedSlot(slotId: String?) {
        val oldSelected = selectedSlotId
        selectedSlotId = slotId
        // Refresh only affected items
        currentList.forEachIndexed { index, slot ->
            if (slot.id == oldSelected || slot.id == slotId) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parking_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSlotNumber: TextView = itemView.findViewById(R.id.tvSlotNumber)
        private val tvFloor: TextView = itemView.findViewById(R.id.tvFloor)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val ivVehicleType: ImageView = itemView.findViewById(R.id.ivVehicleType)
        private val cardRoot: View = itemView.findViewById(R.id.cardSlot)

        fun bind(slot: ParkingSlot) {
            tvSlotNumber.text = slot.slotNumber
            tvFloor.text = slot.floor
            tvPrice.text = "₹${slot.pricePerHour.toInt()}/hr"

            // Vehicle type icon
            val iconRes = when (slot.vehicleType) {
                Constants.VEHICLE_COMPACT -> R.drawable.ic_car_compact
                Constants.VEHICLE_LARGE -> R.drawable.ic_car_large
                else -> R.drawable.ic_car_standard
            }
            ivVehicleType.setImageResource(iconRes)

            val isUnavailable = slot.id in unavailableIds
            val isSelected = slot.id == selectedSlotId

            when {
                isUnavailable -> {
                    cardRoot.setBackgroundResource(R.drawable.bg_status_unavailable)
                    cardRoot.alpha = 0.4f
                    tvStatus.text = "Booked"
                    tvStatus.setTextColor(itemView.context.getColor(R.color.status_cancelled))
                    cardRoot.isClickable = false
                    cardRoot.isFocusable = false
                    cardRoot.setOnClickListener(null)
                }
                isSelected -> {
                    cardRoot.setBackgroundResource(R.drawable.bg_status_active)
                    cardRoot.alpha = 1.0f
                    tvStatus.text = "Selected"
                    tvStatus.setTextColor(itemView.context.getColor(R.color.yellow_primary))
                    cardRoot.setOnClickListener { onSlotSelected(slot) }
                }
                else -> {
                    cardRoot.setBackgroundResource(R.drawable.bg_status_available)
                    cardRoot.alpha = 1.0f
                    tvStatus.text = "Available"
                    tvStatus.setTextColor(itemView.context.getColor(R.color.status_available))
                    cardRoot.setOnClickListener { onSlotSelected(slot) }
                }
            }
        }
    }

    class SlotDiffCallback : DiffUtil.ItemCallback<ParkingSlot>() {
        override fun areItemsTheSame(old: ParkingSlot, new: ParkingSlot) = old.id == new.id
        override fun areContentsTheSame(old: ParkingSlot, new: ParkingSlot) = old == new
    }
}
