package com.viplavkr.slotify.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.ParkingSlot

class AdminSlotsAdapter(
    private val slots: List<ParkingSlot>
) : RecyclerView.Adapter<AdminSlotsAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSlotNumber: TextView = itemView.findViewById(R.id.tvAdminSlotNumber)
        val tvFloor: TextView = itemView.findViewById(R.id.tvAdminSlotFloor)
        val tvType: TextView = itemView.findViewById(R.id.tvAdminSlotType)
        val tvPrice: TextView = itemView.findViewById(R.id.tvAdminSlotPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_slot, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = slots[position]
        holder.tvSlotNumber.text = s.slotNumber
        holder.tvFloor.text = s.floor
        holder.tvType.text = s.vehicleType
        holder.tvPrice.text = "₹${s.pricePerHour.toInt()}/hr"
    }

    override fun getItemCount() = slots.size
}
