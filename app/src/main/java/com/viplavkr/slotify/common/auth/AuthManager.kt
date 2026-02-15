package com.viplavkr.slotify.common.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.viplavkr.slotify.common.models.Role
import com.viplavkr.slotify.common.models.User
import org.json.JSONObject

object AuthManager {
    private const val PREF_NAME = "slotify_auth"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUser(user: User) {
        prefs.edit().apply {
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_PHONE, user.phone)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_ROLE, user.role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): User? {
        if (!isLoggedIn()) return null
        return User(
            id = prefs.getString(KEY_USER_ID, "") ?: "",
            name = prefs.getString(KEY_USER_NAME, "") ?: "",
            phone = prefs.getString(KEY_USER_PHONE, "") ?: "",
            email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            role = prefs.getString(KEY_USER_ROLE, "USER") ?: "USER"
        )
    }

    fun getRole(): Role {
        val roleString = prefs.getString(KEY_USER_ROLE, "USER") ?: "USER"
        return Role.fromString(roleString)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun parseJwtRole(token: String): Role {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return Role.USER

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            val role = json.optString("role", "USER")
            Role.fromString(role)
        } catch (e: Exception) {
            Role.USER
        }
    }

    fun isTokenValid(): Boolean {
        val token = getToken() ?: return false
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return false

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            val exp = json.optLong("exp", 0)
            val currentTime = System.currentTimeMillis() / 1000
            exp > currentTime
        } catch (e: Exception) {
            false
        }
    }
}