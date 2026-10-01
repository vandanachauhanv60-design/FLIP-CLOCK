package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlipAccentOrange
import com.example.ui.theme.FlipCardBorderDark
import com.example.ui.theme.FlipCardDark
import com.example.ui.theme.FlipTextMuted

@Composable
fun FlipClockDisplay(
    hoursStr: String,
    minutesStr: String,
    secondsStr: String,
    amPmStr: String,
    dateStr: String,
    is24Hour: Boolean,
    showSeconds: Boolean,
    isDarkMode: Boolean = true,
    nextAlarmText: String? = null,
    isMuted: Boolean = false,
    onToggleSound: () -> Unit = {},
    onEnterFullscreen: () -> Unit = {},
    cardWidth: Dp = 68.dp,
    cardHeight: Dp = 100.dp,
    fontSize: TextUnit = 58.sp,
    modifier: Modifier = Modifier
) {
    val bezelBg = if (isDarkMode) Color(0xFF141417) else Color(0xFFEBEBF0)
    val bezelBorder = if (isDarkMode) FlipCardBorderDark else Color(0xFFCECED4)
    val dateColor = if (isDarkMode) FlipTextMuted else Color(0xFF636366)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, bezelBorder, RoundedCornerShape(18.dp))
            .testTag("flip_clock_display"),
        color = bezelBg,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Date + Retro status badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr.uppercase(),
                    color = dateColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier.size(32.dp).testTag("sound_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute tick sound" else "Mute tick sound",
                            tint = dateColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onEnterFullscreen,
                        modifier = Modifier.size(32.dp).testTag("fullscreen_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Full Screen Desk Clock",
                            tint = dateColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Flip Clock digits
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Hours
                FlipDigitPair(
                    value = hoursStr,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    fontSize = fontSize,
                    isDarkMode = isDarkMode
                )

                // Colon
                FlipColon(
                    isDarkMode = isDarkMode,
                    dotSize = (cardHeight.value * 0.08f).dp
                )

                // Minutes
                FlipDigitPair(
                    value = minutesStr,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    fontSize = fontSize,
                    isDarkMode = isDarkMode
                )

                // Optional Seconds or AM/PM
                if (showSeconds) {
                    FlipColon(
                        isDarkMode = isDarkMode,
                        dotSize = (cardHeight.value * 0.06f).dp
                    )
                    FlipDigitPair(
                        value = secondsStr,
                        cardWidth = (cardWidth.value * 0.72f).dp,
                        cardHeight = (cardHeight.value * 0.72f).dp,
                        fontSize = (fontSize.value * 0.72f).sp,
                        isDarkMode = isDarkMode
                    )
                }

                if (!is24Hour) {
                    Spacer(modifier = Modifier.width(8.dp))
                    FlipCard(
                        value = amPmStr,
                        cardWidth = (cardWidth.value * 0.62f).dp,
                        cardHeight = (cardHeight.value * 0.62f).dp,
                        fontSize = (fontSize.value * 0.38f).sp,
                        isDarkMode = isDarkMode
                    )
                }
            }

            // Next Alarm Indicator if present
            if (!nextAlarmText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDarkMode) Color(0xFF222227) else Color(0xFFDFDFE5))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Next Alarm",
                        tint = FlipAccentOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = nextAlarmText,
                        color = if (isDarkMode) Color(0xFFDCDCE2) else Color(0xFF2C2C30),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
