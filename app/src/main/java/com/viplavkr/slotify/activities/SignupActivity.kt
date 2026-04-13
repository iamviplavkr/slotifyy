package com.viplavkr.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.data.MockDataRepository
import com.viplavkr.slotify.user.activities.MainActivity

/**
 * Registration screen for new users.
 * Validates input, registers via MockDataRepository, auto-logs in,
 * and routes directly to the User dashboard (MainActivity).
 * New signups always get Role.USER by default.
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    // Views
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvLoginLink: TextView
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        authManager = AuthManager(this)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignup = findViewById(R.id.btnSignup)
        tvLoginLink = findViewById(R.id.tvLoginLink)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)

        tvError.visibility = View.GONE
        progressBar.visibility = View.GONE
        btnSignup.isEnabled = false
        btnSignup.alpha = 0.5f
    }

    private fun setupListeners() {
        // Enable button only when all fields are filled
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val allFilled = etName.text.toString().trim().isNotEmpty() &&
                        etEmail.text.toString().trim().isNotEmpty() &&
                        etPhone.text.toString().trim().isNotEmpty() &&
                        etPassword.text.toString().isNotEmpty() &&
                        etConfirmPassword.text.toString().isNotEmpty()
                btnSignup.isEnabled = allFilled
                btnSignup.alpha = if (allFilled) 1.0f else 0.5f
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etName.addTextChangedListener(watcher)
        etEmail.addTextChangedListener(watcher)
        etPhone.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)
        etConfirmPassword.addTextChangedListener(watcher)

        btnSignup.setOnClickListener { attemptSignup() }

        tvLoginLink.setOnClickListener {
            finish() // Go back to LoginActivity
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun attemptSignup() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // ── Validation ──────────────────────────────────────────────
        if (name.length < 2) {
            showError("Name must be at least 2 characters")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address")
            return
        }
        if (phone.length < 10) {
            showError("Please enter a valid phone number")
            return
        }
        if (password.length < 6) {
            showError("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            showError("Passwords do not match")
            etConfirmPassword.requestFocus()
            return
        }

        // ── Register ────────────────────────────────────────────────
        setLoadingState(true)
        tvError.visibility = View.GONE

        btnSignup.postDelayed({
            val result = MockDataRepository.register(name, email, phone, password)
            setLoadingState(false)

            result.onSuccess { user ->
                // Auto-login after successful signup
                authManager.saveSession(user)
                Toast.makeText(this, "Welcome to Slotify, ${user.name}!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }

            result.onFailure { error ->
                showError(error.message ?: "Registration failed. Please try again.")
            }
        }, 1000) // Simulated network delay
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun setLoadingState(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnSignup.isEnabled = !loading
        btnSignup.text = if (loading) "" else "Create Account"
        etName.isEnabled = !loading
        etEmail.isEnabled = !loading
        etPhone.isEnabled = !loading
        etPassword.isEnabled = !loading
        etConfirmPassword.isEnabled = !loading
    }
}
