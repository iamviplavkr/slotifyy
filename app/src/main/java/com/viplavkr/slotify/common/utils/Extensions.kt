package com.viplavkr.slotify.common.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// View Extensions
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// Number Extensions
fun Double.toCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(this)
}

fun Double.toRupees(): String {
    return "₹${String.format("%.0f", this)}"
}

// Date Extensions
fun Date.toFormattedString(pattern: String = "dd MMM yyyy, hh:mm a"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this)
}

fun String.toDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): Date? {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date())
}

fun getEndTime(hours: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.HOUR_OF_DAY, hours)
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(calendar.time)
}

// String Extensions
fun String.isValidPhone(): Boolean {
    return this.length == 10 && this.all { it.isDigit() }
}

fun String.isValidOtp(): Boolean {
    return this.length == 6 && this.all { it.isDigit() }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}