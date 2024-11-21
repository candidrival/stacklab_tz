package com.example.stacklab_tz.utils.pushes

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.stacklab_tz.db.entities.Task
import com.google.gson.Gson
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object RemindersManager {
    @SuppressLint("ScheduleExactAlarm")
    fun startDailyReminder(
        context: Context,
        task: Task,
        reminderId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent =
            Intent(context.applicationContext, AlarmDailyReceiver::class.java).let { intent ->
                intent.putExtra("title", task.title)
                val taskString = Gson().toJson(task)
                intent.putExtra("task", taskString)
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        val calendar: Calendar =
            Calendar.getInstance(Locale.ENGLISH).apply {
                set(Calendar.HOUR_OF_DAY, task.hour)
                set(Calendar.MINUTE, task.minute)
            }

        if (Calendar.getInstance(Locale.ENGLISH)
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
            intent
        )
        Log.d("tag_notif", "RemindersManager: ${task.title} startDailyReminder")
    }

    @SuppressLint("ScheduleExactAlarm")
    fun startMonToFriReminder(
        context: Context,
        task: Task,
        reminderId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent =
            Intent(context.applicationContext, AlarmMonToFriReceiver::class.java).let { intent ->
                intent.putExtra("title", task.title)
                val taskString = Gson().toJson(task)
                intent.putExtra("task", taskString)
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        val calendar: Calendar =
            Calendar.getInstance(Locale.ENGLISH).apply {
                set(Calendar.HOUR_OF_DAY, task.hour)
                set(Calendar.MINUTE, task.minute)
            }

        if (Calendar.getInstance(Locale.ENGLISH)
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
            intent
        )
        Log.d("tag_notif", "RemindersManager: ${task.title} startDailyReminder")
    }

    @SuppressLint("ScheduleExactAlarm", "SimpleDateFormat")
    fun startOnceReminder(
        context: Context,
        task: Task,
        reminderId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent =
            Intent(context.applicationContext, AlarmOnceReceiver::class.java).let { intent ->
                intent.putExtra("title", task.title)
                val taskString = Gson().toJson(task)
                intent.putExtra("task", taskString)
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        val date = task.day.toString() + "-" + (task.month + 1).toString() + "-" + task.year.toString()
        val timeTonotify = task.hour.toString() + ":" + task.minute.toString()
        val dateandtime: String = "$date $timeTonotify"

        val formatter: DateFormat = SimpleDateFormat("d-M-yyyy hh:mm")
        val date1 = formatter.parse(dateandtime)

        if (date1 != null) {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(date1.time, intent),
                intent
            )
        }
        Log.d("tag_notif", "RemindersManager: ${task.title} startOnceReminder")
    }

    fun stopReminder(
        context: Context,
        task: Task,
        reminderId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent =
            Intent(context.applicationContext, AlarmDailyReceiver::class.java).let { intent ->
                intent.putExtra("title", task.title)
                val taskString = Gson().toJson(task)
                intent.putExtra("task", taskString)
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        alarmManager.cancel(intent)
        Log.d("tag_notif", "RemindersManager: ${task.title} stopReminder")

    }
}