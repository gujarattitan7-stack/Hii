package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * 100% Offline PCM Audio Synthesizer for IMPOSTER G.
 * Generates smooth futuristic ambient studio music and crisp tactile SFX with zero external audio files.
 */
object SynthAudioEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    var musicEnabled: Boolean = true
        private set

    @Volatile
    var sfxEnabled: Boolean = true
        private set

    private var musicJob: Job? = null

    fun configure(music: Boolean, sfx: Boolean) {
        val prevMusic = musicEnabled
        musicEnabled = music
        sfxEnabled = sfx
        if (music && !prevMusic) {
            startAmbientMusic()
        } else if (!music) {
            stopAmbientMusic()
        }
    }

    fun startAmbientMusic() {
        if (!musicEnabled || musicJob?.isActive == true) return
        musicJob = scope.launch {
            val sampleRate = 22050
            val chordNotes = listOf(
                listOf(130.81, 196.00, 246.94), // Cmaj7
                listOf(110.00, 164.81, 220.00), // Am
                listOf(146.83, 220.00, 261.63), // Dm7
                listOf(98.00, 146.83, 196.00)   // Gsus4
            )
            val bufferSamples = sampleRate / 2 // 0.5s chunk
            val pcm = ShortArray(bufferSamples)
            val minBuf = runCatching {
                AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
            }.getOrDefault(bufferSamples * 2).coerceAtLeast(bufferSamples * 2)

            val track = runCatching {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            }.getOrNull() ?: return@launch

            runCatching { track.play() }.onFailure {
                runCatching { track.release() }
                return@launch
            }

            var sampleIndex = 0L
            try {
                while (isActive && musicEnabled) {
                    val chordIdx = ((sampleIndex / (sampleRate * 3)) % chordNotes.size).toInt()
                    val freqs = chordNotes[chordIdx]
                    for (i in pcm.indices) {
                        val t = (sampleIndex + i).toDouble() / sampleRate
                        val env = 0.5 + 0.5 * sin(2.0 * PI * 0.25 * t)
                        var mix = 0.0
                        for (f in freqs) {
                            mix += sin(2.0 * PI * f * t)
                        }
                        val scaled = (mix / freqs.size) * 1400.0 * (0.45 + 0.55 * env)
                        pcm[i] = scaled.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                    sampleIndex += pcm.size
                    track.write(pcm, 0, pcm.size)
                }
            } catch (_: Throwable) {
                // Safe guard on headless test runners
            } finally {
                runCatching {
                    track.stop()
                    track.release()
                }
            }
        }
    }

    fun stopAmbientMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    fun playClick() = playToneSequence(listOf(660.0 to 45, 880.0 to 35), gain = 0.22)
    fun playCardFlip() = playToneSequence(listOf(420.0 to 50, 630.0 to 60, 840.0 to 70), gain = 0.28)
    fun playWordReveal() = playToneSequence(listOf(523.25 to 70, 659.25 to 70, 783.99 to 110), gain = 0.30)
    fun playImposterSting() = playToneSequence(listOf(233.08 to 120, 220.0 to 140, 174.61 to 220), gain = 0.35)
    fun playVoteLock() = playToneSequence(listOf(440.0 to 60, 554.37 to 90), gain = 0.26)
    fun playEliminate() = playToneSequence(listOf(311.13 to 90, 233.08 to 150), gain = 0.30)
    fun playVictory() = playToneSequence(listOf(523.25 to 90, 659.25 to 90, 783.99 to 100, 1046.50 to 210), gain = 0.32)
    fun playAchievement() = playToneSequence(listOf(587.33 to 80, 739.99 to 80, 880.0 to 160), gain = 0.30)

    private fun playToneSequence(notes: List<Pair<Double, Int>>, gain: Double) {
        if (!sfxEnabled) return
        scope.launch {
            val sampleRate = 22050
            val totalSamples = notes.sumOf { (sampleRate * it.second) / 1000 }
            if (totalSamples <= 0) return@launch
            val pcm = ShortArray(totalSamples)
            var cursor = 0
            for ((freq, durationMs) in notes) {
                val count = (sampleRate * durationMs) / 1000
                for (i in 0 until count) {
                    val t = i.toDouble() / sampleRate
                    val decay = exp(-4.5 * i.toDouble() / count.coerceAtLeast(1))
                    val sample = sin(2.0 * PI * freq * t) * decay * 32767.0 * gain
                    if (cursor + i < pcm.size) {
                        pcm[cursor + i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }
                cursor += count
            }

            runCatching {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes((pcm.size * 2).coerceAtLeast(2048))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(pcm, 0, pcm.size)
                track.play()
                kotlinx.coroutines.delay(notes.sumOf { it.second }.toLong() + 80L)
                track.stop()
                track.release()
            }
        }
    }
}
