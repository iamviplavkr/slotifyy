package com.viplavkr.slotify

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.utils.Constants


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        supportActionBar?.hide()




        Handler(Looper.getMainLooper()).postDelayed({
            navigateToLogin()
        }, Constants.SPLASH_DELAY)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }


    private fun navigateToLogin() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)

        val intent = if (isLoggedIn) {

            Intent(this, HomeActivity::class.java)
        } else {

            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()


        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
