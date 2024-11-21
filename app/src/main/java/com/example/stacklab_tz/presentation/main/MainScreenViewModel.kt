package com.example.stacklab_tz.presentation.main

import androidx.lifecycle.ViewModel
import com.example.stacklab_tz.db.entities.Task
import com.example.stacklab_tz.repositories.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenState>(Initial)
    val state = _state.asStateFlow()

    suspend fun getAllTasks() {
        _state.value = Initial
        val tasks = taskRepository.getAllTasks()
        if (tasks.isEmpty()) {
            _state.value = Empty
        } else {
            _state.value = NotEmpty(tasks)
        }
    }

    suspend fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
    }

    sealed interface MainScreenState
    data object Initial : MainScreenState
    data class NotEmpty(val tasks: List<Task>) :
        MainScreenState

    data object Empty : MainScreenState
}