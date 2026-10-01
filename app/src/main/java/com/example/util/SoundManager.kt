package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isSoundEffectsEnabled: Boolean = true

    // Ambient loop job
    private var ambientJob: Job? = null
    private var ambientTrack: AudioTrack? = null
    var activeAmbientType: AmbientType = AmbientType.NONE

    enum class AmbientType(val title: String) {
        NONE("Off"),
        MECHANICAL_TICK("Flip Clock Ticking"),
        WHITE_NOISE("Focus White Noise"),
        RAIN("Calm Rain"),
        BROWN_NOISE("Deep Brown Noise")
    }

    /**
     * Plays a realistic split-flap mechanical flip sound (approx 35ms)
     */
    fun playFlipClick() {
        if (!isSoundEffectsEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 30
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                val random = Random(System.nanoTime())
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Double click impact impulse
                    val decay = exp(-t * 220.0)
                    val impact = (random.nextDouble() * 2.0 - 1.0) * decay
                    val tone = sin(2.0 * PI * 850.0 * t) * decay * 0.4
                    val sample = ((impact + tone) * Short.MAX_VALUE * 0.5).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                // Let track finish playing, then release
                kotlinx.coroutines.delay(100)
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Plays a clear, calming chime when a Pomodoro timer / break completes.
     */
    fun playTimerCompletionChime() {
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 1200
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                val freqs = doubleArrayOf(587.33, 880.0, 1174.66) // D5 chord
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val decay = exp(-t * 3.5)
                    var wave = 0.0
                    for (f in freqs) {
                        wave += sin(2.0 * PI * f * t)
                    }
                    wave *= (decay / freqs.size)
                    buffer[i] = (wave * Short.MAX_VALUE * 0.7).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                kotlinx.coroutines.delay(durationMs + 200L)
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Toggles continuous ambient background focus sounds
     */
    fun setAmbient(type: AmbientType) {
        if (activeAmbientType == type) {
            stopAmbient()
            return
        }
        stopAmbient()
        activeAmbientType = type
        if (type == AmbientType.NONE) return

        ambientJob = scope.launch {
            try {
                val sampleRate = 22050
                val bufferSize = sampleRate / 2
                val buffer = ShortArray(bufferSize)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 4)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                ambientTrack = track
                track.play()

                var lastBrown = 0.0
                val random = Random(42)

                while (isActive && activeAmbientType == type) {
                    for (i in 0 until bufferSize) {
                        val sample: Double = when (type) {
                            AmbientType.WHITE_NOISE -> {
                                (random.nextDouble() * 2.0 - 1.0) * 0.15
                            }
                            AmbientType.BROWN_NOISE -> {
                                val white = random.nextDouble() * 2.0 - 1.0
                                lastBrown = (lastBrown + (0.02 * white)) / 1.02
                                lastBrown * 0.3
                            }
                            AmbientType.RAIN -> {
                                val base = (random.nextDouble() * 2.0 - 1.0) * 0.12
                                val drop = if (random.nextDouble() < 0.003) 0.35 else 0.0
                                (base + drop)
                            }
                            AmbientType.MECHANICAL_TICK -> {
                                val tickIndex = i % sampleRate
                                if (tickIndex < 300) {
                                    val t = tickIndex.toDouble() / sampleRate
                                    exp(-t * 200.0) * sin(2.0 * PI * 1200.0 * t) * 0.4
                                } else 0.0
                            }
                            else -> 0.0
                        }
                        buffer[i] = (sample * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (_: Exception) {
            } finally {
                ambientTrack?.stop()
                ambientTrack?.release()
                ambientTrack = null
            }
        }
    }

    fun stopAmbient() {
        activeAmbientType = AmbientType.NONE
        ambientJob?.cancel()
        ambientJob = null
        try {
            ambientTrack?.stop()
            ambientTrack?.release()
        } catch (_: Exception) {
        }
        ambientTrack = null
    }
}
