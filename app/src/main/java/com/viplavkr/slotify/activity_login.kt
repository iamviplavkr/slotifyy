package com.viplavkr.slotify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.data.MockData
import com.viplavkr.slotify.utils.Constants

/**
 * Login Activity
 * Allows users to log in with email and password.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Hide the action bar for a clean UI
        supportActionBar?.hide()

        initializeViews()
        setupClickListeners()
    }

    /**
     * Initialize all view references
     */
    private fun initializeViews() {
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)
    }

    /**
     * Setup click listeners for buttons
     */
    private fun setupClickListeners() {
        // Login button click
        loginButton.setOnClickListener {
            handleLogin()
        }

        // Navigate to Signup screen
        signupText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    /**
     * Handle login functionality
     */
    private fun handleLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Validate inputs
        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            return
        }

        if (password.isEmpty()) {
            passwordInput.error = "Password is required"
            return
        }

        if (password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            return
        }

        // Show loading state
        loginButton.isEnabled = false
        loginButton.text = "Logging in..."

        // Simulate backend authentication
        Thread {
            val isValid = MockData.validateLogin(email, password)

            runOnUiThread {
                loginButton.isEnabled = true
                loginButton.text = "Login"

                if (isValid) {
                    // Save session data
                    saveUserSession(email)

                    // Navigate to Home screen
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    /**
     * Save user session using SharedPreferences
     */
    private fun saveUserSession(email: String) {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            putString(Constants.KEY_USER_EMAIL, email)
            putString(Constants.KEY_USER_NAME, "User") // Placeholder name
            apply()
        }
    }
}
