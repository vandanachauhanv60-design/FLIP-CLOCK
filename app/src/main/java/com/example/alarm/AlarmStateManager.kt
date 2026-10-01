package com.example.alarm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ActiveAlarmState(
    val id: Long,
    val label: String,
    val hour: Int,
    val minute: Int,
    val snoozeMinutes: Int
)

object AlarmStateManager {
    private val _ringingAlarm = MutableStateFlow<ActiveAlarmState?>(null)
    val ringingAlarm: StateFlow<ActiveAlarmState?> = _ringingAlarm.asStateFlow()

    fun triggerAlarm(alarm: ActiveAlarmState) {
        _ringingAlarm.value = alarm
    }

    fun dismissAlarm() {
        _ringingAlarm.value = null
        AlarmAudioPlayer.stop()
    }
}
