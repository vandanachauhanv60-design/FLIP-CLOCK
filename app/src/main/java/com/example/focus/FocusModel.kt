package com.example.focus

enum class FocusState {
    IDLE,
    RUNNING,
    PAUSED,
    BREAK_RUNNING,
    BREAK_PAUSED,
    COMPLETED
}

data class FocusPreset(
    val id: String,
    val name: String,
    val workMinutes: Int,
    val breakMinutes: Int,
    val cycles: Int,
    val description: String
)

val DEFAULT_FOCUS_PRESETS = listOf(
    FocusPreset(
        id = "classic_pomodoro",
        name = "Classic 25/5",
        workMinutes = 25,
        breakMinutes = 5,
        cycles = 4,
        description = "Standard Pomodoro: 25 min focus, 5 min break"
    ),
    FocusPreset(
        id = "deep_work",
        name = "Deep Work 50/10",
        workMinutes = 50,
        breakMinutes = 10,
        cycles = 3,
        description = "Intense focus session: 50 min focus, 10 min break"
    ),
    FocusPreset(
        id = "sprint_15",
        name = "Quick Sprint 15/3",
        workMinutes = 15,
        breakMinutes = 3,
        cycles = 4,
        description = "Fast momentum booster: 15 min focus, 3 min break"
    ),
    FocusPreset(
        id = "extended_study",
        name = "Study 45/15",
        workMinutes = 45,
        breakMinutes = 15,
        cycles = 3,
        description = "Academic study sessions: 45 min focus, 15 min break"
    ),
    FocusPreset(
        id = "power_nap",
        name = "Power Nap 20m",
        workMinutes = 20,
        breakMinutes = 0,
        cycles = 1,
        description = "Quick restorative rest: 20 minutes"
    ),
    FocusPreset(
        id = "quick_timer_5",
        name = "Quick Break 5m",
        workMinutes = 5,
        breakMinutes = 0,
        cycles = 1,
        description = "5 minute breather timer"
    )
)
