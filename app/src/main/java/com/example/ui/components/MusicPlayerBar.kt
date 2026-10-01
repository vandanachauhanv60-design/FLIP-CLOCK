package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.MusicPlayerState
import com.example.music.MusicService
import com.example.ui.theme.FlipCardBorderDark
import com.example.ui.theme.FlipCardDark
import com.example.ui.theme.FlipTextMuted
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.YouTubeMusicRed
import com.example.util.SoundManager

@Composable
fun MusicPlayerCard(
    playerState: MusicPlayerState,
    onSelectService: (MusicService) -> Unit,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onOpenApp: () -> Unit,
    onSelectPlaylist: (String) -> Unit,
    activeAmbient: SoundManager.AmbientType,
    onSelectAmbient: (SoundManager.AmbientType) -> Unit,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkMode) FlipCardDark else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8)
    val textColor = if (isDarkMode) Color.White else Color(0xFF18181B)
    val mutedColor = if (isDarkMode) FlipTextMuted else Color(0xFF71717A)

    val serviceAccent = if (playerState.selectedService == MusicService.SPOTIFY) {
        SpotifyGreen
    } else {
        YouTubeMusicRed
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .testTag("music_player_card"),
        color = cardBg,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Service selector & Open App button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Service Toggle Buttons
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) Color(0xFF141416) else Color(0xFFECECEF))
                        .padding(3.dp)
                ) {
                    MusicServiceTab(
                        title = "Spotify",
                        isSelected = playerState.selectedService == MusicService.SPOTIFY,
                        activeColor = SpotifyGreen,
                        onClick = { onSelectService(MusicService.SPOTIFY) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    MusicServiceTab(
                        title = "YT Music",
                        isSelected = playerState.selectedService == MusicService.YOUTUBE_MUSIC,
                        activeColor = YouTubeMusicRed,
                        onClick = { onSelectService(MusicService.YOUTUBE_MUSIC) }
                    )
                }

                // Open App Quick Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(serviceAccent.copy(alpha = 0.15f))
                        .clickable { onOpenApp() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("open_music_app_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Open ${playerState.selectedService.displayName}",
                        color = serviceAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = "Open App",
                        tint = serviceAccent,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Controller Bar with Waveform, Track, Prev, Play/Pause, Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) Color(0xFF161619) else Color(0xFFF4F4F7))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Track Info & Equalizer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    WaveformIndicator(
                        isPlaying = playerState.isPlaying,
                        color = serviceAccent
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${playerState.selectedService.displayName} Controller",
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (playerState.isPlaying) "Playing • Tap next/stop" else "Ready • Tap Play to resume",
                            color = mutedColor,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                // Controls: Previous, Play/Pause, Stop, Next
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.size(36.dp).testTag("music_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Song",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Main Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(serviceAccent)
                            .clickable { onPlayPause() }
                            .testTag("music_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(36.dp).testTag("music_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Song",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onStop,
                        modifier = Modifier.size(32.dp).testTag("music_stop_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop Playback",
                            tint = mutedColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Focus Playlists / Search shortcuts
            Text(
                text = "FOCUS PLAYLIST SHORTCUTS",
                color = mutedColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val focusPlaylists = listOf(
                    "Lo-Fi Beats" to "lofi focus chill beats",
                    "Deep Work" to "deep focus instrumental",
                    "Classical Study" to "classical focus study",
                    "Synthwave Chill" to "synthwave chill work",
                    "Piano Chill" to "peaceful piano"
                )

                items(focusPlaylists) { (name, query) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkMode) Color(0xFF222226) else Color(0xFFEAEAEE))
                            .clickable { onSelectPlaylist(query) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = serviceAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = name,
                                color = textColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Built-in Ambient generator chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BUILT-IN FOCUS SOUNDS",
                    color = mutedColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                if (activeAmbient != SoundManager.AmbientType.NONE) {
                    Text(
                        text = "● Playing ${activeAmbient.title}",
                        color = SpotifyGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val ambients = listOf(
                    SoundManager.AmbientType.NONE,
                    SoundManager.AmbientType.MECHANICAL_TICK,
                    SoundManager.AmbientType.WHITE_NOISE,
                    SoundManager.AmbientType.RAIN,
                    SoundManager.AmbientType.BROWN_NOISE
                )

                items(ambients) { type ->
                    val isCurrent = activeAmbient == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isCurrent) serviceAccent.copy(alpha = 0.2f)
                                else if (isDarkMode) Color(0xFF222226)
                                else Color(0xFFEAEAEE)
                            )
                            .border(
                                width = if (isCurrent) 1.dp else 0.dp,
                                color = if (isCurrent) serviceAccent else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelectAmbient(type) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = type.title,
                            color = if (isCurrent) serviceAccent else textColor,
                            fontSize = 12.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MusicServiceTab(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color(0xFF8E8E93),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WaveformIndicator(
    isPlaying: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "waveform")

    val h1 by transition.animateFloat(
        initialValue = 4f,
        targetValue = if (isPlaying) 16f else 4f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 16f,
        targetValue = if (isPlaying) 6f else 8f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 8f,
        targetValue = if (isPlaying) 20f else 6f,
        animationSpec = infiniteRepeatable(tween(310), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        modifier = modifier.size(width = 24.dp, height = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(3.dp).height(h1.dp).background(color, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.width(3.dp).height(h2.dp).background(color, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.width(3.dp).height(h3.dp).background(color, RoundedCornerShape(2.dp)))
    }
}
