package com.viplavkr.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.viplavkr.slotify.R
import com.viplavkr.slotify.admin.activities.AdminDashboardActivity
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.data.MockParkingRepository
import com.viplavkr.slotify.common.models.Role
import com.viplavkr.slotify.user.activities.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignupLink: TextView
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authManager = AuthManager(this)

        if (authManager.isLoggedIn()) {
            routeToDashboard()
            return
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignupLink = findViewById(R.id.tvSignupLink)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)

        tvError.visibility = View.GONE
        progressBar.visibility = View.GONE
        btnLogin.isEnabled = false
        btnLogin.alpha = 0.5f
    }

    private fun setupListeners() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailFilled = etEmail.text.toString().trim().isNotEmpty()
                val passFilled = etPassword.text.toString().isNotEmpty()
                btnLogin.isEnabled = emailFilled && passFilled
                btnLogin.alpha = if (btnLogin.isEnabled) 1f else 0.5f
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        etEmail.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)

        btnLogin.setOnClickListener { attemptLogin() }

        tvSignupLink.setOnClickListener {
            try {
                startActivity(Intent(this, SignupActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Signup screen not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun attemptLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email")
            return
        }

        if (password.length < 6) {
            showError("Password must be at least 6 characters")
            return
        }

        setLoading(true)

        btnLogin.postDelayed({

            val user = MockParkingRepository.login(email, password)

            setLoading(false)

            if (user != null) {
                authManager.saveSession(user)
                Toast.makeText(this, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                routeToDashboard()
            } else {
                showError("Invalid credentials")
            }

        }, 800)
    }

    private fun routeToDashboard() {

        val role = authManager.getUserRole()

        val intent = when (role) {
            Role.ADMIN -> Intent(this, AdminDashboardActivity::class.java)
            Role.USER -> Intent(this, MainActivity::class.java)
            else -> Intent(this, MainActivity::class.java) // fallback
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showError(msg: String) {
        tvError.text = msg
        tvError.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !loading
        btnLogin.text = if (loading) "Please wait..." else "Log In"
    }
}

