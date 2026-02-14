package com.viplavkr.slotify.admin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viplavkr.slotify.common.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success

    private var allUsers = listOf<User>()
    private val usersList = mutableListOf<User>()

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                delay(500)

                allUsers = generateMockUsers()
                usersList.clear()
                usersList.addAll(allUsers)
                _users.value = usersList.toList()

            } catch (e: Exception) {
                _error.value = "Failed to load users: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                usersList.clear()
                usersList.addAll(allUsers)
            } else {
                val filtered = allUsers.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.phone.contains(query) ||
                            it.email.contains(query, ignoreCase = true)
                }
                usersList.clear()
                usersList.addAll(filtered)
            }
            _users.value = usersList.toList()
        }
    }

    fun toggleUserStatus(user: User) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                delay(300)

                val index = usersList.indexOfFirst { it.id == user.id }
                if (index >= 0) {
                    val updated = user.copy(isActive = !user.isActive)
                    usersList[index] = updated

                    // Update in allUsers too
                    val allIndex = allUsers.indexOfFirst { it.id == user.id }
                    if (allIndex >= 0) {
                        allUsers = allUsers.toMutableList().apply { set(allIndex, updated) }
                    }

                    _users.value = usersList.toList()
                    _success.value = "User ${if (updated.isActive) "activated" else "deactivated"}"
                }

            } catch (e: Exception) {
                _error.value = "Failed to update status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateMockUsers(): List<User> {
        return listOf(
            User("u1", "John Doe", "+919876543210", "john.doe@email.com", "USER", true, "Jan 2024", 12, 2400.0),
            User("u2", "Jane Smith", "+919876543211", "jane.smith@email.com", "USER", true, "Feb 2024", 8, 1600.0),
            User("u3", "Bob Wilson", "+919876543212", "bob.wilson@email.com", "USER", true, "Mar 2024", 15, 3200.0),
            User("u4", "Alice Brown", "+919876543213", "alice.brown@email.com", "USER", true, "Mar 2024", 5, 850.0),
            User("u5", "Charlie Davis", "+919876543214", "charlie.d@email.com", "USER", false, "Apr 2024", 2, 200.0),
            User("u6", "Eva Martinez", "+919876543215", "eva.m@email.com", "USER", true, "Apr 2024", 20, 4500.0),
            User("u7", "Frank Johnson", "+919876543216", "frank.j@email.com", "USER", true, "May 2024", 7, 1400.0),
            User("u8", "Grace Lee", "+919876543217", "grace.lee@email.com", "USER", true, "May 2024", 10, 2100.0),
            User("u9", "Henry Taylor", "+919876543218", "henry.t@email.com", "USER", true, "Jun 2024", 3, 450.0),
            User("u10", "Ivy Chen", "+919876543219", "ivy.chen@email.com", "USER", true, "Jun 2024", 6, 1200.0)
        )
    }
}