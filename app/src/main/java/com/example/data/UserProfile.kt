package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val username: String,
    val monthlyBudget: Double,
    val streak: Int = 0,
    val isOnboarded: Boolean = false
)
