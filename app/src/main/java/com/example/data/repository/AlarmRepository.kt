package com.example.data.repository

import com.example.data.dao.AlarmDao
import com.example.data.model.AlarmEntity
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {
    val allAlarms: Flow<List<AlarmEntity>> = alarmDao.getAllAlarms()
    val activeAlarms: Flow<List<AlarmEntity>> = alarmDao.getActiveAlarms()

    suspend fun getAlarmById(id: Long): AlarmEntity? = alarmDao.getAlarmById(id)

    suspend fun insert(alarm: AlarmEntity): Long = alarmDao.insertAlarm(alarm)

    suspend fun update(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)

    suspend fun delete(alarm: AlarmEntity) = alarmDao.deleteAlarm(alarm)

    suspend fun deleteById(id: Long) = alarmDao.deleteAlarmById(id)

    suspend fun toggleAlarm(alarm: AlarmEntity, enabled: Boolean) {
        alarmDao.updateAlarm(alarm.copy(isEnabled = enabled))
    }
}
