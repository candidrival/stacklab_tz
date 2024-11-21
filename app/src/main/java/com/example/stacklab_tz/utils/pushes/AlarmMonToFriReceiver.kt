package com.example.stacklab_tz.utils.pushes

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.stacklab_tz.R
import com.example.stacklab_tz.db.entities.Task
import com.example.stacklab_tz.utils.sendReminderNotification
import com.google.gson.Gson

class AlarmMonToFriReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        val title = intent.getStringExtra("title")
        val taskString = intent.getStringExtra("task")
        val task = Gson().fromJson(taskString, Task::class.java)
        val calendar = Calendar.getInstance()
        if(calendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY
            || calendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY )
        {
            notificationManager.sendReminderNotification(
                applicationContext = context,
                channelId = context.getString(R.string.reminders_notification_channel_id),
                title = title ?: ""
            )
        }

        Log.d("tag_notif", "AlarmMonToFriReceiver onReceive: ${task.title} show notif")
        RemindersManager.startMonToFriReminder(
            context = context.applicationContext,
            task = task,
            reminderId = task.id
        )
        Log.d("tag_notif", "AlarmMonToFriReceiver onReceive: ${task.title} startReminder")
    }
}
