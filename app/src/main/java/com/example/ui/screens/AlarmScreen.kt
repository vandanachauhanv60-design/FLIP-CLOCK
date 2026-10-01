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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.AlarmEntity
import com.example.ui.theme.FlipAccentGreen
import com.example.ui.theme.FlipAccentOrange
import com.example.ui.theme.FlipCardBorderDark
import com.example.ui.theme.FlipCardDark
import com.example.ui.theme.FlipTextMuted

@Composable
fun AlarmScreen(
    alarms: List<AlarmEntity>,
    onAddAlarm: (hour: Int, minute: Int, label: String, daysBitmask: Int, vibrate: Boolean, snoozeMin: Int) -> Unit,
    onToggleAlarm: (AlarmEntity, Boolean) -> Unit,
    onDeleteAlarm: (AlarmEntity) -> Unit,
    is24Hour: Boolean = false,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val textColor = if (isDarkMode) Color.White else Color(0xFF18181B)
    val mutedColor = if (isDarkMode) FlipTextMuted else Color(0xFF71717A)
    val cardBg = if (isDarkMode) FlipCardDark else Color.White
    val cardBorder = if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Header & Info banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BUILT-IN ALARMS",
                        color = mutedColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    val activeCount = alarms.count { it.isEnabled }
                    Text(
                        text = "$activeCount active",
                        color = if (activeCount > 0) FlipAccentOrange else mutedColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            if (alarms.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = mutedColor,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Alarms Set",
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + below to create a flip clock alarm",
                            color = mutedColor,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmItemRow(
                        alarm = alarm,
                        onToggle = { onToggleAlarm(alarm, it) },
                        onDelete = { onDeleteAlarm(alarm) },
                        is24Hour = is24Hour,
                        isDarkMode = isDarkMode,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to add Alarm
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_alarm_fab"),
            containerColor = FlipAccentOrange,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm")
        }
    }

    if (showAddDialog) {
        AddAlarmDialog(
            is24Hour = is24Hour,
            isDarkMode = isDarkMode,
            onDismiss = { showAddDialog = false },
            onConfirm = { hour, minute, label, bitmask, vibrate, snoozeMin ->
                onAddAlarm(hour, minute, label, bitmask, vibrate, snoozeMin)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AlarmItemRow(
    alarm: AlarmEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    is24Hour: Boolean,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkMode) FlipCardDark else Color.White
    val cardBorder = if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8)
    val textColor = if (isDarkMode) Color.White else Color(0xFF18181B)
    val mutedColor = if (isDarkMode) FlipTextMuted else Color(0xFF71717A)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .testTag("alarm_item_${alarm.id}"),
        color = cardBg,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Time Display
                Text(
                    text = alarm.getFormattedTime(is24Hour),
                    color = if (alarm.isEnabled) textColor else mutedColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Label and repeat days
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alarm.label,
                        color = if (alarm.isEnabled) FlipAccentOrange else mutedColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = " • ${alarm.getDaysSummary()}",
                        color = mutedColor,
                        fontSize = 12.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Switch
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = FlipAccentOrange
                    ),
                    modifier = Modifier.testTag("alarm_switch_${alarm.id}")
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp).testTag("delete_alarm_${alarm.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = mutedColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddAlarmDialog(
    is24Hour: Boolean,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int, label: String, daysBitmask: Int, vibrate: Boolean, snoozeMin: Int) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(7) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var isPm by remember { mutableStateOf(false) }
    var label by remember { mutableStateOf("Wake Up") }
    var daysBitmask by remember { mutableIntStateOf(0) } // 0 = once
    var vibrate by remember { mutableStateOf(true) }
    var snoozeMin by remember { mutableIntStateOf(5) }

    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set New Alarm", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                // Time inputs: Hour & Minute
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour selector
                    NumberWheelSelector(
                        value = if (is24Hour) selectedHour else if (selectedHour % 12 == 0) 12 else selectedHour % 12,
                        range = if (is24Hour) 0..23 else 1..12,
                        onValueChange = { h ->
                            selectedHour = if (is24Hour) h else if (isPm) (if (h == 12) 12 else h + 12) else (if (h == 12) 0 else h)
                        }
                    )

                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    // Minute selector
                    NumberWheelSelector(
                        value = selectedMinute,
                        range = 0..59,
                        onValueChange = { selectedMinute = it },
                        step = 5
                    )

                    if (!is24Hour) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (!isPm) FlipAccentOrange else Color(0xFF2C2C32))
                                    .clickable {
                                        isPm = false
                                        if (selectedHour >= 12) selectedHour -= 12
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("AM", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isPm) FlipAccentOrange else Color(0xFF2C2C32))
                                    .clickable {
                                        isPm = true
                                        if (selectedHour < 12) selectedHour += 12
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("PM", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Label
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Alarm Label") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("alarm_label_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Repeat Days
                Text(
                    text = "Repeat Days:",
                    fontSize = 12.sp,
                    color = FlipTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEachIndexed { index, dayLetter ->
                        val bit = 1 shl index
                        val isDaySelected = (daysBitmask and bit) != 0
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isDaySelected) FlipAccentOrange else Color(0xFF2C2C32))
                                .clickable {
                                    daysBitmask = if (isDaySelected) {
                                        daysBitmask and bit.inv()
                                    } else {
                                        daysBitmask or bit
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayLetter,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Vibration toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Vibration", fontSize = 13.sp)
                    Switch(
                        checked = vibrate,
                        onCheckedChange = { vibrate = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = FlipAccentOrange)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedHour, selectedMinute, label.trim(), daysBitmask, vibrate, snoozeMin)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlipAccentOrange),
                modifier = Modifier.testTag("confirm_alarm_button")
            ) {
                Text("Save Alarm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NumberWheelSelector(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    step: Int = 1
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                val next = if (value - step < range.first) range.last else value - step
                onValueChange(next)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF222226)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d", value),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )
        }

        IconButton(
            onClick = {
                val next = if (value + step > range.last) range.first else value + step
                onValueChange(next)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
