package com.viplavkr.slotify.user.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.models.SlotType
import com.viplavkr.slotify.databinding.ItemSlotBinding

class SlotsAdapter(
    private val onSlotClick: (ParkingSlot) -> Unit
) : ListAdapter<ParkingSlot, SlotsAdapter.SlotViewHolder>(SlotDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val binding = ItemSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateSlots(slots: List<ParkingSlot>) {
        submitList(slots)
    }

    inner class SlotViewHolder(
        private val binding: ItemSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSlotClick(getItem(position))
                }
            }
        }

        fun bind(slot: ParkingSlot) {
            binding.apply {
                tvSlotNumber.text = slot.slotNumber
                tvLevel.text = "Level ${slot.level}"
                tvSlotType.text = slot.type.name
                tvPrice.text = "₹${slot.pricePerHour.toInt()}/hr"

                // Set availability badge
                if (slot.isAvailable) {
                    tvStatus.text = "Available"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_available)
                } else {
                    tvStatus.text = "Occupied"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_occupied)
                }

                // Set slot type icon
                val typeIcon = when (slot.type) {
                    SlotType.COMPACT -> R.drawable.ic_car_compact
                    SlotType.STANDARD -> R.drawable.ic_car_standard
                    SlotType.LARGE -> R.drawable.ic_car_large
                }
                ivSlotType.setImageResource(typeIcon)
            }
        }
    }

    class SlotDiffCallback : DiffUtil.ItemCallback<ParkingSlot>() {
        override fun areItemsTheSame(oldItem: ParkingSlot, newItem: ParkingSlot): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ParkingSlot, newItem: ParkingSlot): Boolean {
            return oldItem == newItem
        }
    }
}