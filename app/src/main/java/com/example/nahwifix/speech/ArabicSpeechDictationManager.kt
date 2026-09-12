package com.example.nahwifix.speech

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

/**
 * Arabic Voice Dictation Manager
 * Handles continuous speech recognition configured for Arabic dialect and Modern Standard Arabic (ar-SA).
 */
class ArabicSpeechDictationManager(private val context: Context) {

    private val tag = "ArabicSpeechDictation"
    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _dictatedText = MutableStateFlow("")
    val dictatedText: StateFlow<String> = _dictatedText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w(tag, "Speech recognition is not available on this device.")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        _errorMessage.value = null
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "لم يتم التعرف على الصوت، يرجى إعادة المحاولة والتحدث بوضوح."
                            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "خطأ في الاتصال بالشبكة لخدمة التعرف على الصوت."
                            SpeechRecognizer.ERROR_AUDIO -> "خطأ في تسجيل الصوت من المايكروفون."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "يلزم منح إذن استخدام المايكروفون للإملاء الصوتي."
                            else -> "حدث خطأ أثناء الإملاء الصوتي (رمز: $error)."
                        }
                        Log.e(tag, "Speech error code: $error: $errorMsg")
                        _errorMessage.value = errorMsg
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val recognized = matches[0]
                            _dictatedText.value = recognized
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            _dictatedText.value = matches[0]
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize SpeechRecognizer", e)
        }
    }

    /**
     * Start speech recognition explicitly for Arabic (ar-SA and general Arabic fallback)
     */
    fun startListening(onResult: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _errorMessage.value = "التعرف على الصوت غير مدعوم على هذا الجهاز"
            return
        }

        if (speechRecognizer == null) {
            initRecognizer()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث باللغة العربية الآن...")
        }

        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
            _errorMessage.value = null
        } catch (e: Exception) {
            Log.e(tag, "Error starting SpeechRecognizer", e)
            _isListening.value = false
            _errorMessage.value = e.message
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e(tag, "Error stopping SpeechRecognizer", e)
        }
        _isListening.value = false
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e(tag, "Error destroying SpeechRecognizer", e)
        }
        speechRecognizer = null
        _isListening.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
