package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarm.ActiveAlarmState
import com.example.alarm.AlarmScheduler
import com.example.alarm.AlarmStateManager
import com.example.data.AppDatabase
import com.example.data.model.AlarmEntity
import com.example.data.model.TodoEntity
import com.example.data.repository.AlarmRepository
import com.example.data.repository.TodoRepository
import com.example.focus.FocusPreset
import com.example.focus.FocusSessionState
import com.example.focus.FocusTimerManager
import com.example.music.MediaManager
import com.example.music.MusicPlayerState
import com.example.music.MusicService
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class AppNavTab(val title: String) {
    CLOCK("Clock"),
    FOCUS("Focus"),
    TODO("To-Do"),
    ALARMS("Alarms")
}

data class TimeState(
    val hours: String = "12",
    val minutes: String = "00",
    val seconds: String = "00",
    val amPm: String = "AM",
    val dateString: String = "Thu, Oct 1"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val alarmRepository = AlarmRepository(db.alarmDao())
    private val todoRepository = TodoRepository(db.todoDao())
    private val alarmScheduler = AlarmScheduler(application)
    private val mediaManager = MediaManager(application)

    // Current Time Flow
    private val _timeState = MutableStateFlow(TimeState())
    val timeState: StateFlow<TimeState> = _timeState.asStateFlow()

    // Clock Settings
    private val _is24Hour = MutableStateFlow(false)
    val is24Hour: StateFlow<Boolean> = _is24Hour.asStateFlow()

    private val _showSeconds = MutableStateFlow(true)
    val showSeconds: StateFlow<Boolean> = _showSeconds.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(AppNavTab.CLOCK)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Active Ambient Focus sound
    private val _activeAmbient = MutableStateFlow(SoundManager.AmbientType.NONE)
    val activeAmbient: StateFlow<SoundManager.AmbientType> = _activeAmbient.asStateFlow()

    // Alarms Flow from DB
    val alarms: StateFlow<List<AlarmEntity>> = alarmRepository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // To-Dos Flow from DB
    val todos: StateFlow<List<TodoEntity>> = todoRepository.allTodos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Ringing Alarm from AlarmStateManager
    val ringingAlarm: StateFlow<ActiveAlarmState?> = AlarmStateManager.ringingAlarm

    // Media Manager Player State
    val playerState: StateFlow<MusicPlayerState> = mediaManager.playerState

    // Focus Timer Engine
    private val focusTimerManager = FocusTimerManager(
        onSessionCompleted = { taskId ->
            if (taskId != null) {
                viewModelScope.launch {
                    todoRepository.incrementPomodoro(taskId)
                }
            }
        }
    )
    val focusSessionState: StateFlow<FocusSessionState> = focusTimerManager.state

    init {
        // Start Time ticker
        startTimeTicker()
        // Ensure default alarms and tasks if first run
        initializeDefaultsIfEmpty()
    }

    private fun startTimeTicker() {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            while (isActive) {
                val cal = Calendar.getInstance()
                val hour24 = cal.get(Calendar.HOUR_OF_DAY)
                val hour12 = cal.get(Calendar.HOUR)
                val displayHour = if (_is24Hour.value) {
                    String.format("%02d", hour24)
                } else {
                    val h = if (hour12 == 0) 12 else hour12
                    String.format("%02d", h)
                }
                val minute = String.format("%02d", cal.get(Calendar.MINUTE))
                val second = String.format("%02d", cal.get(Calendar.SECOND))
                val amPm = if (hour24 >= 12) "PM" else "AM"
                val dateStr = dateFormat.format(cal.time)

                _timeState.value = TimeState(
                    hours = displayHour,
                    minutes = minute,
                    seconds = second,
                    amPm = amPm,
                    dateString = dateStr
                )
                delay(1000)
            }
        }
    }

    private fun initializeDefaultsIfEmpty() {
        viewModelScope.launch {
            // Seed a sample task if list is empty
            if (todoRepository.getTodoById(1) == null) {
                todoRepository.insert(
                    TodoEntity(
                        title = "Finish Deep Work Focus Session",
                        pomodorosEstimated = 4,
                        pomodorosCompleted = 1,
                        category = "Deep Work"
                    )
                )
                todoRepository.insert(
                    TodoEntity(
                        title = "Review FLIP_CLOCK Features & Spotify Controls",
                        pomodorosEstimated = 2,
                        pomodorosCompleted = 0,
                        category = "Study"
                    )
                )
            }
            // Seed default morning alarm if empty
            if (alarmRepository.getAlarmById(1) == null) {
                val defaultAlarm = AlarmEntity(
                    hour = 7,
                    minute = 30,
                    label = "Morning Alarm",
                    isEnabled = true,
                    daysBitmask = 62 // Weekdays Mon-Fri
                )
                val id = alarmRepository.insert(defaultAlarm)
                alarmScheduler.schedule(defaultAlarm.copy(id = id))
            }
        }
    }

    fun setNavTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun toggle24Hour() {
        _is24Hour.value = !_is24Hour.value
    }

    fun toggleShowSeconds() {
        _showSeconds.value = !_showSeconds.value
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleSoundMuted() {
        _isMuted.value = !_isMuted.value
        SoundManager.isSoundEffectsEnabled = !_isMuted.value
    }

    fun setFullscreen(fullscreen: Boolean) {
        _isFullscreen.value = fullscreen
    }

    // --- ALARMS ---
    fun addAlarm(hour: Int, minute: Int, label: String, daysBitmask: Int, vibrate: Boolean, snoozeMin: Int) {
        viewModelScope.launch {
            val newAlarm = AlarmEntity(
                hour = hour,
                minute = minute,
                label = label.ifBlank { "Alarm" },
                isEnabled = true,
                daysBitmask = daysBitmask,
                vibrate = vibrate,
                snoozeMinutes = snoozeMin
            )
            val id = alarmRepository.insert(newAlarm)
            val scheduledAlarm = newAlarm.copy(id = id)
            alarmScheduler.schedule(scheduledAlarm)
        }
    }

    fun toggleAlarm(alarm: AlarmEntity, enabled: Boolean) {
        viewModelScope.launch {
            alarmRepository.toggleAlarm(alarm, enabled)
            if (enabled) {
                alarmScheduler.schedule(alarm.copy(isEnabled = true))
            } else {
                alarmScheduler.cancel(alarm.id)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm.id)
            alarmRepository.delete(alarm)
        }
    }

    fun snoozeRingingAlarm() {
        val ringing = ringingAlarm.value ?: return
        AlarmStateManager.dismissAlarm()
        alarmScheduler.scheduleSnooze(ringing.id, ringing.label, ringing.snoozeMinutes)
    }

    fun dismissRingingAlarm() {
        AlarmStateManager.dismissAlarm()
    }

    fun getNextAlarmFormatted(): String? {
        val active = alarms.value.filter { it.isEnabled }
        if (active.isEmpty()) return null

        var earliestTime: Long = Long.MAX_VALUE
        var nextAlarm: AlarmEntity? = null
        for (alarm in active) {
            val trigger = alarmScheduler.calculateNextTriggerTime(alarm)
            if (trigger < earliestTime) {
                earliestTime = trigger
                nextAlarm = alarm
            }
        }

        if (nextAlarm == null || earliestTime == Long.MAX_VALUE) return null

        val diffMs = earliestTime - System.currentTimeMillis()
        if (diffMs <= 0) return null

        val hours = (diffMs / (1000 * 60 * 60)).toInt()
        val minutes = ((diffMs / (1000 * 60)) % 60).toInt()

        return if (hours > 0) {
            "⏰ Next in ${hours}h ${minutes}m (${nextAlarm.getFormattedTime(_is24Hour.value)})"
        } else {
            "⏰ Next in ${minutes}m (${nextAlarm.getFormattedTime(_is24Hour.value)})"
        }
    }

    // --- FOCUS TIMERS ---
    fun startFocusTimer() = focusTimerManager.start()
    fun pauseFocusTimer() = focusTimerManager.pause()
    fun resetFocusTimer() = focusTimerManager.reset()
    fun skipFocusTimer() = focusTimerManager.skipCurrent()
    fun selectFocusPreset(preset: FocusPreset) = focusTimerManager.selectPreset(preset)
    fun setCustomFocusDuration(minutes: Int) = focusTimerManager.setCustomDuration(minutes)
    fun setActiveFocusTask(taskId: Long?, taskTitle: String?) = focusTimerManager.setActiveTask(taskId, taskTitle)

    // --- TO-DO LIST ---
    fun addTodo(title: String, estPomodoros: Int, category: String) {
        viewModelScope.launch {
            todoRepository.insert(
                TodoEntity(
                    title = title,
                    pomodorosEstimated = estPomodoros,
                    category = category
                )
            )
        }
    }

    fun toggleTodo(todo: TodoEntity) {
        viewModelScope.launch {
            todoRepository.toggleCompleted(todo)
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            todoRepository.delete(todo)
        }
    }

    fun focusOnTask(todo: TodoEntity) {
        focusTimerManager.setActiveTask(todo.id, todo.title)
        _currentTab.value = AppNavTab.FOCUS
    }

    // --- MUSIC & AMBIENT ---
    fun selectMusicService(service: MusicService) = mediaManager.setSelectedService(service)
    fun playMusic() = mediaManager.play()
    fun pauseMusic() = mediaManager.pause()
    fun togglePlayPauseMusic() = mediaManager.togglePlayPause()
    fun stopMusic() = mediaManager.stop()
    fun nextSong() = mediaManager.next()
    fun previousSong() = mediaManager.previous()
    fun openMusicApp() = mediaManager.openMusicApp()
    fun openMusicPlaylist(query: String) = mediaManager.openFocusPlaylist(query)

    fun selectAmbientSound(type: SoundManager.AmbientType) {
        SoundManager.setAmbient(type)
        _activeAmbient.value = SoundManager.activeAmbientType
    }
}
