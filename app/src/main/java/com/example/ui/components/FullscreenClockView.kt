package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlipBackgroundDark
import com.example.ui.theme.FlipBackgroundLight
import com.example.ui.theme.FlipTextMuted

@Composable
fun FullscreenClockView(
    hoursStr: String,
    minutesStr: String,
    secondsStr: String,
    amPmStr: String,
    dateStr: String,
    is24Hour: Boolean,
    showSeconds: Boolean,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onExitFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isDarkMode) FlipBackgroundDark else FlipBackgroundLight
    val textColor = if (isDarkMode) Color(0xFF8E8E93) else Color(0xFF636366)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("fullscreen_clock_view")
    ) {
        // Top exit & theme controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BEDSIDE FLIP CLOCK",
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Row {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("fullscreen_theme_toggle")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.Nightlight,
                        contentDescription = "Toggle Theme",
                        tint = textColor
                    )
                }

                IconButton(
                    onClick = onExitFullscreen,
                    modifier = Modifier.testTag("exit_fullscreen_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Fullscreen",
                        tint = textColor
                    )
                }
            }
        }

        // Centered Giant Flip Display
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FlipClockDisplay(
                hoursStr = hoursStr,
                minutesStr = minutesStr,
                secondsStr = secondsStr,
                amPmStr = amPmStr,
                dateStr = dateStr,
                is24Hour = is24Hour,
                showSeconds = showSeconds,
                isDarkMode = isDarkMode,
                cardWidth = 80.dp,
                cardHeight = 120.dp,
                fontSize = 68.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Tap ✕ to return to app",
                color = textColor.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
