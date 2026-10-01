package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.MusicPlayerState
import com.example.music.MusicService
import com.example.ui.components.FlipClockDisplay
import com.example.ui.components.MusicPlayerCard
import com.example.ui.theme.FlipAccentOrange
import com.example.ui.theme.FlipCardBorderDark
import com.example.ui.theme.FlipCardDark
import com.example.ui.theme.FlipTextMuted
import com.example.util.SoundManager

@Composable
fun ClockScreen(
    hoursStr: String,
    minutesStr: String,
    secondsStr: String,
    amPmStr: String,
    dateStr: String,
    is24Hour: Boolean,
    onToggle24Hour: () -> Unit,
    showSeconds: Boolean,
    onToggleShowSeconds: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    isMuted: Boolean,
    onToggleMuted: () -> Unit,
    onEnterFullscreen: () -> Unit,
    nextAlarmText: String?,
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
    modifier: Modifier = Modifier
) {
    val textColor = if (isDarkMode) Color.White else Color(0xFF18181B)
    val mutedColor = if (isDarkMode) FlipTextMuted else Color(0xFF71717A)
    val cardBg = if (isDarkMode) FlipCardDark else Color.White
    val cardBorder = if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Main Flip Clock Centerpiece
            FlipClockDisplay(
                hoursStr = hoursStr,
                minutesStr = minutesStr,
                secondsStr = secondsStr,
                amPmStr = amPmStr,
                dateStr = dateStr,
                is24Hour = is24Hour,
                showSeconds = showSeconds,
                isDarkMode = isDarkMode,
                nextAlarmText = nextAlarmText,
                isMuted = isMuted,
                onToggleSound = onToggleMuted,
                onEnterFullscreen = onEnterFullscreen,
                cardWidth = 68.dp,
                cardHeight = 100.dp,
                fontSize = 58.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clock Customization Controls
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, cardBorder, RoundedCornerShape(14.dp)),
                color = cardBg,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 12h/24h toggle chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (is24Hour) FlipAccentOrange.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onToggle24Hour() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("toggle_24h_chip")
                    ) {
                        Text(
                            text = if (is24Hour) "24 Hours" else "12 Hours",
                            color = if (is24Hour) FlipAccentOrange else mutedColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Show Seconds chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (showSeconds) FlipAccentOrange.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onToggleShowSeconds() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("toggle_seconds_chip")
                    ) {
                        Text(
                            text = if (showSeconds) "Seconds: ON" else "Seconds: OFF",
                            color = if (showSeconds) FlipAccentOrange else mutedColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Black & White Theme Toggle
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier.testTag("clock_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.Nightlight,
                            contentDescription = "Toggle Black and White Theme",
                            tint = if (isDarkMode) FlipAccentOrange else Color(0xFF18181B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Integrated Music & Ambience Controller for Spotify & YouTube Music
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
}
