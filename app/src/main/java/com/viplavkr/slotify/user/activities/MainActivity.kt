package com.viplavkr.slotify.user.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.viplavkr.slotify.R
import com.viplavkr.slotify.databinding.ActivityMainBinding
import com.viplavkr.slotify.user.fragments.BookingsFragment
import com.viplavkr.slotify.user.fragments.HomeFragment
import com.viplavkr.slotify.user.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val bookingsFragment = BookingsFragment()
    private val profileFragment = ProfileFragment()
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragments()
        setupBottomNavigation()
    }

    private fun setupFragments() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, profileFragment, "profile").hide(profileFragment)
            add(R.id.fragmentContainer, bookingsFragment, "bookings").hide(bookingsFragment)
            add(R.id.fragmentContainer, homeFragment, "home")
        }.commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    switchFragment(homeFragment)
                    true
                }
                R.id.nav_bookings -> {
                    switchFragment(bookingsFragment)
                    true
                }
                R.id.nav_profile -> {
                    switchFragment(profileFragment)
                    true
                }
                else -> false
            }
        }

        // Set default selection
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun switchFragment(fragment: Fragment) {
        if (fragment != activeFragment) {
            supportFragmentManager.beginTransaction().apply {
                hide(activeFragment)
                show(fragment)
            }.commit()
            activeFragment = fragment
        }
    }

    override fun onBackPressed() {
        if (activeFragment != homeFragment) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        } else {
            super.onBackPressed()
        }
    }
}