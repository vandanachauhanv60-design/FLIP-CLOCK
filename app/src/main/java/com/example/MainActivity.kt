package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AlarmRingingDialog
import com.example.ui.components.FullscreenClockView
import com.example.ui.screens.AlarmScreen
import com.example.ui.screens.ClockScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.TodoScreen
import com.example.ui.theme.FlipAccentOrange
import com.example.ui.theme.FlipBackgroundDark
import com.example.ui.theme.FlipBackgroundLight
import com.example.ui.theme.FlipCardBorderDark
import com.example.ui.theme.FlipClockTheme
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            FlipClockTheme(darkTheme = isDarkMode) {
                // Request Notification Permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notifLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { /* Handled */ }
                    LaunchedEffect(Unit) {
                        notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
                val timeState by viewModel.timeState.collectAsStateWithLifecycle()
                val is24Hour by viewModel.is24Hour.collectAsStateWithLifecycle()
                val showSeconds by viewModel.showSeconds.collectAsStateWithLifecycle()
                val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
                val ringingAlarm by viewModel.ringingAlarm.collectAsStateWithLifecycle()
                val alarms by viewModel.alarms.collectAsStateWithLifecycle()
                val todos by viewModel.todos.collectAsStateWithLifecycle()
                val focusState by viewModel.focusSessionState.collectAsStateWithLifecycle()
                val playerState by viewModel.playerState.collectAsStateWithLifecycle()
                val activeAmbient by viewModel.activeAmbient.collectAsStateWithLifecycle()

                // Bedside Fullscreen View
                if (isFullscreen) {
                    FullscreenClockView(
                        hoursStr = timeState.hours,
                        minutesStr = timeState.minutes,
                        secondsStr = timeState.seconds,
                        amPmStr = timeState.amPm,
                        dateStr = timeState.dateString,
                        is24Hour = is24Hour,
                        showSeconds = showSeconds,
                        isDarkMode = isDarkMode,
                        onToggleTheme = { viewModel.toggleDarkMode() },
                        onExitFullscreen = { viewModel.setFullscreen(false) }
                    )
                } else {
                    val appBg = if (isDarkMode) FlipBackgroundDark else FlipBackgroundLight
                    val navBarBg = if (isDarkMode) Color(0xFF141417) else Color(0xFFEBEBF0)
                    val textColor = if (isDarkMode) Color.White else Color(0xFF18181B)

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = appBg,
                        contentWindowInsets = WindowInsets.statusBars,
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = "FLIP_CLOCK",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 3.sp,
                                        fontSize = 18.sp,
                                        color = textColor
                                    )
                                },
                                actions = {
                                    IconButton(
                                        onClick = { viewModel.toggleSoundMuted() },
                                        modifier = Modifier.testTag("app_bar_sound_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                            contentDescription = "Toggle Mute",
                                            tint = if (isMuted) Color(0xFF8E8E93) else FlipAccentOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.toggleDarkMode() },
                                        modifier = Modifier.testTag("app_bar_theme_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.Nightlight,
                                            contentDescription = "Toggle Theme",
                                            tint = if (isDarkMode) FlipAccentOrange else Color(0xFF18181B),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.setFullscreen(true) },
                                        modifier = Modifier.testTag("app_bar_fullscreen_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fullscreen,
                                            contentDescription = "Bedside Fullscreen",
                                            tint = textColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = appBg
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = navBarBg,
                                modifier = Modifier.testTag("main_bottom_nav")
                            ) {
                                AppNavTab.values().forEach { tab ->
                                    val isSelected = currentTab == tab
                                    val icon = when (tab) {
                                        AppNavTab.CLOCK -> Icons.Default.Schedule
                                        AppNavTab.FOCUS -> Icons.Default.HourglassBottom
                                        AppNavTab.TODO -> Icons.Default.Checklist
                                        AppNavTab.ALARMS -> Icons.Default.Alarm
                                    }

                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.setNavTab(tab) },
                                        icon = { Icon(icon, contentDescription = tab.title) },
                                        label = {
                                            Text(
                                                text = tab.title,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            selectedTextColor = FlipAccentOrange,
                                            indicatorColor = FlipAccentOrange,
                                            unselectedIconColor = Color(0xFF8E8E93),
                                            unselectedTextColor = Color(0xFF8E8E93)
                                        ),
                                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                AppNavTab.CLOCK -> {
                                    ClockScreen(
                                        hoursStr = timeState.hours,
                                        minutesStr = timeState.minutes,
                                        secondsStr = timeState.seconds,
                                        amPmStr = timeState.amPm,
                                        dateStr = timeState.dateString,
                                        is24Hour = is24Hour,
                                        onToggle24Hour = { viewModel.toggle24Hour() },
                                        showSeconds = showSeconds,
                                        onToggleShowSeconds = { viewModel.toggleShowSeconds() },
                                        isDarkMode = isDarkMode,
                                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                                        isMuted = isMuted,
                                        onToggleMuted = { viewModel.toggleSoundMuted() },
                                        onEnterFullscreen = { viewModel.setFullscreen(true) },
                                        nextAlarmText = viewModel.getNextAlarmFormatted(),
                                        playerState = playerState,
                                        onSelectMusicService = { viewModel.selectMusicService(it) },
                                        onPlayPauseMusic = { viewModel.togglePlayPauseMusic() },
                                        onStopMusic = { viewModel.stopMusic() },
                                        onNextSong = { viewModel.nextSong() },
                                        onPrevSong = { viewModel.previousSong() },
                                        onOpenMusicApp = { viewModel.openMusicApp() },
                                        onSelectPlaylist = { viewModel.openMusicPlaylist(it) },
                                        activeAmbient = activeAmbient,
                                        onSelectAmbient = { viewModel.selectAmbientSound(it) }
                                    )
                                }
                                AppNavTab.FOCUS -> {
                                    FocusScreen(
                                        focusState = focusState,
                                        todos = todos,
                                        onStartTimer = { viewModel.startFocusTimer() },
                                        onPauseTimer = { viewModel.pauseFocusTimer() },
                                        onResetTimer = { viewModel.resetFocusTimer() },
                                        onSkipTimer = { viewModel.skipFocusTimer() },
                                        onSelectPreset = { viewModel.selectFocusPreset(it) },
                                        onSetCustomDuration = { viewModel.setCustomFocusDuration(it) },
                                        onSelectActiveTask = { id, title -> viewModel.setActiveFocusTask(id, title) },
                                        playerState = playerState,
                                        onSelectMusicService = { viewModel.selectMusicService(it) },
                                        onPlayPauseMusic = { viewModel.togglePlayPauseMusic() },
                                        onStopMusic = { viewModel.stopMusic() },
                                        onNextSong = { viewModel.nextSong() },
                                        onPrevSong = { viewModel.previousSong() },
                                        onOpenMusicApp = { viewModel.openMusicApp() },
                                        onSelectPlaylist = { viewModel.openMusicPlaylist(it) },
                                        activeAmbient = activeAmbient,
                                        onSelectAmbient = { viewModel.selectAmbientSound(it) },
                                        isDarkMode = isDarkMode
                                    )
                                }
                                AppNavTab.TODO -> {
                                    TodoScreen(
                                        todos = todos,
                                        onAddTodo = { title, est, cat -> viewModel.addTodo(title, est, cat) },
                                        onToggleTodo = { viewModel.toggleTodo(it) },
                                        onDeleteTodo = { viewModel.deleteTodo(it) },
                                        onFocusTask = { viewModel.focusOnTask(it) },
                                        isDarkMode = isDarkMode
                                    )
                                }
                                AppNavTab.ALARMS -> {
                                    AlarmScreen(
                                        alarms = alarms,
                                        onAddAlarm = { h, m, l, b, v, s -> viewModel.addAlarm(h, m, l, b, v, s) },
                                        onToggleAlarm = { alarm, enabled -> viewModel.toggleAlarm(alarm, enabled) },
                                        onDeleteAlarm = { viewModel.deleteAlarm(it) },
                                        is24Hour = is24Hour,
                                        isDarkMode = isDarkMode
                                    )
                                }
                            }
                        }
                    }
                }

                // Modal Alarm Ringing Alert Overlay
                ringingAlarm?.let { activeAlarm ->
                    AlarmRingingDialog(
                        activeAlarm = activeAlarm,
                        onSnooze = { viewModel.snoozeRingingAlarm() },
                        onDismiss = { viewModel.dismissRingingAlarm() }
                    )
                }
            }
        }
    }
}
