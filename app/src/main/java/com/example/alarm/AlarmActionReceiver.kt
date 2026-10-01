package com.example.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, 0L)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Stop audio and vibration
        AlarmStateManager.dismissAlarm()
        notificationManager.cancel(alarmId.toInt())

        when (intent.action) {
            ACTION_SNOOZE -> {
                val label = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_LABEL) ?: "Alarm"
                val snoozeMin = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_SNOOZE_MIN, 5)
                val scheduler = AlarmScheduler(context)
                scheduler.scheduleSnooze(alarmId, label, snoozeMin)
            }
            ACTION_DISMISS -> {
                // Already dismissed
            }
        }
    }

    companion object {
        const val ACTION_SNOOZE = "com.example.action.ALARM_SNOOZE"
        const val ACTION_DISMISS = "com.example.action.ALARM_DISMISS"
    }
}
