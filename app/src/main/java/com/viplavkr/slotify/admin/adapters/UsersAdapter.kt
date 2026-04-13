package com.viplavkr.slotify.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.User

class UsersAdapter(
    private val users: List<User>
) : RecyclerView.Adapter<UsersAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvAdminUserName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvAdminUserEmail)
        val tvRole: TextView = itemView.findViewById(R.id.tvAdminUserRole)
        val tvPhone: TextView = itemView.findViewById(R.id.tvAdminUserPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_user, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val u = users[position]
        holder.tvName.text = u.name
        holder.tvEmail.text = u.email
        holder.tvPhone.text = u.phone
        holder.tvRole.text = u.role.name
        val roleColor = if (u.role.name == "ADMIN") R.color.yellow_primary else R.color.status_active
        holder.tvRole.setTextColor(holder.itemView.context.getColor(roleColor))
    }

    override fun getItemCount() = users.size
}
