package com.example.nahwifix.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nahwifix.data.AppDatabase
import com.example.nahwifix.data.HistoryEntity
import com.example.nahwifix.data.HistoryRepository
import com.example.nahwifix.data.repository.AllamArabicWritingRepository
import com.example.nahwifix.data.repository.GeminiArabicGrammarRepository
import com.example.nahwifix.engine.ArabicGrammarEngine
import com.example.nahwifix.model.AppLanguage
import com.example.nahwifix.model.CorrectionItem
import com.example.nahwifix.model.CorrectionResult
import com.example.nahwifix.speech.ArabicSpeechDictationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class NahwiFixViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val historyRepository: HistoryRepository
    val geminiRepository: GeminiArabicGrammarRepository = GeminiArabicGrammarRepository()
    val allamRepository: AllamArabicWritingRepository = AllamArabicWritingRepository()

    init {
        val db = AppDatabase.getDatabase(application)
        historyRepository = HistoryRepository(db.historyDao())
        initTts(application)
    }

    val historyList: StateFlow<List<HistoryEntity>> = historyRepository.allHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _inputText = MutableStateFlow("ذهب محمد الى المدرسة، و هو يحمل كتبه وشاهد عصفورا جميلا.")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _result = MutableStateFlow(ArabicGrammarEngine.analyzeText(_inputText.value))
    val result: StateFlow<CorrectionResult> = _result.asStateFlow()

    private val _selectedItem = MutableStateFlow<CorrectionItem?>(_result.value.items.firstOrNull())
    val selectedItem: StateFlow<CorrectionItem?> = _selectedItem.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ARABIC)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _thinkBeforeReply = MutableStateFlow(true)
    val thinkBeforeReply: StateFlow<Boolean> = _thinkBeforeReply.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Write with AI state
    private val _isGeneratingWithAi = MutableStateFlow(false)
    val isGeneratingWithAi: StateFlow<Boolean> = _isGeneratingWithAi.asStateFlow()

    private val _aiWritingPrompt = MutableStateFlow("")
    val aiWritingPrompt: StateFlow<String> = _aiWritingPrompt.asStateFlow()

    private val _aiTone = MutableStateFlow("formal") // formal, creative, concise, academic, persuasive
    val aiTone: StateFlow<String> = _aiTone.asStateFlow()

    private val _aiGeneratedText = MutableStateFlow<String?>(null)
    val aiGeneratedText: StateFlow<String?> = _aiGeneratedText.asStateFlow()

    private val _isWriteAiExpanded = MutableStateFlow(false)
    val isWriteAiExpanded: StateFlow<Boolean> = _isWriteAiExpanded.asStateFlow()

    // Reader Mode for people who can't read / Accessibility
    private var textToSpeech: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isReaderModeActive = MutableStateFlow(false)
    val isReaderModeActive: StateFlow<Boolean> = _isReaderModeActive.asStateFlow()

    private val _readerFontSize = MutableStateFlow(22) // default 22sp for large readable text
    val readerFontSize: StateFlow<Int> = _readerFontSize.asStateFlow()

    private val _isHighContrast = MutableStateFlow(false)
    val isHighContrast: StateFlow<Boolean> = _isHighContrast.asStateFlow()

    // Microphone / Speech Dictation
    private val dictationManager = ArabicSpeechDictationManager(application)
    val isListeningToVoice: StateFlow<Boolean> = dictationManager.isListening

    init {
        // Collect recognized speech and append/replace into input text
        viewModelScope.launch {
            dictationManager.dictatedText.collect { spokenText ->
                if (spokenText.isNotBlank()) {
                    val current = _inputText.value.trim()
                    val combined = if (current.isEmpty()) {
                        spokenText
                    } else {
                        "$current $spokenText"
                    }
                    _inputText.value = combined
                    analyzeText()
                    _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                        "تمت إضافة الإملاء الصوتي وتدقيقه لغوياً"
                    else
                        "Voice dictation added & analyzed"
                }
            }
        }

        viewModelScope.launch {
            dictationManager.errorMessage.collect { err ->
                if (err != null) {
                    _statusMessage.value = err
                }
            }
        }
    }

    private fun initTts(context: Application) {
        try {
            textToSpeech = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e("NahwiFixViewModel", "TTS Init error", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val tts = textToSpeech ?: return
            // Try Arabic locale first
            val arLocale = Locale("ar")
            val result = tts.setLanguage(arLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default or English
                tts.setLanguage(Locale.getDefault())
            }
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
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
            })
            _isTtsReady.value = true
        } else {
            _isTtsReady.value = false
        }
    }

    fun toggleReaderMode() {
        _isReaderModeActive.value = !_isReaderModeActive.value
        if (_isReaderModeActive.value) {
            _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم تفعيل وضع القارئ الصوتي الميسر"
            else
                "Reader Mode activated"
        } else {
            stopSpeaking()
        }
    }

    fun setReaderModeActive(active: Boolean) {
        _isReaderModeActive.value = active
        if (!active) stopSpeaking()
    }

    fun setReaderFontSize(size: Int) {
        _readerFontSize.value = size.coerceIn(16, 36)
    }

    fun increaseReaderFontSize() {
        setReaderFontSize(_readerFontSize.value + 2)
    }

    fun decreaseReaderFontSize() {
        setReaderFontSize(_readerFontSize.value - 2)
    }

    fun toggleHighContrast() {
        _isHighContrast.value = !_isHighContrast.value
    }

    fun speakText(textToRead: String) {
        val cleanText = textToRead.trim()
        if (cleanText.isEmpty()) return

        val tts = textToSpeech
        if (tts != null && _isTtsReady.value) {
            _isSpeaking.value = true
            tts.stop()
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "NAHWI_READER_${System.currentTimeMillis()}")
            tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "NAHWI_READER")
            _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                "جارٍ الاستماع للنص..."
            else
                "Reading text aloud..."
        } else {
            _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                "خدمة النطق الصوتي غير مفعلة على جهازك"
            else
                "Text-to-speech service not available"
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    // Voice Dictation Controls
    fun startDictation() {
        // If TTS is speaking, stop it so it doesn't feed into microphone
        stopSpeaking()
        dictationManager.startListening { spoken ->
            if (spoken.isNotBlank()) {
                val cur = _inputText.value.trim()
                _inputText.value = if (cur.isEmpty()) spoken else "$cur $spoken"
                analyzeText()
            }
        }
        _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
            "تحدث الآن باللغة العربية وسيتم تحويل صوتك لنص..."
        else
            "Speak in Arabic now..."
    }

    fun stopDictation() {
        dictationManager.stopListening()
        _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
            "تم إيقاف الإملاء الصوتي"
        else
            "Voice dictation stopped"
    }

    fun toggleDictation() {
        if (isListeningToVoice.value) {
            stopDictation()
        } else {
            startDictation()
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        dictationManager.destroy()
    }

    fun updateInputText(newText: String) {
        _inputText.value = newText
        analyzeText()
    }

    fun analyzeText() {
        val currentText = _inputText.value.trim()
        if (currentText.isEmpty()) {
            _result.value = CorrectionResult()
            _selectedItem.value = null
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val analysisResult = geminiRepository.analyzeArabicText(
                    text = currentText,
                    deepReasoning = _thinkBeforeReply.value
                )

                val analysis = analysisResult.getOrDefault(ArabicGrammarEngine.analyzeText(currentText))
                _result.value = analysis

                if (_selectedItem.value == null || !analysis.items.any { it.id == _selectedItem.value?.id }) {
                    _selectedItem.value = analysis.items.firstOrNull()
                }

                if (analysis.items.isNotEmpty()) {
                    historyRepository.saveCheck(
                        original = analysis.originalText,
                        corrected = analysis.correctedText,
                        issueCount = analysis.items.size
                    )
                }
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun selectItem(item: CorrectionItem) {
        _selectedItem.value = item
    }

    fun applySingleCorrection(item: CorrectionItem) {
        val currentText = _inputText.value
        val updated = if (item.startIndex in 0..currentText.length &&
            item.endIndex in item.startIndex..currentText.length &&
            currentText.substring(item.startIndex, item.endIndex) == item.originalSnippet
        ) {
            currentText.substring(0, item.startIndex) +
                    item.suggestedReplacement +
                    currentText.substring(item.endIndex)
        } else {
            // Fallback string replacement
            currentText.replaceFirst(item.originalSnippet, item.suggestedReplacement)
        }
        _inputText.value = updated
        val newResult = ArabicGrammarEngine.analyzeText(updated)
        _result.value = newResult
        _selectedItem.value = newResult.items.firstOrNull()
        _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
            "تم تصحيح '${item.originalSnippet}' إلى '${item.suggestedReplacement}'"
        else
            "Fixed '${item.originalSnippet}' to '${item.suggestedReplacement}'"
    }

    fun fixAllErrors() {
        val currentText = _inputText.value.trim()
        if (currentText.isEmpty()) return

        val analysis = ArabicGrammarEngine.analyzeText(currentText)
        if (analysis.correctedText.isNotBlank()) {
            val errorCount = analysis.items.size
            _inputText.value = analysis.correctedText
            val cleanResult = ArabicGrammarEngine.analyzeText(analysis.correctedText)
            _result.value = cleanResult
            _selectedItem.value = null
            _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                if (errorCount > 0) "تم تصحيح كافة الأخطاء ($errorCount خطأ) بنجاح!" else "النص سليم وخالٍ من الأخطاء"
            else
                if (errorCount > 0) "Fixed all $errorCount errors successfully!" else "Text is clean and error-free"
        }
    }

    fun applyAllCorrections() {
        fixAllErrors()
    }

    fun loadSample(index: Int) {
        val samples = listOf(
            "ذهب محمد الى المدرسة، و هو يحمل كتبه وشاهد عصفورا جميلا.",
            "إن المعلمة يساعد الطلاب في فهم الدرس.",
            "كان الولد يلعب في الحديقةُ وشاهد عصفوراً جميلاً."
        )
        if (index in samples.indices) {
            updateInputText(samples[index])
        }
    }

    fun clearInput() {
        _inputText.value = ""
        _result.value = CorrectionResult()
        _selectedItem.value = null
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
    }

    fun toggleThinkBeforeReply() {
        _thinkBeforeReply.value = !_thinkBeforeReply.value
    }

    fun setWriteAiExpanded(expanded: Boolean) {
        _isWriteAiExpanded.value = expanded
    }

    fun toggleWriteAiExpanded() {
        _isWriteAiExpanded.value = !_isWriteAiExpanded.value
    }

    fun updateAiWritingPrompt(prompt: String) {
        _aiWritingPrompt.value = prompt
    }

    fun setAiTone(tone: String) {
        _aiTone.value = tone
    }

    fun generateWithAi(prompt: String = _aiWritingPrompt.value, tone: String = _aiTone.value) {
        val currentPrompt = prompt.trim()
        val currentContent = _inputText.value.trim()
        if (currentPrompt.isEmpty() && currentContent.isEmpty()) return

        _isGeneratingWithAi.value = true
        viewModelScope.launch {
            val result = allamRepository.writeWithAllam(
                prompt = currentPrompt,
                currentText = currentContent,
                tone = tone
            )
            _isGeneratingWithAi.value = false
            result.onSuccess { text ->
                _aiGeneratedText.value = text
                _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                    "تمت الكتابة بالذكاء الاصطناعي بنجاح!"
                else
                    "Text generated with AI successfully!"
            }.onFailure { err ->
                _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                    "تعذر توليد النص: ${err.message}"
                else
                    "Failed to generate: ${err.message}"
            }
        }
    }

    fun applyAiGeneratedTextToInput() {
        val generated = _aiGeneratedText.value ?: return
        updateInputText(generated)
        _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
            "تم تطبيق النص المولد في مساحة التدقيق"
        else
            "Applied generated text to checker"
    }

    fun appendAiGeneratedTextToInput() {
        val generated = _aiGeneratedText.value ?: return
        val current = _inputText.value.trim()
        val combined = if (current.isEmpty()) generated else "$current\n\n$generated"
        updateInputText(combined)
        _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
            "تم إلحاق النص المولد بالنص الحالي"
        else
            "Appended generated text"
    }

    fun clearAiGeneratedText() {
        _aiGeneratedText.value = null
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun deleteHistoryItem(item: HistoryEntity) {
        viewModelScope.launch {
            historyRepository.deleteCheck(item)
            _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم حذف السجل المحدد"
            else
                "Deleted history record"
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
            _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم مسح كافة سجلات التحليل"
            else
                "Cleared all analysis history"
        }
    }

    fun restoreHistoryToInput(item: HistoryEntity) {
        updateInputText(item.originalText)
        _statusMessage.value = if (_language.value == AppLanguage.ARABIC)
            "تمت استعادة النص من السجل"
        else
            "Restored text from history"
    }
}
