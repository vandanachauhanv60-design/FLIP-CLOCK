package com.example.focus

import android.os.CountDownTimer
import com.example.util.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FocusSessionState(
    val selectedPreset: FocusPreset = DEFAULT_FOCUS_PRESETS.first(),
    val focusState: FocusState = FocusState.IDLE,
    val secondsRemaining: Long = 25 * 60L,
    val totalSeconds: Long = 25 * 60L,
    val currentCycle: Int = 1,
    val totalCycles: Int = 4,
    val isBreak: Boolean = false,
    val activeTaskId: Long? = null,
    val activeTaskTitle: String? = null
) {
    val progress: Float
        get() = if (totalSeconds > 0) {
            1f - (secondsRemaining.toFloat() / totalSeconds.toFloat())
        } else 0f
}

class FocusTimerManager(
    private val onSessionCompleted: (taskId: Long?) -> Unit = {}
) {
    private val _state = MutableStateFlow(FocusSessionState())
    val state: StateFlow<FocusSessionState> = _state.asStateFlow()

    private var timer: CountDownTimer? = null

    fun selectPreset(preset: FocusPreset) {
        stopTimer()
        val totalSec = preset.workMinutes * 60L
        _state.value = _state.value.copy(
            selectedPreset = preset,
            focusState = FocusState.IDLE,
            secondsRemaining = totalSec,
            totalSeconds = totalSec,
            currentCycle = 1,
            totalCycles = preset.cycles,
            isBreak = false
        )
    }

    fun setCustomDuration(minutes: Int) {
        val safeMinutes = minutes.coerceIn(1, 180)
        val customPreset = FocusPreset(
            id = "custom_$safeMinutes",
            name = "Custom ${safeMinutes}m",
            workMinutes = safeMinutes,
            breakMinutes = 5,
            cycles = 1,
            description = "Custom timer: $safeMinutes minutes"
        )
        selectPreset(customPreset)
    }

    fun setActiveTask(taskId: Long?, taskTitle: String?) {
        _state.value = _state.value.copy(
            activeTaskId = taskId,
            activeTaskTitle = taskTitle
        )
    }

    fun start() {
        val currentState = _state.value
        if (currentState.focusState == FocusState.RUNNING || currentState.focusState == FocusState.BREAK_RUNNING) {
            return
        }

        val isBreak = currentState.isBreak
        _state.value = currentState.copy(
            focusState = if (isBreak) FocusState.BREAK_RUNNING else FocusState.RUNNING
        )

        startCountdown(currentState.secondsRemaining)
    }

    fun pause() {
        val currentState = _state.value
        timer?.cancel()
        timer = null
        _state.value = currentState.copy(
            focusState = if (currentState.isBreak) FocusState.BREAK_PAUSED else FocusState.PAUSED
        )
    }

    fun reset() {
        stopTimer()
        val preset = _state.value.selectedPreset
        val totalSec = preset.workMinutes * 60L
        _state.value = _state.value.copy(
            focusState = FocusState.IDLE,
            secondsRemaining = totalSec,
            totalSeconds = totalSec,
            currentCycle = 1,
            isBreak = false
        )
    }

    fun skipCurrent() {
        stopTimer()
        handleIntervalFinish()
    }

    private fun startCountdown(durationSec: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(durationSec * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = (millisUntilFinished / 1000L) + 1
                _state.value = _state.value.copy(secondsRemaining = sec)
            }

            override fun onFinish() {
                _state.value = _state.value.copy(secondsRemaining = 0)
                handleIntervalFinish()
            }
        }.start()
    }

    private fun handleIntervalFinish() {
        val current = _state.value
        SoundManager.playTimerCompletionChime()

        if (!current.isBreak) {
            // Work interval finished!
            onSessionCompleted(current.activeTaskId)

            // Check if there is a break configured
            if (current.selectedPreset.breakMinutes > 0) {
                val breakSec = current.selectedPreset.breakMinutes * 60L
                _state.value = current.copy(
                    isBreak = true,
                    focusState = FocusState.BREAK_RUNNING,
                    secondsRemaining = breakSec,
                    totalSeconds = breakSec
                )
                startCountdown(breakSec)
            } else {
                finishCycleOrAdvance()
            }
        } else {
            // Break finished! Move to next cycle or complete
            finishCycleOrAdvance()
        }
    }

    private fun finishCycleOrAdvance() {
        val current = _state.value
        if (current.currentCycle < current.totalCycles) {
            val nextCycle = current.currentCycle + 1
            val workSec = current.selectedPreset.workMinutes * 60L
            _state.value = current.copy(
                isBreak = false,
                currentCycle = nextCycle,
                focusState = FocusState.RUNNING,
                secondsRemaining = workSec,
                totalSeconds = workSec
            )
            startCountdown(workSec)
        } else {
            // All cycles complete!
            _state.value = current.copy(
                focusState = FocusState.COMPLETED,
                isBreak = false,
                secondsRemaining = 0
            )
            stopTimer()
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }
}
