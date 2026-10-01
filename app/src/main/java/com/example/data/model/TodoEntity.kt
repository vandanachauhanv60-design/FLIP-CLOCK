package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val pomodorosEstimated: Int = 2,
    val pomodorosCompleted: Int = 0,
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)
