package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val note: String,
    val category: String, // Food, Travel, Shopping, Study, Bills, Friends, Health, Random
    val timestamp: Long
)
