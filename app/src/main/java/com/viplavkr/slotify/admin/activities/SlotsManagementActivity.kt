package com.viplavkr.slotify.admin.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.Location
import com.viplavkr.slotify.common.models.ParkingSlot
import com.viplavkr.slotify.common.models.SlotType
import com.viplavkr.slotify.common.utils.gone
import com.viplavkr.slotify.common.utils.showToast
import com.viplavkr.slotify.common.utils.visible
import com.viplavkr.slotify.databinding.ActivitySlotsManagementBinding
import com.viplavkr.slotify.databinding.DialogAddSlotBinding
import com.viplavkr.slotify.databinding.DialogBulkAddSlotsBinding
import com.viplavkr.slotify.admin.adapters.AdminSlotsAdapter
import com.viplavkr.slotify.admin.viewmodels.LocationsViewModel
import com.viplavkr.slotify.user.viewmodels.SlotsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SlotsManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlotsManagementBinding
    private lateinit var slotsAdapter: AdminSlotsAdapter
    private var allSlots = mutableListOf<ParkingSlot>()
    private var locations = listOf<Location>()
    private var selectedLocationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlotsManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupLocationFilter()
        setupRecyclerView()
        setupButtons()
        loadData()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupLocationFilter() {
        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocationId = if (position == 0) null else locations.getOrNull(position - 1)?.id
                filterSlots()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        slotsAdapter = AdminSlotsAdapter(
            onEditClick = { slot -> showEditSlotDialog(slot) },
            onToggleStatus = { slot -> toggleSlotStatus(slot) }
        )

        binding.rvSlots.apply {
            layoutManager = LinearLayoutManager(this@SlotsManagementActivity)
            adapter = slotsAdapter
        }
    }

    private fun setupButtons() {
        binding.fabAddSlot.setOnClickListener {
            showAddSlotDialog()
        }

        binding.btnBulkAdd.setOnClickListener {
            showBulkAddDialog()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            binding.progressBar.visible()
            delay(500)

            // Mock locations
            locations = listOf(
                Location("1", "Slotify Mall Parking", "123 Main St", "Mumbai", 0.0, 0.0, 50, 35, true, 50.0),
                Location("2", "Central Plaza Parking", "456 Center Ave", "Mumbai", 0.0, 0.0, 80, 52, true, 60.0),
                Location("3", "Tech Park Parking", "789 Tech Blvd", "Bangalore", 0.0, 0.0, 100, 78, true, 55.0)
            )

            // Setup spinner
            val locationNames = mutableListOf("All Locations")
            locationNames.addAll(locations.map { it.name })
            val spinnerAdapter = ArrayAdapter(this@SlotsManagementActivity, android.R.layout.simple_spinner_item, locationNames)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerLocation.adapter = spinnerAdapter

            // Mock slots
            allSlots = generateMockSlots().toMutableList()

            binding.progressBar.gone()
            filterSlots()
        }
    }

    private fun generateMockSlots(): List<ParkingSlot> {
        val slots = mutableListOf<ParkingSlot>()
        var id = 1

        // Location 1 slots
        for (level in listOf("A", "B")) {
            for (i in 1..10) {
                slots.add(ParkingSlot(
                    id = (id++).toString(),
                    slotNumber = "$level-${String.format("%02d", i)}",
                    level = level,
                    type = when (i % 3) { 0 -> SlotType.LARGE; 1 -> SlotType.COMPACT; else -> SlotType.STANDARD },
                    pricePerHour = 50.0 + (i % 3) * 10,
                    isAvailable = i % 5 != 0,
                    locationId = "1",
                    locationName = "Slotify Mall Parking"
                ))
            }
        }

        // Location 2 slots
        for (level in listOf("P1", "P2")) {
            for (i in 1..15) {
                slots.add(ParkingSlot(
                    id = (id++).toString(),
                    slotNumber = "$level-${String.format("%02d", i)}",
                    level = level,
                    type = when (i % 3) { 0 -> SlotType.LARGE; 1 -> SlotType.COMPACT; else -> SlotType.STANDARD },
                    pricePerHour = 60.0,
                    isAvailable = i % 4 != 0,
                    locationId = "2",
                    locationName = "Central Plaza Parking"
                ))
            }
        }

        return slots
    }

    private fun filterSlots() {
        val filtered = if (selectedLocationId == null) {
            allSlots
        } else {
            allSlots.filter { it.locationId == selectedLocationId }
        }

        slotsAdapter.updateSlots(filtered)
        binding.tvSlotsCount.text = "${filtered.size} Slots"

        if (filtered.isEmpty()) {
            binding.emptyState.visible()
            binding.rvSlots.gone()
        } else {
            binding.emptyState.gone()
            binding.rvSlots.visible()
        }
    }

    private fun showAddSlotDialog() {
        val dialogBinding = DialogAddSlotBinding.inflate(layoutInflater)

        // Setup location spinner in dialog
        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations.map { it.name })
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerLocation.adapter = locationAdapter

        // Setup type spinner
        val types = SlotType.values().map { it.name }
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerType.adapter = typeAdapter

        AlertDialog.Builder(this)
            .setTitle("Add New Slot")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val locationIndex = dialogBinding.spinnerLocation.selectedItemPosition
                val location = locations.getOrNull(locationIndex)
                val slotNumber = dialogBinding.etSlotNumber.text.toString().trim()
                val level = dialogBinding.etLevel.text.toString().trim()
                val type = SlotType.values()[dialogBinding.spinnerType.selectedItemPosition]
                val price = dialogBinding.etPrice.text.toString().toDoubleOrNull() ?: 50.0

                if (location != null && slotNumber.isNotEmpty() && level.isNotEmpty()) {
                    val newSlot = ParkingSlot(
                        id = (allSlots.size + 1).toString(),
                        slotNumber = slotNumber,
                        level = level,
                        type = type,
                        pricePerHour = price,
                        isAvailable = true,
                        locationId = location.id,
                        locationName = location.name
                    )
                    allSlots.add(newSlot)
                    filterSlots()
                    showToast("Slot added successfully")
                } else {
                    showToast("Please fill all required fields")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditSlotDialog(slot: ParkingSlot) {
        val dialogBinding = DialogAddSlotBinding.inflate(layoutInflater)

        // Pre-fill data
        dialogBinding.etSlotNumber.setText(slot.slotNumber)
        dialogBinding.etLevel.setText(slot.level)
        dialogBinding.etPrice.setText(slot.pricePerHour.toInt().toString())

        // Setup spinners
        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations.map { it.name })
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerLocation.adapter = locationAdapter
        val locationIndex = locations.indexOfFirst { it.id == slot.locationId }
        if (locationIndex >= 0) dialogBinding.spinnerLocation.setSelection(locationIndex)

        val types = SlotType.values().map { it.name }
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerType.adapter = typeAdapter
        dialogBinding.spinnerType.setSelection(slot.type.ordinal)

        AlertDialog.Builder(this)
            .setTitle("Edit Slot")
            .setView(dialogBinding.root)
            .setPositiveButton("Update") { _, _ ->
                val locationIndex = dialogBinding.spinnerLocation.selectedItemPosition
                val location = locations.getOrNull(locationIndex)
                val slotNumber = dialogBinding.etSlotNumber.text.toString().trim()
                val level = dialogBinding.etLevel.text.toString().trim()
                val type = SlotType.values()[dialogBinding.spinnerType.selectedItemPosition]
                val price = dialogBinding.etPrice.text.toString().toDoubleOrNull() ?: 50.0

                if (location != null && slotNumber.isNotEmpty() && level.isNotEmpty()) {
                    val index = allSlots.indexOfFirst { it.id == slot.id }
                    if (index >= 0) {
                        allSlots[index] = slot.copy(
                            slotNumber = slotNumber,
                            level = level,
                            type = type,
                            pricePerHour = price,
                            locationId = location.id,
                            locationName = location.name
                        )
                        filterSlots()
                        showToast("Slot updated successfully")
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBulkAddDialog() {
        val dialogBinding = DialogBulkAddSlotsBinding.inflate(layoutInflater)

        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations.map { it.name })
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerLocation.adapter = locationAdapter

        val types = SlotType.values().map { it.name }
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerType.adapter = typeAdapter

        AlertDialog.Builder(this)
            .setTitle("Bulk Add Slots")
            .setView(dialogBinding.root)
            .setPositiveButton("Create") { _, _ ->
                val locationIndex = dialogBinding.spinnerLocation.selectedItemPosition
                val location = locations.getOrNull(locationIndex)
                val level = dialogBinding.etLevel.text.toString().trim()
                val type = SlotType.values()[dialogBinding.spinnerType.selectedItemPosition]
                val price = dialogBinding.etPrice.text.toString().toDoubleOrNull() ?: 50.0
                val startNum = dialogBinding.etStartNumber.text.toString().toIntOrNull() ?: 1
                val count = dialogBinding.etCount.text.toString().toIntOrNull() ?: 10

                if (location != null && level.isNotEmpty() && count > 0) {
                    var currentId = allSlots.size + 1
                    for (i in startNum until startNum + count) {
                        allSlots.add(ParkingSlot(
                            id = (currentId++).toString(),
                            slotNumber = "$level-${String.format("%02d", i)}",
                            level = level,
                            type = type,
                            pricePerHour = price,
                            isAvailable = true,
                            locationId = location.id,
                            locationName = location.name
                        ))
                    }
                    filterSlots()
                    showToast("$count slots created successfully")
                } else {
                    showToast("Please fill all required fields")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toggleSlotStatus(slot: ParkingSlot) {
        val action = if (slot.isActive) "deactivate" else "activate"

        AlertDialog.Builder(this)
            .setTitle("Confirm Action")
            .setMessage("Are you sure you want to $action slot ${slot.slotNumber}?")
            .setPositiveButton("Yes") { _, _ ->
                val index = allSlots.indexOfFirst { it.id == slot.id }
                if (index >= 0) {
                    allSlots[index] = slot.copy(isActive = !slot.isActive)
                    filterSlots()
                    showToast("Slot ${if (slot.isActive) "deactivated" else "activated"}")
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}