package com.viplavkr.slotify.admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.Location
import com.viplavkr.slotify.databinding.ItemLocationBinding

class LocationsAdapter(
    private val onEditClick: (Location) -> Unit,
    private val onToggleStatus: (Location) -> Unit
) : ListAdapter<Location, LocationsAdapter.LocationViewHolder>(LocationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateLocations(locations: List<Location>) {
        submitList(locations)
    }

    inner class LocationViewHolder(
        private val binding: ItemLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: Location) {
            binding.apply {
                tvLocationName.text = location.name
                tvAddress.text = location.address
                tvCity.text = location.city
                tvSlotsInfo.text = "${location.availableSlots}/${location.totalSlots} available"
                tvPrice.text = "₹${location.pricePerHour.toInt()}/hr"
                tvTiming.text = "${location.openTime} - ${location.closeTime}"

                // Status badge
                if (location.isActive) {
                    tvStatus.text = "Active"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_active)
                } else {
                    tvStatus.text = "Inactive"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_inactive)
                }

                btnEdit.setOnClickListener { onEditClick(location) }
                btnToggleStatus.setOnClickListener { onToggleStatus(location) }
                btnToggleStatus.text = if (location.isActive) "Deactivate" else "Activate"
            }
        }
    }

    class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }
}