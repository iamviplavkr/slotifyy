package com.viplavkr.slotify.admin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viplavkr.slotify.common.data.MockDataRepository
import com.viplavkr.slotify.common.models.User

class UsersViewModel : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun loadUsers() {
        _users.value = MockDataRepository.getAllUsers()
    }
}
