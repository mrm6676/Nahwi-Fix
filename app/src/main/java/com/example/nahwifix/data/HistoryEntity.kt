package com.example.nahwifix.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalText: String,
    val correctedText: String,
    val issueCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
