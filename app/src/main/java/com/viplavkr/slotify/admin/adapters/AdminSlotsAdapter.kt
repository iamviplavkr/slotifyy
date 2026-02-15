package com.viplavkr.slotify.admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.databinding.ItemAdminSlotBinding

class AdminSlotsAdapter(
    private val onEditClick: (ParkingSlot) -> Unit,
    private val onToggleStatus: (ParkingSlot) -> Unit
) : ListAdapter<ParkingSlot, AdminSlotsAdapter.AdminSlotViewHolder>(SlotDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminSlotViewHolder {
        val binding = ItemAdminSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminSlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateSlots(slots: List<ParkingSlot>) {
        submitList(slots)
    }

    inner class AdminSlotViewHolder(
        private val binding: ItemAdminSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(slot: ParkingSlot) {
            binding.apply {
                tvSlotNumber.text = slot.slotNumber
                tvLevel.text = "Level ${slot.level}"
                tvType.text = slot.type.name
                tvLocation.text = slot.locationName
                tvPrice.text = "₹${slot.pricePerHour.toInt()}/hr"

                // Availability status
                if (slot.isAvailable) {
                    tvAvailability.text = "Available"
                    tvAvailability.setBackgroundResource(R.drawable.bg_status_available)
                } else {
                    tvAvailability.text = "Occupied"
                    tvAvailability.setBackgroundResource(R.drawable.bg_status_occupied)
                }

                // Active status
                if (slot.isActive) {
                    tvStatus.text = "Active"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_active)
                } else {
                    tvStatus.text = "Inactive"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_inactive)
                }

                btnEdit.setOnClickListener { onEditClick(slot) }
                btnToggleStatus.setOnClickListener { onToggleStatus(slot) }
                btnToggleStatus.text = if (slot.isActive) "Deactivate" else "Activate"
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