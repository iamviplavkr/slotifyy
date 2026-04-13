package com.viplavkr.slotify.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.Location

class LocationsAdapter(
    private val locations: List<Location>
) : RecyclerView.Adapter<LocationsAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvAdminLocName)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAdminLocAddress)
        val tvSlots: TextView = itemView.findViewById(R.id.tvAdminLocSlots)
        val tvRating: TextView = itemView.findViewById(R.id.tvAdminLocRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_location, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val loc = locations[position]
        holder.tvName.text = loc.name
        holder.tvAddress.text = loc.address
        holder.tvSlots.text = "${loc.totalSlots} total slots"
        holder.tvRating.text = "★ ${loc.rating}"
    }

    override fun getItemCount() = locations.size
}
