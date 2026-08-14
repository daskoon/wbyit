package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundEngine(private val context: Context? = null) {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    private val audioQueue = Channel<ShortArray>(Channel.UNLIMITED)
    private var introJob: Job? = null
    private var isPlayingIntro = false

    init {
        startAudioStreamWorker()
    }

    private fun startAudioStreamWorker() {
        scope.launch {
            val sampleRate = 22050
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
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
                    .setBufferSizeInBytes(minBuf * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()

                while (isActive) {
                    val pcmData = audioQueue.receive()
                    if (isEnabled && pcmData.isNotEmpty()) {
                        track.write(pcmData, 0, pcmData.size)
                    }
                }
            } catch (_: Exception) {
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Exception) {}
            }
        }
    }

    fun stopIntroMusic() {
        isPlayingIntro = false
        introJob?.cancel()
        introJob = null
    }

    fun playDoorChime() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateTonesPcm(listOf(Tone(1318.5, 160, 0.45f), Tone(1046.5, 280, 0.40f))))
        }
    }

    fun playWalkieSquelch() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateNoisePcm(70, 0.25f))
            audioQueue.trySend(generateTonesPcm(listOf(Tone(880.0, 45, 0.35f))))
        }
    }

    fun playCashRegister() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateTonesPcm(listOf(
                Tone(1396.9, 70, 0.4f),
                Tone(1760.0, 80, 0.45f),
                Tone(2093.0, 220, 0.5f)
            )))
        }
    }

    fun playCustomerAngry() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateTonesPcm(listOf(Tone(220.0, 150, 0.5f), Tone(164.8, 250, 0.45f))))
        }
    }

    fun playCustomerHappy() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateTonesPcm(listOf(
                Tone(523.25, 70, 0.35f),
                Tone(659.25, 70, 0.35f),
                Tone(783.99, 120, 0.4f),
                Tone(1046.50, 180, 0.45f)
            )))
        }
    }

    fun playScannerBeep() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateTonesPcm(listOf(Tone(2400.0, 65, 0.4f))))
        }
    }

    fun playIntercom() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateTonesPcm(listOf(Tone(784.0, 180, 0.4f), Tone(1046.5, 240, 0.45f))))
        }
    }

    fun playSecurityAlarm() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateTonesPcm(listOf(
                Tone(1200.0, 100, 0.45f),
                Tone(800.0, 100, 0.45f),
                Tone(1200.0, 100, 0.45f),
                Tone(800.0, 120, 0.45f)
            )))
        }
    }

    fun playVictoryFanfare() {
        if (!isEnabled) return
        scope.launch {
            audioQueue.trySend(generateTonesPcm(listOf(
                Tone(523.25, 100, 0.4f),
                Tone(659.25, 100, 0.4f),
                Tone(783.99, 100, 0.4f),
                Tone(1046.50, 280, 0.5f)
            )))
        }
    }

    fun playStarWarsFanfare() {
        if (!isEnabled) return
        stopIntroMusic()
        isPlayingIntro = true
        introJob = scope.launch {
            val sampleRate = 22050
            data class Note(val freq: Double, val durMs: Int, val harmony: Double = 0.0)

            val notes = listOf(
                Note(233.08, 140, 116.54),
                Note(233.08, 140, 116.54),
                Note(233.08, 140, 116.54),
                Note(311.13, 850, 155.56),
                Note(466.16, 850, 233.08),
                Note(415.30, 160, 207.65),
                Note(392.00, 160, 196.00),
                Note(349.23, 160, 174.61),
                Note(622.25, 800, 311.13),
                Note(466.16, 450, 233.08),
                Note(415.30, 160, 207.65),
                Note(392.00, 160, 196.00),
                Note(349.23, 160, 174.61),
                Note(622.25, 800, 311.13),
                Note(466.16, 450, 233.08),
                Note(415.30, 200, 207.65),
                Note(392.00, 200, 196.00),
                Note(415.30, 200, 207.65),
                Note(349.23, 900, 174.61),
                Note(233.08, 250, 116.54),
                Note(261.63, 250, 130.81),
                Note(293.66, 250, 146.83),
                Note(311.13, 600, 155.56),
                Note(349.23, 600, 174.61),
                Note(392.00, 1200, 196.00),
                Note(466.16, 1600, 233.08)
            )

            for (note in notes) {
                if (!isPlayingIntro || !isActive) break
                val count = (note.durMs * sampleRate) / 1000
                val pcm = ShortArray(count)
                for (i in 0 until count) {
                    val t = i.toDouble() / sampleRate
                    val base = sin(2.0 * PI * note.freq * t)
                    val h2 = 0.5 * sin(2.0 * PI * (note.freq * 2) * t)
                    val h3 = 0.25 * sin(2.0 * PI * (note.freq * 3) * t)
                    val harm = if (note.harmony > 0) 0.4 * sin(2.0 * PI * note.harmony * t) else 0.0
                    val combined = (base + h2 + h3 + harm) / 2.15

                    val attack = (i.toFloat() / (sampleRate * 0.02f)).coerceIn(0f, 1f)
                    val decay = (1.0 - (i.toDouble() / count).coerceIn(0.0, 1.0)).toFloat().coerceIn(0.05f, 1f)
                    val env = attack * decay

                    val sampleVal = (combined * Short.MAX_VALUE * 0.55f * env).toInt()
                    pcm[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                audioQueue.send(pcm)
            }
        }
    }

    private data class Tone(val freqHz: Double, val durationMs: Int, val volume: Float = 0.5f)

    private fun generateTonesPcm(tones: List<Tone>): ShortArray {
        val sampleRate = 22050
        val totalSamples = tones.sumOf { (it.durationMs * sampleRate) / 1000 }
        val pcm = ShortArray(totalSamples)

        var offset = 0
        for (tone in tones) {
            val count = (tone.durationMs * sampleRate) / 1000
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                val envelope = (1.0 - (i.toDouble() / count)).toFloat().coerceIn(0f, 1f)
                val sampleVal = sin(2.0 * PI * tone.freqHz * t) * Short.MAX_VALUE * tone.volume * envelope
                pcm[offset + i] = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            offset += count
        }
        return pcm
    }

    private fun generateNoisePcm(durationMs: Int, volume: Float): ShortArray {
        val sampleRate = 22050
        val totalSamples = (durationMs * sampleRate) / 1000
        val pcm = ShortArray(totalSamples)
        val rand = java.util.Random()
        for (i in 0 until totalSamples) {
            val envelope = (1.0 - (i.toDouble() / totalSamples)).toFloat().coerceIn(0f, 1f)
            val noise = (rand.nextFloat() * 2f - 1f) * Short.MAX_VALUE * volume * envelope
            pcm[i] = noise.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }
}
