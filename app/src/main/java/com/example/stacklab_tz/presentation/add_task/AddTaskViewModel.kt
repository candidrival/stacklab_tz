package com.example.stacklab_tz.presentation.add_task

import androidx.lifecycle.ViewModel
import com.example.stacklab_tz.db.entities.Task
import com.example.stacklab_tz.repositories.TaskRepository

class AddTaskViewModel(
    private val taskRepository: TaskRepository
): ViewModel() {

    suspend fun insertTask(task: Task) {
        taskRepository.insertTask(task)
    }

    suspend fun getTask(id: Int) : Task {
        return taskRepository.getTask(id)
    }
}