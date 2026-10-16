package com.example.assistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechRecognitionManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    private val _statusText = MutableStateFlow("Tap mic to speak")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    var onResultCallback: ((String) -> Unit)? = null
    var onErrorCallback: ((String) -> Unit)? = null

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(languageCode: String = "hi-IN") {
        stopListening()

        if (!isAvailable()) {
            _statusText.value = "Speech recognition service unavailable"
            onErrorCallback?.invoke("Voice recognition not available on this device")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        _statusText.value = "🎤 Sun raha hoon..."
                    }

                    override fun onBeginningOfSpeech() {
                        _statusText.value = "🔍 Samajh raha hoon..."
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize roughly 0..10 dB to 0..1 for visualizer
                        val normalized = (rmsdB.coerceIn(0f, 10f) / 10f)
                        _audioLevel.value = normalized
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _audioLevel.value = 0f
                        _statusText.value = "Processing..."
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _audioLevel.value = 0f
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Mic permission needed"
                            SpeechRecognizer.ERROR_NETWORK -> "Network connection error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timed out"
                            SpeechRecognizer.ERROR_NO_MATCH -> "Aawaaz saaf nahi aayi, please dobara bolo..."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Service is busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timeout, koi aawaaz nahi suni"
                            else -> "Voice recognition error"
                        }
                        _statusText.value = errorMessage
                        onErrorCallback?.invoke(errorMessage)
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _audioLevel.value = 0f
                        _statusText.value = "Ready"

                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val spokenText = matches[0]
                            onResultCallback?.invoke(spokenText)
                        } else {
                            onErrorCallback?.invoke("Aawaaz saaf nahi aayi, please dobara bolo...")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            _statusText.value = matches[0]
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("SpeechRecognition", "Error starting speech recognition", e)
            _isListening.value = false
            _audioLevel.value = 0f
            onErrorCallback?.invoke("Voice recognition error: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SpeechRecognition", "Error stopping speech recognizer", e)
        } finally {
            speechRecognizer = null
            _isListening.value = false
            _audioLevel.value = 0f
        }
    }
}
