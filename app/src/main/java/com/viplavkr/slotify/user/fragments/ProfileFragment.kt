package com.viplavkr.slotify.user.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.viplavkr.slotify.R
import com.viplavkr.slotify.activities.LoginActivity
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.BookingStatus

class ProfileFragment : Fragment() {

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())
        val user = authManager.getCurrentUser()

        val tvName = view.findViewById<TextView>(R.id.tvUserName)
        val tvEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvUserPhone)
        val tvInitials = view.findViewById<TextView>(R.id.tvAvatarInitial)
        val tvTotalBookings = view.findViewById<TextView>(R.id.tvTotalBookings)
        val tvActiveBookings = view.findViewById<TextView>(R.id.tvActiveBookings)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // USER DATA
        tvName.text = user?.name ?: "User"
        tvEmail.text = user?.email ?: ""
        tvPhone.text = user?.phone ?: ""

        // INITIALS (FIXED INDENTATION)
        val initials = user?.name
            ?.split(" ")
            ?.take(2)
            ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
            ?.joinToString("")
            ?: "U"

        tvInitials.text = initials

        // BOOKINGS
        val userId = user?.id ?: ""
        val bookings = MockParkingRepository.getBookingsByUser(userId)

        tvTotalBookings.text = bookings.size.toString()

        // ✅ FIX: USE ENUM INSTEAD OF STRING
        val activeCount = bookings.count {
            it.status in listOf(
                BookingStatus.CONFIRMED,
                BookingStatus.ACTIVE
            )
        }

        tvActiveBookings.text = activeCount.toString()

        // LOGOUT
        btnLogout.setOnClickListener {
            authManager.clearSession()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}

