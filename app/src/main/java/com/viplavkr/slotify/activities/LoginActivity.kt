package com.viplavkr.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.viplavkr.slotify.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        setupClicks()
    }

    private fun setupClicks() {

        // SEND OTP
        binding.btnSendOtp.setOnClickListener {

            val phone = binding.etPhone.text.toString().trim()

            if (phone.length != 10) {
                binding.etPhone.error = "Enter valid phone number"
                return@setOnClickListener
            }

            sendOtp("+91$phone")
        }

        // VERIFY OTP
        binding.btnVerifyOtp.setOnClickListener {

            val otp = binding.etOtp.text.toString().trim()

            if (otp.length != 6) {
                binding.etOtp.error = "Enter valid OTP"
                return@setOnClickListener
            }

            verifyOtp(otp)
        }
    }

    // =========================
    // SEND OTP
    // =========================
    private fun sendOtp(phone: String) {

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("OTP", "Verification failed", e)
                    binding.etPhone.error = "OTP failed"
                }

                override fun onCodeSent(
                    verId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = verId
                    Log.d("OTP", "OTP Sent Successfully")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // =========================
    // VERIFY OTP
    // =========================
    private fun verifyOtp(otp: String) {

        val verId = verificationId

        if (verId == null) {
            binding.etOtp.error = "Please request OTP first"
            return
        }

        val credential = PhoneAuthProvider.getCredential(verId, otp)
        signInWithCredential(credential)
    }

    // =========================
    // FINAL SIGN IN
    // =========================
    private fun signInWithCredential(credential: PhoneAuthCredential) {

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Log.d("OTP", "Login Successful")

                    // 🔥 MOVE TO MAIN ACTIVITY
                    val intent = Intent(this, com.viplavkr.slotify.user.activities.MainActivity::class.java)

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    binding.etOtp.error = "Invalid OTP"
                    Log.e("OTP", "Login Failed", task.exception)
                }
            }
    }
}
