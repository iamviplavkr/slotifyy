package com.viplavkr.slotify.user.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.Location
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.user.activities.SlotsActivity
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var authManager: AuthManager
    private lateinit var adapter: LocationsAdapter
    private var allLocations: List<Location> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        setupGreeting(view)
        setupSearch(view)
        setupLocations(view)
    }

    // ✅ Greeting
    private fun setupGreeting(view: View) {
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        val tvSubGreeting = view.findViewById<TextView>(R.id.tvSubGreeting)

        val name = authManager.getUserName() ?: "User"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }

        tvGreeting.text = "$greeting, ${name.split(" ").first()}!"
        tvSubGreeting.text = "Where are you parking today?"
    }

    // ✅ Search
    private fun setupSearch(view: View) {
        val etSearch = view.findViewById<EditText>(R.id.etSearch)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterLocations(s.toString())
            }
        })
    }

    private fun filterLocations(query: String) {
        val filtered = if (query.isBlank()) {
            allLocations
        } else {
            allLocations.filter {
                it.name.contains(query, true) ||
                        it.address.contains(query, true)
            }
        }
        adapter.updateList(filtered)
    }

    // ✅ RecyclerView
    private fun setupLocations(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvLocations)

        allLocations = MockParkingRepository.getAllLocations()

        adapter = LocationsAdapter(allLocations) { location ->
            val intent = Intent(requireContext(), SlotsActivity::class.java)
            intent.putExtra(Constants.EXTRA_LOCATION, location.id)
            startActivity(intent)
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

    // ✅ Adapter
    inner class LocationsAdapter(
        private var items: List<Location>,
        private val onClick: (Location) -> Unit
    ) : RecyclerView.Adapter<LocationsAdapter.VH>() {

        fun updateList(newItems: List<Location>) {
            items = newItems
            notifyDataSetChanged()
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvLocationName)
            val address: TextView = view.findViewById(R.id.tvAddress)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_location, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val loc = items[position]
            holder.name.text = loc.name
            holder.address.text = loc.address
            holder.itemView.setOnClickListener { onClick(loc) }
        }

        override fun getItemCount() = items.size
    }
}
