package com.unsyiah.timemaster.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.unsyiah.timemaster.MainActivity
import com.unsyiah.timemaster.R

const val CLOCK_IN_PROGRESS_NOTIFICATION_ID = 0
const val CLOCK_IN_PROGRESS_REQUEST_CODE = 0
const val TIMER_COMPLETE_NOTIFICATION_ID = 1

fun getNotificationManager(context: Context) : NotificationManager {
    return ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    ) as NotificationManager
}

@RequiresApi(Build.VERSION_CODES.M)
fun NotificationManager.sendClockInProgressNotification(
    context: Context,
    taskName: String
) {
    val mainIntent = Intent(context, MainActivity::class.java)
    val pendingMainIntent = PendingIntent.getActivity(
        context,
        CLOCK_IN_PROGRESS_REQUEST_CODE,
        mainIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val clockInProgressNotification = NotificationCompat.Builder(
        context,
        context.getString(R.string.clock_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_check_circle_outline_24)
        .setContentTitle("Sedang dalam proses... ")
        .setContentText(taskName)
        .setContentIntent(pendingMainIntent)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)

    notify(CLOCK_IN_PROGRESS_NOTIFICATION_ID, clockInProgressNotification.build())
}

fun NotificationManager.cancelClockInProgressNotification() {
    cancel(CLOCK_IN_PROGRESS_NOTIFICATION_ID)
}

@RequiresApi(Build.VERSION_CODES.M)
fun NotificationManager.sendTimerCompleteNotification(
    context: Context,
    taskName: String
) {
    val mainIntent = Intent(context, MainActivity::class.java)
    val pendingMainIntent = PendingIntent.getActivity(
        context,
        0,
        mainIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val timerCompleteNotification = NotificationCompat.Builder(
        context,
        context.getString(R.string.alarm_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_check_circle_outline_24)
        .setContentTitle("Timer complete!")
        .setContentText("You finished $taskName. Good job!")
        .setContentIntent(pendingMainIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(
        TIMER_COMPLETE_NOTIFICATION_ID,
        timerCompleteNotification.build()
    )
}