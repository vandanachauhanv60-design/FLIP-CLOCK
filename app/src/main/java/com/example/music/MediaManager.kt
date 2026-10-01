package com.example.music

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.SystemClock
import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MusicService(val displayName: String, val packageName: String, val scheme: String) {
    SPOTIFY("Spotify", "com.spotify.music", "spotify:home"),
    YOUTUBE_MUSIC("YT Music", "com.google.android.apps.youtube.music", "https://music.youtube.com")
}

data class MusicPlayerState(
    val selectedService: MusicService = MusicService.SPOTIFY,
    val isPlaying: Boolean = false,
    val currentTrackTitle: String = "Focus Soundscape",
    val isAppInstalled: Boolean = true
)

class MediaManager(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _playerState = MutableStateFlow(MusicPlayerState())
    val playerState: StateFlow<MusicPlayerState> = _playerState.asStateFlow()

    init {
        checkAppInstalled(MusicService.SPOTIFY)
    }

    fun setSelectedService(service: MusicService) {
        val installed = isPackageInstalled(service.packageName)
        _playerState.value = _playerState.value.copy(
            selectedService = service,
            isAppInstalled = installed
        )
    }

    fun play() {
        dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY)
        _playerState.value = _playerState.value.copy(isPlaying = true)
    }

    fun pause() {
        dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PAUSE)
        _playerState.value = _playerState.value.copy(isPlaying = false)
    }

    fun togglePlayPause() {
        dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        _playerState.value = _playerState.value.copy(isPlaying = !_playerState.value.isPlaying)
    }

    fun next() {
        dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
    }

    fun previous() {
        dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    }

    fun stop() {
        dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_STOP)
        _playerState.value = _playerState.value.copy(isPlaying = false)
    }

    private fun dispatchMediaKey(keyCode: Int) {
        val eventTime = SystemClock.uptimeMillis()

        // 1. Send system-wide media key event via AudioManager
        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0)
        val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0)
        audioManager.dispatchMediaKeyEvent(downEvent)
        audioManager.dispatchMediaKeyEvent(upEvent)

        // 2. Also send explicit broadcast intent to target service if specified
        val targetPackage = _playerState.value.selectedService.packageName
        try {
            val downIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
                setPackage(targetPackage)
                putExtra(Intent.EXTRA_KEY_EVENT, downEvent)
            }
            context.sendOrderedBroadcast(downIntent, null)

            val upIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
                setPackage(targetPackage)
                putExtra(Intent.EXTRA_KEY_EVENT, upEvent)
            }
            context.sendOrderedBroadcast(upIntent, null)
        } catch (_: Exception) {
        }
    }

    fun openMusicApp(service: MusicService = _playerState.value.selectedService) {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(service.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        } else {
            // Web fallback or Play Store fallback
            val webUrl = if (service == MusicService.SPOTIFY) {
                "https://open.spotify.com"
            } else {
                "https://music.youtube.com"
            }
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (_: Exception) {
            }
        }
    }

    fun openFocusPlaylist(query: String) {
        val service = _playerState.value.selectedService
        if (service == MusicService.SPOTIFY) {
            try {
                val spotifyIntent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:search:$query")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(spotifyIntent)
            } catch (_: Exception) {
                openMusicApp(service)
            }
        } else {
            try {
                val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://music.youtube.com/search?q=$query")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(ytIntent)
            } catch (_: Exception) {
                openMusicApp(service)
            }
        }
    }

    private fun checkAppInstalled(service: MusicService) {
        _playerState.value = _playerState.value.copy(
            isAppInstalled = isPackageInstalled(service.packageName)
        )
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            false
        }
    }
}
