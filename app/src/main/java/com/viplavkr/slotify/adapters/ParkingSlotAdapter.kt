package com.viplavkr.slotify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.models.ParkingSlot


class ParkingSlotAdapter(
    private val onSlotClick: (ParkingSlot) -> Unit
) : RecyclerView.Adapter<ParkingSlotAdapter.ParkingSlotViewHolder>() {

    private var parkingSlots: List<ParkingSlot> = emptyList()


    class ParkingSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.slotNameText)
        val addressText: TextView = itemView.findViewById(R.id.slotAddressText)
        val distanceText: TextView = itemView.findViewById(R.id.slotDistanceText)
        val priceText: TextView = itemView.findViewById(R.id.slotPriceText)
        val availabilityText: TextView = itemView.findViewById(R.id.slotAvailabilityText)
        val ratingText: TextView = itemView.findViewById(R.id.slotRatingText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingSlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parking_slot, parent, false)
        return ParkingSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParkingSlotViewHolder, position: Int) {
        val slot = parkingSlots[position]

        holder.nameText.text = slot.name
        holder.addressText.text = slot.address
        holder.distanceText.text = slot.getFormattedDistance()
        holder.priceText.text = holder.itemView.context.getString(
            R.string.price_format,
            slot.pricePerHour
        )

        holder.ratingText.text = "⭐ ${slot.rating}"


        if (slot.isAvailable()) {
            holder.availabilityText.text = "${slot.availableSlots} slots available"
            holder.availabilityText.setTextColor(
                holder.itemView.context.getColor(R.color.accent_yellow)
            )
        } else {
            holder.availabilityText.text = "Full"
            holder.availabilityText.setTextColor(
                holder.itemView.context.getColor(R.color.error_red)
            )
        }


        holder.itemView.setOnClickListener {
            onSlotClick(slot)
        }
    }

    override fun getItemCount(): Int = parkingSlots.size


    fun submitList(newSlots: List<ParkingSlot>) {
        parkingSlots = newSlots
        notifyDataSetChanged()
    }
}
