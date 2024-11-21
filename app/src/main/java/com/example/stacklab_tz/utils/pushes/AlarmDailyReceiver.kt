package com.example.stacklab_tz.utils.pushes

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.stacklab_tz.R
import com.example.stacklab_tz.db.entities.Task
import com.example.stacklab_tz.utils.sendReminderNotification
import com.google.gson.Gson

class AlarmDailyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        val title = intent.getStringExtra("title")
        val taskString = intent.getStringExtra("task")
        val task = Gson().fromJson(taskString, Task::class.java)
        notificationManager.sendReminderNotification(
            applicationContext = context,
            channelId = context.getString(R.string.reminders_notification_channel_id),
            title = title ?: ""
        )

        Log.d("tag_notif", "AlarmDailyReceiver onReceive: ${task.title} show notif")
        RemindersManager.startDailyReminder(
            context = context.applicationContext,
            task = task,
            reminderId = task.id
        )
        Log.d("tag_notif", "AlarmDailyReceiver onReceive: ${task.title} startReminder")
    }
}
