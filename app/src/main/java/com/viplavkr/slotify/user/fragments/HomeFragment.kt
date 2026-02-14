package com.viplavkr.slotify.user.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.databinding.FragmentHomeBinding
import com.viplavkr.slotify.user.activities.ScannerActivity
import com.viplavkr.slotify.user.activities.SlotsActivity
import java.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupGreeting()
        setupQuickActions()
        setupStats()
        checkLocationPermission()
    }

    private fun setupGreeting() {
        val user = AuthManager.getUser()
        val firstName = user?.name?.split(" ")?.firstOrNull() ?: "User"
        binding.tvGreeting.text = "Hello, $firstName!"
        binding.tvSubtitle.text = "Find your perfect parking spot"
    }

    private fun setupQuickActions() {
        // Find Parking
        binding.cardFindParking.setOnClickListener {
            startActivity(Intent(requireContext(), SlotsActivity::class.java))
        }

        // Scan QR
        binding.cardScanQr.setOnClickListener {
            startActivity(Intent(requireContext(), ScannerActivity::class.java))
        }

        // My Bookings - Navigate to bookings tab
        binding.cardMyBookings.setOnClickListener {
            (activity as? com.viplavkr.slotify.user.activities.MainActivity)?.let { mainActivity ->
                val bottomNav =
                    mainActivity.findViewById<BottomNavigationView>(R.id.bottomNavigation)
                bottomNav.selectedItemId = R.id.nav_bookings
            }

        }

        // Quick Book
        binding.cardQuickBook.setOnClickListener {
            startActivity(Intent(requireContext(), SlotsActivity::class.java))
        }

        // Main Find Parking Button
        binding.btnFindParking.setOnClickListener {
            startActivity(Intent(requireContext(), SlotsActivity::class.java))
        }
    }

    private fun setupStats() {
        // Mock stats
        binding.tvAvailableSlots.text = "150+"
        binding.tvSupport.text = "24/7"
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val city = addresses[0].locality ?: addresses[0].subAdminArea ?: "Unknown"
                        binding.tvLocation.text = city
                    }
                } catch (e: Exception) {
                    binding.tvLocation.text = "Location Available"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}