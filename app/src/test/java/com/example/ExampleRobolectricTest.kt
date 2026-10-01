package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AlarmEntity
import com.example.data.model.TodoEntity
import com.example.focus.DEFAULT_FOCUS_PRESETS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FLIP_CLOCK", appName)
  }

  @Test
  fun `alarm time formatting 12 hour`() {
    val alarmAm = AlarmEntity(hour = 7, minute = 5, label = "Morning")
    assertEquals("07:05 AM", alarmAm.getFormattedTime(false))

    val alarmPm = AlarmEntity(hour = 19, minute = 30, label = "Evening")
    assertEquals("07:30 PM", alarmPm.getFormattedTime(false))

    val alarmMidnight = AlarmEntity(hour = 0, minute = 0, label = "Midnight")
    assertEquals("12:00 AM", alarmMidnight.getFormattedTime(false))
  }

  @Test
  fun `alarm time formatting 24 hour`() {
    val alarm = AlarmEntity(hour = 14, minute = 45, label = "Focus")
    assertEquals("14:45", alarm.getFormattedTime(true))
  }

  @Test
  fun `focus presets validation`() {
    val classicPomodoro = DEFAULT_FOCUS_PRESETS.first()
    assertEquals(25, classicPomodoro.workMinutes)
    assertEquals(5, classicPomodoro.breakMinutes)
    assertEquals(4, classicPomodoro.cycles)
  }

  @Test
  fun `todo completion toggle`() {
    val todo = TodoEntity(title = "Study Kotlin", isCompleted = false)
    val completedTodo = todo.copy(isCompleted = true)
    assertTrue(completedTodo.isCompleted)
  }
}
