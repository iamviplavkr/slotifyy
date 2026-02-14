package com.viplavkr.slotify.user.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.viplavkr.slotify.R
import com.viplavkr.slotify.activities.LoginActivity
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.utils.showToast
import com.viplavkr.slotify.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserInfo()
        setupStats()
        setupMenuItems()
    }

    private fun setupUserInfo() {
        val user = AuthManager.getUser()
        binding.tvUserName.text = user?.name ?: "User"
        binding.tvUserPhone.text = user?.phone ?: "+91 98765 43210"
        binding.tvUserEmail.text = user?.email ?: "user@slotify.com"

        // User avatar initial
        val initial = user?.name?.firstOrNull()?.uppercase() ?: "U"
        binding.tvAvatarInitial.text = initial
    }

    private fun setupStats() {
        // Mock stats
        binding.tvTotalBookings.text = "5"
        binding.tvActiveBookings.text = "1"
        binding.tvTotalSpent.text = "₹850"
    }

    private fun setupMenuItems() {
        binding.menuEditProfile.setOnClickListener {
            requireContext().showToast("Edit Profile")
        }

        binding.menuPaymentMethods.setOnClickListener {
            requireContext().showToast("Payment Methods")
        }

        binding.menuNotifications.setOnClickListener {
            requireContext().showToast("Notifications")
        }

        binding.menuHelpSupport.setOnClickListener {
            requireContext().showToast("Help & Support")
        }

        binding.menuTerms.setOnClickListener {
            requireContext().showToast("Terms & Conditions")
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        AuthManager.logout()

        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}