package com.unsyiah.timemaster.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.unsyiah.timemaster.util.cancelClockInProgressNotification
import com.unsyiah.timemaster.util.sendTimerCompleteNotification

class AlarmReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelClockInProgressNotification()
        notificationManager.sendTimerCompleteNotification(
            context,
            intent.getStringExtra("taskName") ?: "your task"
        )
    }
}