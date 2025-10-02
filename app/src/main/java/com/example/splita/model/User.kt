package com.example.splita.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String = "",
    val age: Int = 0,
    val height: Int = 0,
    val weight: Float = 0f,
    val sex: String = "M",
    val activityCalories: Int = 0,
    val totalCalories: Int = 0,
    val isSelected: Boolean = false
)
