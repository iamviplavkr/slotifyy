package com.viplavkr.slotify.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.BookingStatus
import com.viplavkr.slotify.common.utils.gone
import com.viplavkr.slotify.common.utils.showToast
import com.viplavkr.slotify.common.utils.visible
import com.viplavkr.slotify.databinding.FragmentBookingsBinding
import com.viplavkr.slotify.user.adapters.BookingsAdapter
import com.viplavkr.slotify.user.viewmodels.BookingsViewModel

class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingsViewModel
    private lateinit var adapter: BookingsAdapter
    private var currentTab = BookingStatus.ACTIVE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BookingsViewModel::class.java]

        setupTabs()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadBookings()
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = when (tab?.position) {
                    0 -> BookingStatus.ACTIVE
                    1 -> BookingStatus.COMPLETED
                    else -> BookingStatus.ACTIVE
                }
                filterBookings()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = BookingsAdapter { booking ->
            // Handle booking click - show QR code or details
            requireContext().showToast("Booking: ${booking.bookingId}")
        }

        binding.rvBookings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BookingsFragment.adapter
        }
    }

    private fun filterBookings() {
        val allBookings = viewModel.bookings.value ?: return
        val filteredBookings = allBookings.filter { it.status == currentTab }
        adapter.updateBookings(filteredBookings)

        if (filteredBookings.isEmpty()) {
            binding.emptyState.visible()
            binding.rvBookings.gone()
            binding.tvEmptyMessage.text = if (currentTab == BookingStatus.ACTIVE) {
                "No active bookings"
            } else {
                "No completed bookings"
            }
        } else {
            binding.emptyState.gone()
            binding.rvBookings.visible()
        }
    }

    private fun observeViewModel() {
        viewModel.bookings.observe(viewLifecycleOwner) {
            filterBookings()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { requireContext().showToast(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}