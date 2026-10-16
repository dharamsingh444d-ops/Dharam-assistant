package com.example.assistant

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val appContext = context.applicationContext

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    var speechRate: Float = 0.95f
        set(value) {
            field = value
            tts?.setSpeechRate(value)
        }

    var speechPitch: Float = 1.0f
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    var preferredLanguage: String = "hi-IN"
        set(value) {
            field = value
            applyLanguage()
        }

    init {
        try {
            tts = TextToSpeech(appContext, this)
        } catch (e: Exception) {
            Log.e("TtsManager", "Error initializing TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })

            applyLanguage()
            tts?.setSpeechRate(speechRate)
            tts?.setPitch(speechPitch)
            _isReady.value = true
        } else {
            Log.e("TtsManager", "TTS Initialization failed with status $status")
        }
    }

    private fun applyLanguage() {
        val targetLocale = when (preferredLanguage) {
            "hi-IN" -> Locale("hi", "IN")
            "en-IN" -> Locale("en", "IN")
            else -> Locale.US
        }

        val result = tts?.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to English if Hindi pack is not installed on device
            Log.w("TtsManager", "Preferred locale $preferredLanguage not supported, falling back to default")
            tts?.setLanguage(Locale.ENGLISH)
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (tts == null || !_isReady.value) {
            Log.w("TtsManager", "TTS not ready yet")
            onDone?.invoke()
            return
        }

        stop()
        val utteranceId = "dharam_${System.currentTimeMillis()}"
        val params = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

        _isSpeaking.value = true
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        if (result == TextToSpeech.ERROR) {
            _isSpeaking.value = false
            onDone?.invoke()
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TtsManager", "Error stopping TTS", e)
        } finally {
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
