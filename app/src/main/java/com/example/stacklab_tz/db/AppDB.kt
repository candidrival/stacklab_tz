package com.example.stacklab_tz.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stacklab_tz.db.dao.TaskDao
import com.example.stacklab_tz.db.entities.Task

const val DATABASE_NAME = "room_db"
private const val CURRENT_DB_VERSION = 1

@Database(
    entities = [
        Task::class,
    ],
    version = CURRENT_DB_VERSION
)
abstract class AppDB : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}