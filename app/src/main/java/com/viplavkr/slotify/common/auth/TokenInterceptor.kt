package com.viplavkr.slotify.common.auth

import android.content.Context
import android.content.Intent
import com.viplavkr.slotify.activities.LoginActivity

/**
 * Interceptor for handling expired session tokens.
 *
 * In a real app with Retrofit/OkHttp, this would be an OkHttp Interceptor.
 * For the mock demo, call TokenInterceptor.checkToken() before any "API call"
 * to ensure the session hasn't expired.
 *
 * Usage:
 *   if (!TokenInterceptor.checkToken(context)) return // user is being redirected
 */
object TokenInterceptor {

    /**
     * Validates the current auth token. If expired, clears session
     * and redirects to LoginActivity.
     *
     * @return true if token is valid, false if expired (user redirected)
     */
    fun checkToken(context: Context): Boolean {
        val authManager = AuthManager(context)

        if (!authManager.isLoggedIn()) {
            forceLogout(context, authManager)
            return false
        }
        return true
    }

    /**
     * Forces a logout — clears all session data and sends user to Login.
     */
    fun forceLogout(context: Context, authManager: AuthManager? = null) {
        val auth = authManager ?: AuthManager(context)
        auth.clearSession()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}
