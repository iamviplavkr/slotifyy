package com.viplavkr.slotify.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Extension functions and utilities used across the app.
 */

// ── Network Connectivity Check ──────────────────────────────────────

/**
 * Returns true if the device has an active internet connection.
 * Use before any API call to show graceful error.
 */
fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

/**
 * Shows a Snackbar error and prevents the action if offline.
 * Returns true if network is available, false if not (Snackbar shown).
 *
 * Usage:
 *   if (!rootView.requireNetwork(context)) return
 */
fun View.requireNetwork(context: Context): Boolean {
    if (!context.isNetworkAvailable()) {
        Snackbar.make(this, "No internet connection. Please check your network.", Snackbar.LENGTH_LONG)
        .setBackgroundTint(context.getColor(android.R.color.holo_red_dark))
            .setTextColor(context.getColor(android.R.color.white))
            .show()
        return false
    }
    return true
}

// ── Date/Time Formatting ────────────────────────────────────────────

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(this)
}

fun Long.toFormattedDateTime(): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(this)
}

fun Long.toTimeOnly(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(this)
}

// ── Currency Formatting ─────────────────────────────────────────────

fun Double.toRupees(): String = "₹${this.toInt()}"

// ── String Extensions ───────────────────────────────────────────────

fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPhone(): Boolean = this.length >= 10
