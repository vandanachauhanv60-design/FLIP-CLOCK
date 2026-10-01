package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, 0L)
        val label = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_LABEL) ?: "Alarm"
        val hour = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_HOUR, 0)
        val minute = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_MINUTE, 0)
        val vibrate = intent.getBooleanExtra(AlarmScheduler.EXTRA_ALARM_VIBRATE, true)
        val snoozeMin = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_SNOOZE_MIN, 5)

        // Update active in-app state
        AlarmStateManager.triggerAlarm(
            ActiveAlarmState(
                id = alarmId,
                label = label,
                hour = hour,
                minute = minute,
                snoozeMinutes = snoozeMin
            )
        )

        // Start ringing sound & vibration
        AlarmAudioPlayer.start(context, vibrate)

        // Show High Priority Notification with Snooze & Dismiss actions
        showNotification(context, alarmId, label, hour, minute, snoozeMin)
    }

    private fun showNotification(
        context: Context,
        alarmId: Long,
        label: String,
        hour: Int,
        minute: Int,
        snoozeMin: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "flip_clock_alarm_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FLIP_CLOCK Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Flip Clock Alarm notifications"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Open app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("ALARM_RINGING", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze intent
        val snoozeIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_SNOOZE
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmScheduler.EXTRA_ALARM_LABEL, label)
            putExtra(AlarmScheduler.EXTRA_ALARM_SNOOZE_MIN, snoozeMin)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt() + 10000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss intent
        val dismissIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_DISMISS
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt() + 20000,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeFormatted = String.format("%02d:%02d", hour, minute)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ $label ($timeFormatted)")
            .setContentText("Flip Clock Alarm is ringing!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(0, "Snooze ($snoozeMin min)", snoozePendingIntent)
            .addAction(0, "Dismiss", dismissPendingIntent)

        notificationManager.notify(alarmId.toInt(), builder.build())
    }
}
