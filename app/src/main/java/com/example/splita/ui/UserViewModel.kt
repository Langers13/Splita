package com.example.splita.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.splita.data.DataStoreManager
import com.example.splita.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _foodWeight = MutableStateFlow("")
    val foodWeight = _foodWeight.asStateFlow()

    init {
        viewModelScope.launch {
            _users.value = dataStoreManager.usersFlow.first()
        }
    }

    fun addUser() {
        val currentUsers = _users.value.toMutableList()
        val newUser = User(id = (currentUsers.maxOfOrNull { it.id } ?: 0) + 1)
        currentUsers.add(newUser)
        _users.value = currentUsers
        saveUsers()
    }

    fun deleteUser(user: User) {
        val currentUsers = _users.value.toMutableList()
        currentUsers.remove(user)
        _users.value = currentUsers
        saveUsers()
    }

    fun toggleUserSelection(user: User) {
        val currentUsers = _users.value.map {
            if (it.id == user.id) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        }
        _users.value = currentUsers
        saveUsers()
    }

    fun updateUser(updatedUser: User) {
        val currentUsers = _users.value.map {
            if (it.id == updatedUser.id) {
                val totalCalories = calculateTotalCalories(updatedUser)
                updatedUser.copy(totalCalories = totalCalories)
            } else {
                it
            }
        }
        _users.value = currentUsers
        saveUsers()
    }

    private fun calculateTotalCalories(user: User): Int {
        val bmr = if (user.sex == "M") {
            10 * user.weight + 6.25 * user.height - 5 * user.age + 5
        } else {
            10 * user.weight + 6.25 * user.height - 5 * user.age - 161
        }
        return (bmr + user.activityCalories).toInt()
    }

    fun updateFoodWeight(weight: String) {
        _foodWeight.value = weight
    }

    fun calculateFoodWeight(): Double {
        return try {
            val expression = ExpressionBuilder(_foodWeight.value).build()
            expression.evaluate()
        } catch (_: Exception) {
            0.0
        }
    }

    private fun saveUsers() {
        viewModelScope.launch {
            dataStoreManager.saveUsers(_users.value)
        }
    }
}