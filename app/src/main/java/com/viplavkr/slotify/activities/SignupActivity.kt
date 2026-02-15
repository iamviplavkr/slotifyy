package com.viplavkr.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.viplavkr.slotify.R
import com.viplavkr.slotify.common.auth.AuthManager
import com.viplavkr.slotify.common.models.Role
import com.viplavkr.slotify.common.models.User
import com.viplavkr.slotify.common.utils.*
import com.viplavkr.slotify.databinding.ActivitySignupBinding
import com.viplavkr.slotify.user.activities.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var isOtpSent = false
    private var currentPhone = ""
    private var currentName = ""
    private var currentEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.otpContainer.gone()
        binding.btnSignup.isEnabled = false
    }

    private fun setupListeners() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        binding.etName.addTextChangedListener(watcher)
        binding.etPhone.addTextChangedListener(watcher)
        binding.etEmail.addTextChangedListener(watcher)
        binding.etOtp.addTextChangedListener(watcher)

        binding.btnSignup.setOnClickListener {
            if (isOtpSent) verifyOtpAndSignup() else sendOtp()
        }

        binding.tvChangeNumber.setOnClickListener { resetToDetailsInput() }
        binding.tvLoginLink.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun validateForm() {
        binding.btnSignup.isEnabled = if (isOtpSent) {
            binding.etOtp.text.toString().isValidOtp()
        } else {
            binding.etName.text.toString().isNotEmpty() &&
                    binding.etPhone.text.toString().isValidPhone() &&
                    binding.etEmail.text.toString().isValidEmail()
        }
    }

    private fun sendOtp() {
        currentName = binding.etName.text.toString().trim()
        currentPhone = binding.etPhone.text.toString().trim()
        currentEmail = binding.etEmail.text.toString().trim()

        if (!currentPhone.isValidPhone() || !currentEmail.isValidEmail()) {
            showToast("Enter valid details")
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            delay(1000)
            showLoading(false)
            isOtpSent = true

            binding.detailsContainer.gone()
            binding.otpContainer.visible()
            binding.tvPhoneDisplay.text = "+91 $currentPhone"
            binding.btnSignup.text = getString(R.string.create_account)
            binding.btnSignup.isEnabled = false
        }
    }

    private fun verifyOtpAndSignup() {
        if (!binding.etOtp.text.toString().isValidOtp()) {
            showToast("Invalid OTP")
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            delay(1500)

            val newUser = User(
                id = "user_${System.currentTimeMillis()}",
                name = currentName,
                phone = "+91$currentPhone",
                email = currentEmail,
                role = Role.USER.name
            )

            val mockToken = "mock_token_${System.currentTimeMillis()}"
            AuthManager.saveToken(mockToken)
            AuthManager.saveUser(newUser)

            showLoading(false)

            startActivity(
                Intent(this@SignupActivity, MainActivity::class.java)
                    .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
            )
            finish()
        }
    }

    private fun resetToDetailsInput() {
        isOtpSent = false
        binding.detailsContainer.visible()
        binding.otpContainer.gone()
        binding.btnSignup.text = getString(R.string.send_otp)
        binding.etOtp.text?.clear()
        validateForm()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSignup.isEnabled = !show
    }
}
