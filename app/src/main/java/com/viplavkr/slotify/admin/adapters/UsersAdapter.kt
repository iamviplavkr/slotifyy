package com.viplavkr.slotify.admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.User
import com.viplavkr.slotify.databinding.ItemUserBinding

class UsersAdapter(
    private val onViewDetails: (User) -> Unit,
    private val onToggleStatus: (User) -> Unit
) : ListAdapter<User, UsersAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateUsers(users: List<User>) {
        submitList(users)
    }

    inner class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                tvUserName.text = user.name
                tvUserPhone.text = user.phone
                tvUserEmail.text = user.email
                tvTotalBookings.text = "${user.totalBookings} bookings"
                tvTotalSpent.text = "₹${user.totalSpent.toInt()}"
                tvMemberSince.text = "Since ${user.createdAt}"

                // Avatar initial
                tvAvatarInitial.text = user.name.firstOrNull()?.uppercase() ?: "U"

                // Status badge
                if (user.isActive) {
                    tvStatus.text = "Active"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_active)
                } else {
                    tvStatus.text = "Inactive"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_inactive)
                }

                root.setOnClickListener { onViewDetails(user) }
                btnToggleStatus.setOnClickListener { onToggleStatus(user) }
                btnToggleStatus.text = if (user.isActive) "Deactivate" else "Activate"
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}