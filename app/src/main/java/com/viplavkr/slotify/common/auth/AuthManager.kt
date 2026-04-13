package com.viplavkr.slotify.common.auth

import android.content.Context
import android.content.SharedPreferences
import com.viplavkr.slotify.common.models.Role
import com.viplavkr.slotify.common.models.User
import com.viplavkr.slotify.common.utils.Constants
import java.util.UUID

/**
 * Manages authentication state using SharedPreferences.
 * Handles login persistence, session tokens, and role-based access.
 *
 * Usage:
 *   val auth = AuthManager(context)
 *   auth.saveSession(user)
 *   if (auth.isLoggedIn()) { ... }
 *   val role = auth.getUserRole()
 */
class AuthManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    // ── Session Management ──────────────────────────────────────────

    /**
     * Persists the user session after successful login/signup.
     * Generates a mock auth token with a 24-hour expiry.
     */
    fun saveSession(user: User) {
        val token = "slotify_${UUID.randomUUID()}"
        val expiry = System.currentTimeMillis() + Constants.TOKEN_VALIDITY_MS

        prefs.edit().apply {
            putString(Constants.KEY_USER_ID, user.id)
            putString(Constants.KEY_USER_NAME, user.name)
            putString(Constants.KEY_USER_EMAIL, user.email)
            putString(Constants.KEY_USER_PHONE, user.phone)
            putString(Constants.KEY_USER_ROLE, user.role.name)
            putString(Constants.KEY_AUTH_TOKEN, token)
            putLong(Constants.KEY_TOKEN_EXPIRY, expiry)
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Returns true if user has an active, non-expired session.
     */
    fun isLoggedIn(): Boolean {
        val loggedIn = prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
        if (!loggedIn) return false

        // Check token expiry
        val expiry = prefs.getLong(Constants.KEY_TOKEN_EXPIRY, 0L)
        if (System.currentTimeMillis() > expiry) {
            clearSession()
            return false
        }
        return true
    }

    /**
     * Returns the current user's Role, or null if not logged in.
     */
    fun getUserRole(): Role? {
        val roleStr = prefs.getString(Constants.KEY_USER_ROLE, null) ?: return null
        return try {
            Role.valueOf(roleStr)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    /**
     * Returns the stored auth token for API calls.
     */
    fun getAuthToken(): String? {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null)
    }

    /**
     * Reconstructs the current User object from stored preferences.
     */
    fun getCurrentUser(): User? {
        if (!isLoggedIn()) return null

        val id = prefs.getString(Constants.KEY_USER_ID, null) ?: return null
        val name = prefs.getString(Constants.KEY_USER_NAME, "") ?: ""
        val email = prefs.getString(Constants.KEY_USER_EMAIL, "") ?: ""
        val phone = prefs.getString(Constants.KEY_USER_PHONE, "") ?: ""
        val role = getUserRole() ?: Role.USER

        return User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            password = "", // Never stored in prefs
        role = role
        )
    }

    fun getUserId(): String? = prefs.getString(Constants.KEY_USER_ID, null)
    fun getUserName(): String? = prefs.getString(Constants.KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(Constants.KEY_USER_EMAIL, null)

    /**
     * Wipes all session data. Used on logout or token expiry.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
