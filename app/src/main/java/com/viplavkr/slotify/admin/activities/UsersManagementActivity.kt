package com.viplavkr.slotify.admin.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.User
import com.viplavkr.slotify.common.utils.gone
import com.viplavkr.slotify.common.utils.showToast
import com.viplavkr.slotify.common.utils.visible
import com.viplavkr.slotify.databinding.ActivityUsersManagementBinding
import com.viplavkr.slotify.admin.adapters.UsersAdapter
import com.viplavkr.slotify.admin.viewmodels.UsersViewModel

class UsersManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersManagementBinding
    private lateinit var viewModel: UsersViewModel
    private lateinit var adapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[UsersViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        observeViewModel()

        viewModel.loadUsers()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(
            onViewDetails = { user -> showUserDetails(user) },
            onToggleStatus = { user -> toggleUserStatus(user) }
        )

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UsersManagementActivity)
            adapter = this@UsersManagementActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { v, _, _ ->
            val query = v.text.toString().trim()
            viewModel.searchUsers(query)
            true
        }
    }

    private fun showUserDetails(user: User) {
        AlertDialog.Builder(this)
            .setTitle(user.name)
            .setMessage("""
                Phone: ${user.phone}
                Email: ${user.email}
                Total Bookings: ${user.totalBookings}
                Total Spent: ₹${user.totalSpent.toInt()}
                Status: ${if (user.isActive) "Active" else "Inactive"}
                Member Since: ${user.createdAt}
            """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun toggleUserStatus(user: User) {
        val action = if (user.isActive) "deactivate" else "activate"

        AlertDialog.Builder(this)
            .setTitle("Confirm Action")
            .setMessage("Are you sure you want to $action ${user.name}?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.toggleUserStatus(user)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.users.observe(this) { users ->
            adapter.updateUsers(users)

            if (users.isEmpty()) {
                binding.emptyState.visible()
                binding.rvUsers.gone()
            } else {
                binding.emptyState.gone()
                binding.rvUsers.visible()
            }

            binding.tvUsersCount.text = "${users.size} Users"
            binding.tvActiveUsers.text = users.count { it.isActive }.toString()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let { showToast(it) }
        }

        viewModel.success.observe(this) { message ->
            message?.let { showToast(it) }
        }
    }
}