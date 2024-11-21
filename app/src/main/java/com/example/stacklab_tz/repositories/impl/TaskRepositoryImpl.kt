package com.example.stacklab_tz.repositories.impl

import com.example.stacklab_tz.db.dao.TaskDao
import com.example.stacklab_tz.db.entities.Task
import com.example.stacklab_tz.repositories.TaskRepository

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    override suspend fun getTask(id: Int): Task {
        return taskDao.getTask(id)
    }

    override suspend fun insertTask(task: Task) {
        taskDao.insert(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.id)
    }
}