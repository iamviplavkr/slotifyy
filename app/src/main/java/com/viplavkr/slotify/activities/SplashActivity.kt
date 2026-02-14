package com.viplavkr.slotify.activities

import android.annotation.SuppressLint
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

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize AuthManager
        AuthManager.init(this)

        // Navigate after splash delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, Constants.SPLASH_DELAY)
    }

    private fun navigateToNextScreen() {
        val intent = if (AuthManager.isLoggedIn()) {
            // User is logged in, check role
            when (AuthManager.getRole()) {
                Role.ADMIN -> Intent(this, AdminDashboardActivity::class.java)
                Role.USER -> Intent(this, MainActivity::class.java)
            }
        } else {
            // User is not logged in, go to login
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}