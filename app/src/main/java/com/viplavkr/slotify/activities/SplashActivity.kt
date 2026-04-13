package com.viplavkr.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.admin.activities.AdminDashboardActivity
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.models.Role
import com.viplavkr.slotify.common.utils.Constants
import com.viplavkr.slotify.user.activities.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // ✅ FIX 1: Initialize authManager
        authManager = AuthManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, Constants.SPLASH_DELAY)
    }

    private fun navigateToNextScreen() {

        val intent = if (authManager.isLoggedIn()) {

            // ✅ FIX 2: 'when' (small w)
            val role = authManager.getUserRole()

             when (role) {
                Role.ADMIN -> Intent(this, AdminDashboardActivity::class.java)
                Role.USER -> Intent(this, MainActivity::class.java)
                else -> Intent(this, LoginActivity::class.java)
            }

        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}


