package com.viplavkr.slotify.admin.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.models.Location
import com.viplavkr.slotify.common.utils.gone
import com.viplavkr.slotify.common.utils.showToast
import com.viplavkr.slotify.common.utils.visible
import com.viplavkr.slotify.databinding.ActivityLocationsManagementBinding
import com.viplavkr.slotify.databinding.DialogAddLocationBinding
import com.viplavkr.slotify.admin.adapters.LocationsAdapter
import com.viplavkr.slotify.admin.viewmodels.LocationsViewModel

class LocationsManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationsManagementBinding
    private lateinit var viewModel: LocationsViewModel
    private lateinit var adapter: LocationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationsManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LocationsViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeViewModel()

        viewModel.loadLocations()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = LocationsAdapter(
            onEditClick = { location -> showEditDialog(location) },
            onToggleStatus = { location -> toggleLocationStatus(location) }
        )

        binding.rvLocations.apply {
            layoutManager = LinearLayoutManager(this@LocationsManagementActivity)
            adapter = this@LocationsManagementActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fabAddLocation.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val dialogBinding = DialogAddLocationBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Add New Location")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val address = dialogBinding.etAddress.text.toString().trim()
                val city = dialogBinding.etCity.text.toString().trim()
                val priceStr = dialogBinding.etPrice.text.toString().trim()

                if (name.isNotEmpty() && address.isNotEmpty() && city.isNotEmpty()) {
                    val price = priceStr.toDoubleOrNull() ?: 50.0
                    viewModel.addLocation(name, address, city, price)
                } else {
                    showToast("Please fill all required fields")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(location: Location) {
        val dialogBinding = DialogAddLocationBinding.inflate(layoutInflater)

        // Pre-fill existing data
        dialogBinding.etName.setText(location.name)
        dialogBinding.etAddress.setText(location.address)
        dialogBinding.etCity.setText(location.city)
        dialogBinding.etPrice.setText(location.pricePerHour.toInt().toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Location")
            .setView(dialogBinding.root)
            .setPositiveButton("Update") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val address = dialogBinding.etAddress.text.toString().trim()
                val city = dialogBinding.etCity.text.toString().trim()
                val price = dialogBinding.etPrice.text.toString().toDoubleOrNull() ?: 50.0

                if (name.isNotEmpty() && address.isNotEmpty() && city.isNotEmpty()) {
                    viewModel.updateLocation(location.id, name, address, city, price)
                } else {
                    showToast("Please fill all required fields")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toggleLocationStatus(location: Location) {
        val action = if (location.isActive) "deactivate" else "activate"

        AlertDialog.Builder(this)
            .setTitle("Confirm Action")
            .setMessage("Are you sure you want to $action ${location.name}?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.toggleLocationStatus(location)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.locations.observe(this) { locations ->
            adapter.updateLocations(locations)

            if (locations.isEmpty()) {
                binding.emptyState.visible()
                binding.rvLocations.gone()
            } else {
                binding.emptyState.gone()
                binding.rvLocations.visible()
            }

            binding.tvLocationsCount.text = "${locations.size} Locations"
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