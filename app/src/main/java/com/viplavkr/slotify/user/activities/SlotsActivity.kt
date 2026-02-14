package com.viplavkr.slotify.user.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.common.utils.gone
import com.viplavkr.slotify.common.utils.showToast
import com.viplavkr.slotify.common.utils.visible
import com.viplavkr.slotify.databinding.ActivitySlotsBinding
import com.viplavkr.slotify.user.adapters.SlotsAdapter
import com.viplavkr.slotify.user.viewmodels.SlotsViewModel

class SlotsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlotsBinding
    private lateinit var viewModel: SlotsViewModel
    private lateinit var adapter: SlotsAdapter
    private var selectedSlot: ParkingSlot? = null
    private var showOnlyAvailable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlotsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SlotsViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        setupBottomSheet()
        observeViewModel()

        viewModel.loadSlots()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = SlotsAdapter { slot ->
            if (slot.isAvailable) {
                selectedSlot = slot
                showBottomSheet(slot)
            } else {
                showToast("This slot is currently occupied")
            }
        }

        binding.rvSlots.apply {
            layoutManager = LinearLayoutManager(this@SlotsActivity)
            adapter = this@SlotsActivity.adapter
        }
    }

    private fun setupFilters() {
        binding.chipAvailable.isChecked = true

        binding.chipAvailable.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showOnlyAvailable = true
                binding.chipAll.isChecked = false
                filterSlots()
            }
        }

        binding.chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showOnlyAvailable = false
                binding.chipAvailable.isChecked = false
                filterSlots()
            }
        }
    }

    private fun filterSlots() {
        val allSlots = viewModel.slots.value ?: return
        val filteredSlots = if (showOnlyAvailable) {
            allSlots.filter { it.isAvailable }
        } else {
            allSlots
        }
        adapter.updateSlots(filteredSlots)
        updateSlotsCount(filteredSlots.size, filteredSlots.count { it.isAvailable })
    }

    private fun setupBottomSheet() {
        binding.bottomSheet.gone()

        binding.btnBookNow.setOnClickListener {
            selectedSlot?.let { slot ->
                val intent = Intent(this, PaymentActivity::class.java).apply {
                    putExtra(Constants.EXTRA_SLOT, slot)
                }
                startActivity(intent)
            }
        }

        binding.btnCloseSheet.setOnClickListener {
            hideBottomSheet()
        }
    }

    private fun showBottomSheet(slot: ParkingSlot) {
        binding.tvSheetSlotNumber.text = slot.slotNumber
        binding.tvSheetLevel.text = "Level ${slot.level}"
        binding.tvSheetType.text = slot.type.name
        binding.tvSheetPrice.text = "₹${slot.pricePerHour.toInt()}/hr"
        binding.bottomSheet.visible()
    }

    private fun hideBottomSheet() {
        binding.bottomSheet.gone()
        selectedSlot = null
    }

    private fun observeViewModel() {
        viewModel.slots.observe(this) { slots ->
            filterSlots()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let { showToast(it) }
        }
    }

    private fun updateSlotsCount(total: Int, available: Int) {
        binding.tvSlotsCount.text = "$total slots ($available available)"
    }

    override fun onBackPressed() {
        if (binding.bottomSheet.visibility == View.VISIBLE) {
            hideBottomSheet()
        } else {
            super.onBackPressed()
        }
    }
}