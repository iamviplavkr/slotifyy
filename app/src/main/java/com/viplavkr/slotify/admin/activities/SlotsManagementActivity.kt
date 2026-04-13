package com.viplavkr.slotify.admin.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viplavkr.slotify.R
import com.viplavkr.slotify.admin.adapters.AdminSlotsAdapter
import com.viplavkr.slotify.common.data.MockParkingRepository

/**
 * Admin view of all parking slots across locations.
 */
class SlotsManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val rvList = findViewById<RecyclerView>(R.id.rvList)

        tvTitle.text = "Manage Slots"
        ivBack.setOnClickListener { finish(); overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) }

        val slots = MockParkingRepository.getAllSlots()
        val adapter = AdminSlotsAdapter(slots)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter
    }
}
