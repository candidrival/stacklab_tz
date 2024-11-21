package com.example.stacklab_tz.utils

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.stacklab_tz.MainActivity
import com.example.stacklab_tz.R
import com.example.stacklab_tz.db.entities.Task
import kotlin.random.Random


private var doubleBackToExitPressedOnce = false

fun doubleClickExit(activity: Activity) {
    if (doubleBackToExitPressedOnce) {
        activity.finishAndRemoveTask()
        return
    }

    doubleBackToExitPressedOnce = true
    showToast(activity, "Please click BACK again to exit")

    Handler(Looper.getMainLooper()).postDelayed(
        { doubleBackToExitPressedOnce = false },
        2000
    )
}

private var isToastDisplayed = false
fun showToast(context: Context, message: String) {
    if (!isToastDisplayed) {
        isToastDisplayed = true

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            isToastDisplayed = false
        }, 3000)
    }
}


fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String,
    title: String
) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        1,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setContentTitle(title)
        .setContentText(applicationContext.getString(R.string.push_desk))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
    Log.d("tag_notif", "sendReminderNotification: $title show")
}

const val NOTIFICATION_ID = 1