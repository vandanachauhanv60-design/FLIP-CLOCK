package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TodoEntity
import com.example.focus.DEFAULT_FOCUS_PRESETS
import com.example.focus.FocusPreset
import com.example.focus.FocusSessionState
import com.example.focus.FocusState
import com.example.music.MusicPlayerState
import com.example.music.MusicService
import com.example.ui.components.FlipColon
import com.example.ui.components.FlipDigitPair
import com.example.ui.components.MusicPlayerCard
import com.example.ui.theme.FlipAccentGreen
import com.example.ui.theme.FlipAccentOrange
import com.example.ui.theme.FlipCardBorderDark
import com.example.ui.theme.FlipCardDark
import com.example.ui.theme.FlipTextMuted
import com.example.util.SoundManager

@Composable
fun FocusScreen(
    focusState: FocusSessionState,
    todos: List<TodoEntity>,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit,
    onSelectPreset: (FocusPreset) -> Unit,
    onSetCustomDuration: (Int) -> Unit,
    onSelectActiveTask: (Long?, String?) -> Unit,
    playerState: MusicPlayerState,
    onSelectMusicService: (MusicService) -> Unit,
    onPlayPauseMusic: () -> Unit,
    onStopMusic: () -> Unit,
    onNextSong: () -> Unit,
    onPrevSong: () -> Unit,
    onOpenMusicApp: () -> Unit,
    onSelectPlaylist: (String) -> Unit,
    activeAmbient: SoundManager.AmbientType,
    onSelectAmbient: (SoundManager.AmbientType) -> Unit,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var customMinutesText by remember { mutableStateOf("30") }
    var showTaskPicker by remember { mutableStateOf(false) }

    val textColor = if (isDarkMode) Color.White else Color(0xFF18181B)
    val mutedColor = if (isDarkMode) FlipTextMuted else Color(0xFF71717A)

    val minutes = (focusState.secondsRemaining / 60).toInt()
    val seconds = (focusState.secondsRemaining % 60).toInt()
    val minStr = String.format("%02d", minutes)
    val secStr = String.format("%02d", seconds)

    val isRunning = focusState.focusState == FocusState.RUNNING || focusState.focusState == FocusState.BREAK_RUNNING

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Focus Mode Status Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (focusState.isBreak) FlipAccentGreen.copy(alpha = 0.15f)
                        else FlipAccentOrange.copy(alpha = 0.15f)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                if (focusState.isBreak) FlipAccentGreen else FlipAccentOrange,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (focusState.isBreak) "☕ BREAK TIME (5 MIN)" else "⚡ FOCUS SESSION (25 MIN)",
                        color = if (focusState.isBreak) FlipAccentGreen else FlipAccentOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "Cycle ${focusState.currentCycle}/${focusState.totalCycles}",
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Task Card / Selector
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8), RoundedCornerShape(12.dp))
                    .clickable { showTaskPicker = true },
                color = if (isDarkMode) FlipCardDark else Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = if (focusState.activeTaskId != null) FlipAccentOrange else mutedColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Focusing on task:",
                                color = mutedColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = focusState.activeTaskTitle ?: "No task selected (Tap to link)",
                                color = textColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Change",
                        color = FlipAccentOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Giant Split Flap Timer Display
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8), RoundedCornerShape(18.dp))
                    .testTag("focus_flip_timer"),
                color = if (isDarkMode) Color(0xFF141417) else Color(0xFFEBEBF0),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 22.dp, horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Minutes Digits
                        FlipDigitPair(
                            value = minStr,
                            cardWidth = 72.dp,
                            cardHeight = 108.dp,
                            fontSize = 62.sp,
                            isDarkMode = isDarkMode
                        )

                        FlipColon(
                            isDarkMode = isDarkMode,
                            dotSize = 8.dp
                        )

                        // Seconds Digits
                        FlipDigitPair(
                            value = secStr,
                            cardWidth = 72.dp,
                            cardHeight = 108.dp,
                            fontSize = 62.sp,
                            isDarkMode = isDarkMode
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Progress indicator
                    LinearProgressIndicator(
                        progress = { focusState.progress },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (focusState.isBreak) FlipAccentGreen else FlipAccentOrange,
                        trackColor = if (isDarkMode) Color(0xFF2C2C32) else Color(0xFFD1D1D8)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Timer Control Buttons: Reset, Play/Pause, Skip
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset
                        IconButton(
                            onClick = onResetTimer,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isDarkMode) Color(0xFF222226) else Color(0xFFDFDFE5))
                                .testTag("focus_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Timer",
                                tint = textColor
                            )
                        }

                        // Play / Pause Primary Action
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (focusState.isBreak) FlipAccentGreen else FlipAccentOrange)
                                .clickable {
                                    if (isRunning) onPauseTimer() else onStartTimer()
                                }
                                .testTag("focus_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRunning) "Pause" else "Start",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Skip to next session/break
                        IconButton(
                            onClick = onSkipTimer,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isDarkMode) Color(0xFF222226) else Color(0xFFDFDFE5))
                                .testTag("focus_skip_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Skip Session",
                                tint = textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer Presets Section ("There are many timers")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TIMER PRESETS",
                    color = mutedColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontFamily = FontFamily.Monospace
                )

                TextButton(
                    onClick = { showCustomDialog = true },
                    modifier = Modifier.testTag("custom_timer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreTime,
                        contentDescription = null,
                        tint = FlipAccentOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Custom",
                        color = FlipAccentOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(DEFAULT_FOCUS_PRESETS) { preset ->
                    val isSelected = focusState.selectedPreset.id == preset.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) FlipAccentOrange else if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectPreset(preset) },
                        color = if (isSelected) {
                            if (isDarkMode) Color(0xFF241D17) else Color(0xFFFFF3E0)
                        } else {
                            if (isDarkMode) FlipCardDark else Color.White
                        }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                            Text(
                                text = preset.name,
                                color = if (isSelected) FlipAccentOrange else textColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (preset.breakMinutes > 0)
                                    "${preset.workMinutes}m work + ${preset.breakMinutes}m break"
                                else "${preset.workMinutes}m timer",
                                color = mutedColor,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Music Controller Bar for Spotify & YT Music
            MusicPlayerCard(
                playerState = playerState,
                onSelectService = onSelectMusicService,
                onPlayPause = onPlayPauseMusic,
                onStop = onStopMusic,
                onNext = onNextSong,
                onPrevious = onPrevSong,
                onOpenApp = onOpenMusicApp,
                onSelectPlaylist = onSelectPlaylist,
                activeAmbient = activeAmbient,
                onSelectAmbient = onSelectAmbient,
                isDarkMode = isDarkMode
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Custom Timer Minutes Dialog
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Set Custom Timer") },
            text = {
                Column {
                    Text(
                        "Enter duration in minutes (1 to 180):",
                        fontSize = 13.sp,
                        color = mutedColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customMinutesText,
                        onValueChange = { customMinutesText = it.filter { char -> char.isDigit() } },
                        label = { Text("Minutes") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_minutes_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = customMinutesText.toIntOrNull() ?: 25
                        onSetCustomDuration(mins)
                        showCustomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlipAccentOrange)
                ) {
                    Text("Set Timer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Task Picker Dialog
    if (showTaskPicker) {
        AlertDialog(
            onDismissRequest = { showTaskPicker = false },
            title = { Text("Select Task to Focus") },
            text = {
                LazyColumn(modifier = Modifier.height(250.dp)) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectActiveTask(null, null)
                                    showTaskPicker = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Text("None (General Focus)", color = mutedColor, fontSize = 14.sp)
                        }
                    }
                    items(todos) { todo ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectActiveTask(todo.id, todo.title)
                                    showTaskPicker = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (todo.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = FlipAccentGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Column {
                                    Text(
                                        text = todo.title,
                                        color = textColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "🍅 ${todo.pomodorosCompleted}/${todo.pomodorosEstimated} sessions",
                                        color = mutedColor,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTaskPicker = false }) {
                    Text("Close")
                }
            }
        )
    }
}
