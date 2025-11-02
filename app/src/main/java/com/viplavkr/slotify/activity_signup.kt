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


class SignupActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var signupButton: Button
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        supportActionBar?.hide()

        initializeViews()
        setupClickListeners()
    }


    private fun initializeViews() {
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        signupButton = findViewById(R.id.signupButton)
        loginText = findViewById(R.id.loginText)
    }


    private fun setupClickListeners() {
        // Signup button click
        signupButton.setOnClickListener {
            handleSignup()
        }

        // Navigate to login
        loginText.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }


    private fun handleSignup() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        // Validate inputs
        if (name.isEmpty()) {
            nameInput.error = "Name is required"
            return
        }

        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            return
        }

        if (phone.isEmpty()) {
            phoneInput.error = "Phone number is required"
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

        if (password != confirmPassword) {
            confirmPasswordInput.error = "Passwords do not match"
            return
        }


        signupButton.isEnabled = false
        signupButton.text = "Creating Account..."


        Thread {
            val isSuccess = MockData.registerUser(name, email, password, phone)

            runOnUiThread {
                signupButton.isEnabled = true
                signupButton.text = "Sign Up"

                if (isSuccess) {
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()


                    saveUserSession(email, name)


                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }


    private fun saveUserSession(email: String, name: String) {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            putString(Constants.KEY_USER_EMAIL, email)
            putString(Constants.KEY_USER_NAME, name)
            apply()
        }
    }
}
