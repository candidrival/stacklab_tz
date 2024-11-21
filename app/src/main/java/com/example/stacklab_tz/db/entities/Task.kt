package com.example.stacklab_tz.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val hour: Int,
    val minute: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val repeatMode: RepeatMode
)

enum class RepeatMode {
    ONCE, DAILY, MONTOFRI
}
