package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TodoEntity
import com.example.ui.theme.FlipAccentGreen
import com.example.ui.theme.FlipAccentOrange
import com.example.ui.theme.FlipCardBorderDark
import com.example.ui.theme.FlipCardDark
import com.example.ui.theme.FlipTextMuted

@Composable
fun TodoScreen(
    todos: List<TodoEntity>,
    onAddTodo: (title: String, estPomodoros: Int, category: String) -> Unit,
    onToggleTodo: (TodoEntity) -> Unit,
    onDeleteTodo: (TodoEntity) -> Unit,
    onFocusTask: (TodoEntity) -> Unit,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    var newTitle by remember { mutableStateOf("") }
    var estPomodoros by remember { mutableIntStateOf(2) }
    var selectedCategory by remember { mutableStateOf("Deep Work") }
    var filterMode by remember { mutableStateOf("All") } // All, Active, Done

    val textColor = if (isDarkMode) Color.White else Color(0xFF18181B)
    val mutedColor = if (isDarkMode) FlipTextMuted else Color(0xFF71717A)
    val cardBg = if (isDarkMode) FlipCardDark else Color.White
    val cardBorder = if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8)

    val completedCount = todos.count { it.isCompleted }
    val totalPomodorosCompleted = todos.sumOf { it.pomodorosCompleted }

    val filteredTodos = when (filterMode) {
        "Active" -> todos.filter { !it.isCompleted }
        "Done" -> todos.filter { it.isCompleted }
        else -> todos
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Summary Stats Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
                color = cardBg,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${todos.size}",
                            color = textColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "TOTAL TASKS",
                            color = mutedColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(cardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$completedCount",
                            color = FlipAccentGreen,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "COMPLETED",
                            color = mutedColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(cardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalPomodorosCompleted",
                            color = FlipAccentOrange,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "POMODOROS",
                            color = mutedColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Task Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
                color = cardBg,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ADD NEW TASK",
                        color = mutedColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            placeholder = { Text("What do you want to accomplish?") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FlipAccentOrange,
                                unfocusedBorderColor = cardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("todo_input_field")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    onAddTodo(newTitle.trim(), estPomodoros, selectedCategory)
                                    newTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FlipAccentOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(52.dp).testTag("add_todo_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Estimated Pomodoros selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = FlipAccentOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Est. Pomodoros: $estPomodoros (${estPomodoros * 25} min)",
                                color = textColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1, 2, 3, 4, 5).forEach { count ->
                                val isSelected = estPomodoros == count
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) FlipAccentOrange
                                            else if (isDarkMode) Color(0xFF242428)
                                            else Color(0xFFE8E8EE)
                                        )
                                        .clickable { estPomodoros = count },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$count",
                                        color = if (isSelected) Color.White else textColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Selector
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val categories = listOf("Deep Work", "Study", "Coding", "Writing", "General")
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) FlipAccentOrange.copy(alpha = 0.2f)
                                        else if (isDarkMode) Color(0xFF242428)
                                        else Color(0xFFE8E8EE)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) FlipAccentOrange else mutedColor,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TASKS (${filteredTodos.size})",
                    color = mutedColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontFamily = FontFamily.Monospace
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Active", "Done").forEach { filter ->
                        val isSelected = filterMode == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) if (isDarkMode) Color.White else Color(0xFF18181B)
                                    else Color.Transparent
                                )
                                .clickable { filterMode = filter }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) {
                                    if (isDarkMode) Color.Black else Color.White
                                } else mutedColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Task Items
        if (filteredTodos.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tasks found",
                        color = mutedColor,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredTodos, key = { it.id }) { todo ->
                TodoItemRow(
                    todo = todo,
                    onToggle = { onToggleTodo(todo) },
                    onDelete = { onDeleteTodo(todo) },
                    onFocus = { onFocusTask(todo) },
                    isDarkMode = isDarkMode,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun TodoItemRow(
    todo: TodoEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onFocus: () -> Unit,
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
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .testTag("todo_item_${todo.id}"),
        color = cardBg
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.5.dp,
                        color = if (todo.isCompleted) FlipAccentGreen else mutedColor,
                        shape = CircleShape
                    )
                    .background(if (todo.isCompleted) FlipAccentGreen else Color.Transparent)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    color = if (todo.isCompleted) mutedColor else textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isDarkMode) Color(0xFF242429) else Color(0xFFE8E8EE))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = todo.category,
                            color = mutedColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Pomodoro count badge
                    Text(
                        text = "🍅 ${todo.pomodorosCompleted}/${todo.pomodorosEstimated}",
                        color = if (todo.pomodorosCompleted >= todo.pomodorosEstimated) FlipAccentGreen else FlipAccentOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Focus button (if not completed)
            if (!todo.isCompleted) {
                IconButton(
                    onClick = onFocus,
                    modifier = Modifier.testTag("focus_task_button_${todo.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = "Focus on this task",
                        tint = FlipAccentOrange
                    )
                }
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp).testTag("delete_task_button_${todo.id}")
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
