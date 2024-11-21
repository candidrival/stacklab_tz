package com.example.stacklab_tz.repositories

import com.example.stacklab_tz.db.entities.Task

interface TaskRepository {
    suspend fun getAllTasks() : List<Task>
    suspend fun getTask(id: Int): Task
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
}