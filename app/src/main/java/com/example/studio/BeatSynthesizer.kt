package com.example.studio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class BeatStyle(val displayName: String, val defaultBpm: Int, val description: String) {
    BOOM_BAP("Boom Bap 90s", 90, "Classic raw NYC hip-hop boom bap drums"),
    TRAP_808("Trap 808", 132, "Fast rolling hats and deep punchy 808 bass"),
    DESI_DRILL("Desi Drill", 140, "Sliding UK/Desi drill bounce with syncopated snares"),
    LOFI_CHILL("Lo-Fi Chill", 80, "Mellow dusty vinyl groove for relaxed rhyme sessions")
}

class BeatSynthesizer(private val scope: CoroutineScope) {

    private val sampleRate = 44100
    private val random = Random()

    private val kickBuffer: ShortArray by lazy { generateKick() }
    private val snareBuffer: ShortArray by lazy { generateSnare() }
    private val hihatBuffer: ShortArray by lazy { generateHiHat() }
    private val clapBuffer: ShortArray by lazy { generateClap() }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentStyle = MutableStateFlow(BeatStyle.BOOM_BAP)
    val currentStyle: StateFlow<BeatStyle> = _currentStyle.asStateFlow()

    private val _bpm = MutableStateFlow(90)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _activeStep = MutableStateFlow(0)
    val activeStep: StateFlow<Int> = _activeStep.asStateFlow()

    private var loopJob: Job? = null

    fun setStyle(style: BeatStyle) {
        _currentStyle.value = style
        _bpm.value = style.defaultBpm
        if (_isPlaying.value) {
            startLoop()
        }
    }

    fun setBpm(newBpm: Int) {
        _bpm.value = newBpm.coerceIn(60, 180)
        if (_isPlaying.value) {
            startLoop()
        }
    }

    fun toggleLoop() {
        if (_isPlaying.value) {
            stopLoop()
        } else {
            startLoop()
        }
    }

    fun startLoop() {
        stopLoop()
        _isPlaying.value = true

        loopJob = scope.launch(Dispatchers.Default) {
            var step = 0
            while (isActive && _isPlaying.value) {
                _activeStep.value = step

                // 16-step patterns depending on current style
                val (playKick, playSnare, playHihat, playClap) = getPatternStep(_currentStyle.value, step)

                if (playKick) triggerKick()
                if (playSnare) triggerSnare()
                if (playHihat) triggerHiHat()
                if (playClap) triggerClap()

                val stepDurationMs = ((60000.0 / _bpm.value) / 4.0).toLong().coerceAtLeast(60L)
                delay(stepDurationMs)

                step = (step + 1) % 16
            }
        }
    }

    fun stopLoop() {
        _isPlaying.value = false
        _activeStep.value = 0
        loopJob?.cancel()
        loopJob = null
    }

    private fun getPatternStep(style: BeatStyle, step: Int): Array<Boolean> {
        // Return [Kick, Snare, HiHat, Clap]
        return when (style) {
            BeatStyle.BOOM_BAP -> {
                val k = step == 0 || step == 6 || step == 10
                val s = step == 4 || step == 12
                val h = step % 2 == 0
                val c = false
                arrayOf(k, s, h, c)
            }
            BeatStyle.TRAP_808 -> {
                val k = step == 0 || step == 7 || step == 10 || step == 14
                val s = step == 8
                val h = true // rolling hats
                val c = step == 4 || step == 12
                arrayOf(k, s, h, c)
            }
            BeatStyle.DESI_DRILL -> {
                val k = step == 0 || step == 3 || step == 8 || step == 11
                val s = step == 6 || step == 14
                val h = step % 2 == 0 || step == 7 || step == 15
                val c = step == 8
                arrayOf(k, s, h, c)
            }
            BeatStyle.LOFI_CHILL -> {
                val k = step == 0 || step == 9
                val s = step == 4 || step == 12
                val h = step % 4 == 0 || step % 4 == 2
                val c = false
                arrayOf(k, s, h, c)
            }
        }
    }

    // Drum hit triggers (instant playback for touch pads or sequencer)
    fun triggerKick() = playBufferAsync(kickBuffer)
    fun triggerSnare() = playBufferAsync(snareBuffer)
    fun triggerHiHat() = playBufferAsync(hihatBuffer)
    fun triggerClap() = playBufferAsync(clapBuffer)

    private fun playBufferAsync(buffer: ShortArray) {
        scope.launch(Dispatchers.Default) {
            try {
                val audioTrack = AudioTrack.Builder()
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Release after playback finishes
                val durationMs = (buffer.size.toDouble() / sampleRate * 1000).toLong() + 100
                delay(durationMs)
                audioTrack.release()
            } catch (e: Exception) {
                Log.e("BeatSynthesizer", "Error playing sample", e)
            }
        }
    }

    // DSP Synthesis algorithms for punchy drum sounds
    private fun generateKick(): ShortArray {
        val numSamples = (sampleRate * 0.35).toInt()
        val data = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            // Pitch sweeps from 160Hz down to 45Hz
            val freq = 45.0 + 115.0 * exp(-progress * 14.0)
            phase += 2.0 * PI * freq / sampleRate
            // Amplitude envelope
            val amp = exp(-progress * 7.0)
            val sample = (sin(phase) * amp * 32000).toInt().coerceIn(-32767, 32767)
            data[i] = sample.toShort()
        }
        return data
    }

    private fun generateSnare(): ShortArray {
        val numSamples = (sampleRate * 0.22).toInt()
        val data = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            // Tonal body 180Hz
            val freq = 180.0 * exp(-progress * 8.0)
            phase += 2.0 * PI * freq / sampleRate
            val tone = sin(phase) * exp(-progress * 10.0)

            // Snappy noise
            val noise = (random.nextDouble() * 2.0 - 1.0) * exp(-progress * 6.5)

            val mixed = (tone * 0.4 + noise * 0.6) * 30000
            data[i] = mixed.toInt().coerceIn(-32767, 32767).toShort()
        }
        return data
    }

    private fun generateHiHat(): ShortArray {
        val numSamples = (sampleRate * 0.07).toInt()
        val data = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            // Crisp metallic noise
            val noise = (random.nextDouble() * 2.0 - 1.0) * exp(-progress * 25.0)
            val mixed = noise * 24000
            data[i] = mixed.toInt().coerceIn(-32767, 32767).toShort()
        }
        return data
    }

    private fun generateClap(): ShortArray {
        val numSamples = (sampleRate * 0.20).toInt()
        val data = ShortArray(numSamples)

        // Multiple transient bursts
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val noise = (random.nextDouble() * 2.0 - 1.0)
            val burstEnv = when {
                progress < 0.04 -> progress / 0.04
                progress < 0.08 -> exp(-(progress - 0.04) * 30.0)
                progress < 0.12 -> exp(-(progress - 0.08) * 25.0)
                else -> exp(-(progress - 0.12) * 12.0)
            }
            val mixed = noise * burstEnv * 28000
            data[i] = mixed.toInt().coerceIn(-32767, 32767).toShort()
        }
        return data
    }
}
