package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "Alarm",
    val isEnabled: Boolean = true,
    val daysBitmask: Int = 0, // 0 = Once, 1=Sun, 2=Mon, 4=Tue, 8=Wed, 16=Thu, 32=Fri, 64=Sat
    val vibrate: Boolean = true,
    val snoozeMinutes: Int = 5
) {
    fun isRepeating(): Boolean = daysBitmask > 0

    fun isDayEnabled(dayOfWeek: Int): Boolean { // Calendar.SUNDAY=1..SATURDAY=7
        val bit = 1 shl (dayOfWeek - 1)
        return (daysBitmask and bit) != 0
    }

    fun getFormattedTime(use24Hour: Boolean = false): String {
        return if (use24Hour) {
            String.format("%02d:%02d", hour, minute)
        } else {
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val amPm = if (hour >= 12) "PM" else "AM"
            String.format("%02d:%02d %s", displayHour, minute, amPm)
        }
    }

    fun getDaysSummary(): String {
        if (daysBitmask == 0) return "Once"
        if (daysBitmask == 127) return "Every day"
        if (daysBitmask == 62) return "Weekdays"
        if (daysBitmask == 65) return "Weekends"

        val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val active = mutableListOf<String>()
        for (i in 0..6) {
            if ((daysBitmask and (1 shl i)) != 0) {
                active.add(days[i])
            }
        }
        return active.joinToString(", ")
    }
}
