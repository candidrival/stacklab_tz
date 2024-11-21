package com.example.stacklab_tz.di

import androidx.room.Room
import com.example.stacklab_tz.db.AppDB
import com.example.stacklab_tz.db.DATABASE_NAME
import com.example.stacklab_tz.presentation.add_task.AddTaskViewModel
import com.example.stacklab_tz.presentation.main.MainScreenViewModel
import com.example.stacklab_tz.repositories.TaskRepository
import com.example.stacklab_tz.repositories.impl.TaskRepositoryImpl
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule
    get() = module {
        single {
            Room.databaseBuilder(androidContext(), AppDB::class.java, DATABASE_NAME)
                .allowMainThreadQueries().build()
        }
        single { get<AppDB>().taskDao() }

        viewModel { MainScreenViewModel(get()) }
        viewModel { AddTaskViewModel(get()) }

        single<TaskRepository> { TaskRepositoryImpl(get()) }
    }