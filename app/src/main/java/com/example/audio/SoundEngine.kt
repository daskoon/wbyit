package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundEngine {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    fun playDoorChime() {
        if (!isEnabled) return
        scope.launch {
            // Ding-Dong!
            playTones(listOf(Tone(1318.5, 160, 0.45f), Tone(1046.5, 280, 0.40f)))
        }
    }

    fun playWalkieSquelch() {
        if (!isEnabled) return
        scope.launch {
            // Radio static squelch click
            playNoiseBurst(70, 0.25f)
            playTones(listOf(Tone(880.0, 45, 0.35f)))
        }
    }

    fun playCashRegister() {
        if (!isEnabled) return
        scope.launch {
            // Cha-Ching!
            playTones(listOf(
                Tone(1396.9, 70, 0.4f),
                Tone(1760.0, 80, 0.45f),
                Tone(2093.0, 220, 0.5f)
            ))
        }
    }

    fun playCustomerAngry() {
        if (!isEnabled) return
        scope.launch {
            // Low buzz angry strike
            playTones(listOf(Tone(220.0, 150, 0.5f), Tone(164.8, 250, 0.45f)))
        }
    }

    fun playCustomerHappy() {
        if (!isEnabled) return
        scope.launch {
            // Positive chime
            playTones(listOf(
                Tone(523.25, 70, 0.35f),
                Tone(659.25, 70, 0.35f),
                Tone(783.99, 120, 0.4f),
                Tone(1046.50, 180, 0.45f)
            ))
        }
    }

    fun playScannerBeep() {
        if (!isEnabled) return
        scope.launch {
            // Barcode beep
            playTones(listOf(Tone(2400.0, 65, 0.4f)))
        }
    }

    fun playIntercom() {
        if (!isEnabled) return
        scope.launch {
            // Store PA chime
            playTones(listOf(Tone(784.0, 180, 0.4f), Tone(1046.5, 240, 0.45f)))
        }
    }

    fun playSecurityAlarm() {
        if (!isEnabled) return
        scope.launch {
            // Siren pulse
            playTones(listOf(
                Tone(1200.0, 100, 0.45f),
                Tone(800.0, 100, 0.45f),
                Tone(1200.0, 100, 0.45f),
                Tone(800.0, 120, 0.45f)
            ))
        }
    }

    fun playVictoryFanfare() {
        if (!isEnabled) return
        scope.launch {
            playTones(listOf(
                Tone(523.25, 100, 0.4f),
                Tone(659.25, 100, 0.4f),
                Tone(783.99, 100, 0.4f),
                Tone(1046.50, 280, 0.5f)
            ))
        }
    }

    private var activeIntroTrack: AudioTrack? = null

    fun stopIntroMusic() {
        try {
            activeIntroTrack?.stop()
            activeIntroTrack?.release()
            activeIntroTrack = null
        } catch (_: Exception) {}
    }

    fun playStarWarsFanfare() {
        if (!isEnabled) return
        stopIntroMusic()
        scope.launch {
            val sampleRate = 22050
            // Star Wars Main Theme synthesized in key of Bb / Eb
            // Frequencies:
            // Bb3: 233.08, Eb4: 311.13, G4: 392.00, Bb4: 466.16, Ab4: 415.30, F4: 349.23, Eb5: 622.25, D5: 587.33, C5: 523.25
            data class Note(val freq: Double, val durMs: Int, val harmony: Double = 0.0)

            val notes = listOf(
                // Opening fanfare fanfare strike
                Note(233.08, 140, 116.54), // Bb3 triplet 1
                Note(233.08, 140, 116.54), // Bb3 triplet 2
                Note(233.08, 140, 116.54), // Bb3 triplet 3
                Note(311.13, 850, 155.56), // Eb4 (LONG HIT!)
                Note(466.16, 850, 233.08), // Bb4 (LONG HIT!)
                // Melody phrase 1
                Note(415.30, 160, 207.65), // Ab4
                Note(392.00, 160, 196.00), // G4
                Note(349.23, 160, 174.61), // F4
                Note(622.25, 800, 311.13), // Eb5 (HIGH HIT!)
                Note(466.16, 450, 233.08), // Bb4
                // Melody phrase 2
                Note(415.30, 160, 207.65), // Ab4
                Note(392.00, 160, 196.00), // G4
                Note(349.23, 160, 174.61), // F4
                Note(622.25, 800, 311.13), // Eb5
                Note(466.16, 450, 233.08), // Bb4
                // Phrase 3
                Note(415.30, 200, 207.65), // Ab4
                Note(392.00, 200, 196.00), // G4
                Note(415.30, 200, 207.65), // Ab4
                Note(349.23, 900, 174.61), // F4 (resolving brass)
                // Bridge chords
                Note(233.08, 250, 116.54),
                Note(261.63, 250, 130.81),
                Note(293.66, 250, 146.83),
                Note(311.13, 600, 155.56),
                Note(349.23, 600, 174.61),
                Note(392.00, 1200, 196.00),
                Note(466.16, 1600, 233.08)
            )

            val totalSamples = notes.sumOf { (it.durMs * sampleRate) / 1000 }
            val buffer = ShortArray(totalSamples)
            var offset = 0

            for (note in notes) {
                val count = (note.durMs * sampleRate) / 1000
                for (i in 0 until count) {
                    val t = i.toDouble() / sampleRate
                    // Brass wave with 3 harmonics
                    val base = sin(2.0 * PI * note.freq * t)
                    val h2 = 0.5 * sin(2.0 * PI * (note.freq * 2) * t)
                    val h3 = 0.25 * sin(2.0 * PI * (note.freq * 3) * t)
                    val harm = if (note.harmony > 0) 0.4 * sin(2.0 * PI * note.harmony * t) else 0.0
                    val combined = (base + h2 + h3 + harm) / 2.15

                    // Envelope: fast attack, slight sustain, decay
                    val attack = (i.toFloat() / (sampleRate * 0.02f)).coerceIn(0f, 1f)
                    val decay = (1.0 - (i.toDouble() / count).coerceIn(0.0, 1.0)).toFloat().coerceIn(0.05f, 1f)
                    val env = attack * decay

                    val sampleVal = (combined * Short.MAX_VALUE * 0.55f * env).toInt()
                    buffer[offset + i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                offset += count
            }

            try {
                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
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
                    .setBufferSizeInBytes(buffer.size * 2.coerceAtLeast(minBuf))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                activeIntroTrack = track
                track.write(buffer, 0, buffer.size)
                track.play()
            } catch (_: Exception) {}
        }
    }

    private data class Tone(val freqHz: Double, val durationMs: Int, val volume: Float = 0.5f)

    private fun playTones(tones: List<Tone>) {
        val sampleRate = 22050
        val totalSamples = tones.sumOf { (it.durationMs * sampleRate) / 1000 }
        val buffer = ShortArray(totalSamples)

        var offset = 0
        for (tone in tones) {
            val count = (tone.durationMs * sampleRate) / 1000
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                // Sine wave with exponential decay envelope
                val envelope = (1.0 - (i.toDouble() / count)).toFloat().coerceIn(0f, 1f)
                val sampleVal = sin(2.0 * PI * tone.freqHz * t) * Short.MAX_VALUE * tone.volume * envelope
                buffer[offset + i] = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            offset += count
        }

        playBuffer(buffer, sampleRate)
    }

    private fun playNoiseBurst(durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val totalSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(totalSamples)
        val rand = java.util.Random()
        for (i in 0 until totalSamples) {
            val envelope = (1.0 - (i.toDouble() / totalSamples)).toFloat().coerceIn(0f, 1f)
            val noise = (rand.nextFloat() * 2f - 1f) * Short.MAX_VALUE * volume * envelope
            buffer[i] = noise.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
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
                .setBufferSizeInBytes(buffer.size * 2.coerceAtLeast(minBuf))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            track.setNotificationMarkerPosition(buffer.size)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onPeriodicNotification(p0: AudioTrack?) {}
                override fun onMarkerReached(p0: AudioTrack?) {
                    try {
                        track.stop()
                        track.release()
                    } catch (_: Exception) {}
                }
            })
        } catch (_: Exception) {
            // Audio fallback gracefully ignores
        }
    }
}
