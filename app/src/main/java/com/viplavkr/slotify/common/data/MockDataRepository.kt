package com.viplavkr.slotify.common.data

import com.viplavkr.slotify.common.models.Role
import com.viplavkr.slotify.common.models.User
import java.util.UUID

/**
 * In-memory mock database for demo/presentation purposes.
 * Singleton that survives across Activities within the same app process.
 *
 * Pre-seeded with one Admin and one User account.
 * Swap this out for Retrofit/Room when connecting to a real backend.
 *
 * Demo Credentials:
 *   Admin  →  admin@slotify.com  /  Admin@123
 *   User   →  user@slotify.com   /  User@123
 */
object MockDataRepository {

    // ── User "Table" ────────────────────────────────────────────────

    private val users = mutableListOf(
        User(
            id = "admin-001",
                name = "Slotify Admin",
    email = "admin@slotify.com",
    phone = "+91 98765 00001",
    password = "Admin@123",
    role = Role.ADMIN,
    createdAt = System.currentTimeMillis() - 86_400_000 // created "yesterday"
    ),
    User(
    id = "user-001",
    name = "Viplav Kumar",
    email = "user@slotify.com",
    phone = "+91 98765 00002",
    password = "User@123",
    role = Role.USER,
    createdAt = System.currentTimeMillis() - 43_200_000
    ),
    User(
    id = "user-002",
    name = "Vaibhav Kumar",
    email = "vaibhav@slotify.com",
    phone = "+91 98765 00003",
    password = "vaibhav@123",
    role = Role.USER,
    createdAt = System.currentTimeMillis() - 21_600_000
    )
    )

    // ── Auth Operations ─────────────────────────────────────────────

    /**
     * Authenticates user by email + password.
     * Returns the User on success, null on failure.
     */
    fun login(email: String, password: String): User? {
        return users.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    /**
     * Registers a new user with default USER role.
     * Returns Result.success(user) or Result.failure with reason.
     */
    fun register(name: String, email: String, phone: String, password: String): Result<User> {
        // Check for duplicate email
        if (users.any { it.email.equals(email, ignoreCase = true) }) {
            return Result.failure(Exception("An account with this email already exists"))
        }

        // Check for duplicate phone
        if (users.any { it.phone == phone }) {
            return Result.failure(Exception("This phone number is already registered"))
        }

        val newUser = User(
            id = "user-${UUID.randomUUID().toString().take(8)}",
        name = name,
        email = email,
        phone = phone,
        password = password,
        role = Role.USER
        )
        users.add(newUser)
        return Result.success(newUser)
    }

    // ── Query Operations ────────────────────────────────────────────

    fun findUserById(id: String): User? = users.find { it.id == id }

    fun findUserByEmail(email: String): User? =
        users.find { it.email.equals(email, ignoreCase = true) }

    fun getAllUsers(): List<User> = users.toList()

    fun getUsersByRole(role: Role): List<User> = users.filter { it.role == role }

    fun getUserCount(): Int = users.size

    fun getActiveUserCount(): Int = users.count { it.role == Role.USER }
}
