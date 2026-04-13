package com.viplavkr.slotify.user.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.models.Booking
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.user.activities.ConfirmationActivity
import com.viplavkr.slotify.user.adapters.BookingsAdapter
import com.viplavkr.slotify.user.viewmodels.BookingsViewModel

/**
 * Shows the user's booking history.
 * Active bookings have Extend / QR / Cancel buttons.
 *
 * "Extend Time" opens a bottom sheet where user picks additional hours,
 * checks availability, and processes an extension payment.
 */
class BookingsFragment : Fragment() {

    private lateinit var viewModel: BookingsViewModel
    private lateinit var authManager: AuthManager
    private lateinit var adapter: BookingsAdapter

    private lateinit var rvBookings: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bookings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())
        viewModel = ViewModelProvider(this)[BookingsViewModel::class.java]

        rvBookings = view.findViewById(R.id.rvBookings)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        observeViewModel()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = BookingsAdapter(
            onExtendClicked = { booking -> showExtendBottomSheet(booking) },
            onViewQrClicked = { booking -> navigateToConfirmation(booking) },
            onCancelClicked = { booking -> cancelBooking(booking) }
        )
        rvBookings.layoutManager = LinearLayoutManager(requireContext())
        rvBookings.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            adapter.submitList(bookings)
            tvEmpty.visibility = if (bookings.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.extensionResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                it.onSuccess { booking ->
                    Toast.makeText(requireContext(),
                        "Extended! New end time updated. Additional charge applied.",
                    Toast.LENGTH_LONG).show()
                    loadData() // Refresh list
                }
                it.onFailure { error ->
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
                }
                viewModel.clearExtensionResult()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun loadData() {
        val userId = authManager.getUserId() ?: return
        viewModel.loadBookings(userId)
    }

    private fun showExtendBottomSheet(booking: Booking) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_extend, null)
        dialog.setContentView(sheetView)

        val tvSlotInfo = sheetView.findViewById<TextView>(R.id.tvSheetSlotInfo)
        val numberPicker = sheetView.findViewById<NumberPicker>(R.id.npHours)
        val btnConfirmExtend = sheetView.findViewById<Button>(R.id.btnConfirmExtend)
        val tvEstimate = sheetView.findViewById<TextView>(R.id.tvEstimate)

        tvSlotInfo.text = "${booking.slotNumber} • ${booking.locationName}"

        numberPicker.minValue = 1
        numberPicker.maxValue = 6
        numberPicker.value = 1
        numberPicker.wrapSelectorWheel = false

        // Show estimated cost
        val updateEstimate = {
            val hours = numberPicker.value
            val baseCost = hours * Constants.BASE_PRICE_PER_HOUR
            val total = baseCost + Constants.EXTENSION_SURCHARGE
            tvEstimate.text = "Estimated: ₹${total.toInt()} (₹${Constants.EXTENSION_SURCHARGE.toInt()} surcharge)"
        }
        updateEstimate()

        numberPicker.setOnValueChangedListener { _, _, _ -> updateEstimate() }

        btnConfirmExtend.setOnClickListener {
            viewModel.extendBooking(booking.id, numberPicker.value)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun navigateToConfirmation(booking: Booking) {
        val intent = Intent(requireContext(), ConfirmationActivity::class.java)
        intent.putExtra(Constants.EXTRA_BOOKING_ID, booking.id)
        startActivity(intent)
    }

    private fun cancelBooking(booking: Booking) {
        val userId = authManager.getUserId() ?: return
        viewModel.cancelBooking(booking.id, userId)
        Toast.makeText(requireContext(), "Booking cancelled", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        loadData() // Refresh on return
    }
}
