package com.example.nahwifix.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nahwifix.model.AppLanguage
import com.example.nahwifix.model.CorrectionCategory
import com.example.nahwifix.model.CorrectionItem
import com.example.nahwifix.model.CorrectionResult
import com.example.nahwifix.ui.NahwiFixViewModel
import com.example.nahwifix.ui.components.NahwiFixLogo
import com.example.nahwifix.ui.theme.BadgeAgreement
import com.example.nahwifix.ui.theme.BadgeGrammar
import com.example.nahwifix.ui.theme.BadgePunctuation
import com.example.nahwifix.ui.theme.BadgeSpelling

/**
 * Screen providing an Arabic text input area with full RTL support,
 * word/character counting, and a prominent 'Correct' button.
 */
@Composable
fun ArabicCorrectionScreen(
    viewModel: NahwiFixViewModel,
    modifier: Modifier = Modifier
) {
    val inputText by viewModel.inputText.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val result by viewModel.result.collectAsState()
    val language by viewModel.language.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val isGeneratingWithAi by viewModel.isGeneratingWithAi.collectAsState()
    val aiWritingPrompt by viewModel.aiWritingPrompt.collectAsState()
    val aiTone by viewModel.aiTone.collectAsState()
    val aiGeneratedText by viewModel.aiGeneratedText.collectAsState()
    val isWriteAiExpanded by viewModel.isWriteAiExpanded.collectAsState()

    // Reader Mode & Accessibility State
    val isReaderModeActive by viewModel.isReaderModeActive.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val readerFontSize by viewModel.readerFontSize.collectAsState()
    val isHighContrast by viewModel.isHighContrast.collectAsState()

    // Microphone / Dictation State
    val isListeningToVoice by viewModel.isListeningToVoice.collectAsState()

    ArabicCorrectionContent(
        inputText = inputText,
        isAnalyzing = isAnalyzing,
        result = result,
        language = language,
        statusMessage = statusMessage,
        isGeneratingWithAi = isGeneratingWithAi,
        aiWritingPrompt = aiWritingPrompt,
        aiTone = aiTone,
        aiGeneratedText = aiGeneratedText,
        isWriteAiExpanded = isWriteAiExpanded,
        isReaderModeActive = isReaderModeActive,
        isSpeaking = isSpeaking,
        readerFontSize = readerFontSize,
        isHighContrast = isHighContrast,
        isListeningToVoice = isListeningToVoice,
        onToggleDictation = { viewModel.toggleDictation() },
        onStartDictation = { viewModel.startDictation() },
        onStopDictation = { viewModel.stopDictation() },
        onToggleReaderMode = { viewModel.toggleReaderMode() },
        onSpeakText = { viewModel.speakText(it) },
        onStopSpeaking = { viewModel.stopSpeaking() },
        onIncreaseFontSize = { viewModel.increaseReaderFontSize() },
        onDecreaseFontSize = { viewModel.decreaseReaderFontSize() },
        onToggleHighContrast = { viewModel.toggleHighContrast() },
        onToggleWriteAi = { viewModel.toggleWriteAiExpanded() },
        onAiPromptChange = { viewModel.updateAiWritingPrompt(it) },
        onAiToneSelect = { viewModel.setAiTone(it) },
        onAiGenerate = { prompt, tone -> viewModel.generateWithAi(prompt, tone) },
        onApplyAiText = { viewModel.applyAiGeneratedTextToInput() },
        onAppendAiText = { viewModel.appendAiGeneratedTextToInput() },
        onClearAiGeneratedText = { viewModel.clearAiGeneratedText() },
        onDismissStatus = { viewModel.clearStatusMessage() },
        onInputTextChange = { viewModel.updateInputText(it) },
        onCorrectClick = { viewModel.analyzeText() },
        onFixErrorsClick = { viewModel.fixAllErrors() },
        onClearClick = { viewModel.clearInput() },
        onSampleSelect = { viewModel.loadSample(it) },
        onApplyAll = { viewModel.applyAllCorrections() },
        onApplySingle = { viewModel.applySingleCorrection(it) },
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArabicCorrectionContent(
    inputText: String,
    isAnalyzing: Boolean,
    result: CorrectionResult,
    language: AppLanguage,
    statusMessage: String? = null,
    isGeneratingWithAi: Boolean = false,
    aiWritingPrompt: String = "",
    aiTone: String = "formal",
    aiGeneratedText: String? = null,
    isWriteAiExpanded: Boolean = false,
    isReaderModeActive: Boolean = false,
    isSpeaking: Boolean = false,
    readerFontSize: Int = 22,
    isHighContrast: Boolean = false,
    isListeningToVoice: Boolean = false,
    onToggleDictation: () -> Unit = {},
    onStartDictation: () -> Unit = {},
    onStopDictation: () -> Unit = {},
    onToggleReaderMode: () -> Unit = {},
    onSpeakText: (String) -> Unit = {},
    onStopSpeaking: () -> Unit = {},
    onIncreaseFontSize: () -> Unit = {},
    onDecreaseFontSize: () -> Unit = {},
    onToggleHighContrast: () -> Unit = {},
    onToggleWriteAi: () -> Unit = {},
    onAiPromptChange: (String) -> Unit = {},
    onAiToneSelect: (String) -> Unit = {},
    onAiGenerate: (String, String) -> Unit = { _, _ -> },
    onApplyAiText: () -> Unit = {},
    onAppendAiText: () -> Unit = {},
    onClearAiGeneratedText: () -> Unit = {},
    onDismissStatus: () -> Unit = {},
    onInputTextChange: (String) -> Unit,
    onCorrectClick: () -> Unit,
    onFixErrorsClick: () -> Unit = {},
    onClearClick: () -> Unit,
    onSampleSelect: (Int) -> Unit,
    onApplyAll: () -> Unit,
    onApplySingle: (CorrectionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = language == AppLanguage.ARABIC

    // Permission launcher for RECORD_AUDIO
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartDictation()
        } else {
            Toast.makeText(
                context,
                if (isAr) "يلزم السماح باستخدام المايكروفون للإملاء الصوتي" else "Microphone permission is required for voice dictation",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val onMicClick: () -> Unit = {
        if (isListeningToVoice) {
            onStopDictation()
        } else {
            val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                onStartDictation()
            } else {
                micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 150
        }
    }
    val showScrollToBottom by remember {
        derivedStateOf {
            listState.canScrollForward
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("arabic_correction_screen")
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("header_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NahwiFixLogo(
                                textSize = 22.sp,
                                iconSize = 22.dp,
                                nahwiColor = MaterialTheme.colorScheme.onSurface,
                                fixColor = MaterialTheme.colorScheme.primary,
                                badgeColor = MaterialTheme.colorScheme.primary,
                                showBadge = true
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (isAr) "تصحيح فوري" else "Instant Fix",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAr)
                                "أدخل أي نص عربي لتحليله وضبط قواعد النحو والإملاء وعلامات الترقيم بدقة."
                            else
                                "Enter Arabic text to inspect grammar, spelling, hamza, and punctuation.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isAr) "أمثلة سريعة للتجربة:" else "Quick demo samples:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = false,
                                onClick = { onSampleSelect(0) },
                                label = { Text(if (isAr) "همزة وعطف وترقيم" else "Hamza & Waw") }
                            )
                            FilterChip(
                                selected = false,
                                onClick = { onSampleSelect(1) },
                                label = { Text(if (isAr) "تطابق الفعل والفاعل" else "Agreement") }
                            )
                            FilterChip(
                                selected = false,
                                onClick = { onSampleSelect(2) },
                                label = { Text(if (isAr) "تنوين وجر وإعراب" else "Case & Tanween") }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Navigation Scroll Jump Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = {
                                    coroutineScope.launch {
                                        // Scroll smoothly to Arabic Text Input Card (index 4)
                                        listState.animateScrollToItem(4)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                label = {
                                    Text(
                                        text = if (isAr) "المحرر ⬇" else "Editor ⬇",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            AssistChip(
                                onClick = {
                                    coroutineScope.launch {
                                        val count = listState.layoutInfo.totalItemsCount
                                        if (count > 0) {
                                            listState.animateScrollToItem(count - 1)
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                },
                                label = {
                                    Text(
                                        text = if (isAr) "أسفل الصفحة ⬇" else "Bottom ⬇",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }
            }

        // Status Feedback Banner
        if (!statusMessage.isNullOrBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("status_feedback_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = statusMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(
                            onClick = onDismissStatus,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Close",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Reader Mode & Accessibility Card (وضع القارئ الصوتي الميسر لغير القادرين على القراءة)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reader_mode_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isReaderModeActive) {
                        if (isHighContrast) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (isReaderModeActive) BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isReaderModeActive) 4.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleReaderMode() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isReaderModeActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = if (isReaderModeActive) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isAr) "وضع القارئ الصوتي الميسّر" else "Accessible Reader Mode",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isReaderModeActive && isHighContrast) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isReaderModeActive) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (isReaderModeActive) (if (isAr) "مُفَعَّل" else "ACTIVE") else (if (isAr) "لمن لا يجيد القراءة" else "Accessibility"),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isReaderModeActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isAr) "استمع للنصوص بصوت واضح مع تكبير الخط وتباين الألوان" else "Listen to spoken text with enlarged font & high contrast",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isReaderModeActive && isHighContrast) MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onToggleReaderMode,
                            modifier = Modifier.testTag("toggle_reader_mode_button")
                        ) {
                            Icon(
                                imageVector = if (isReaderModeActive) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isReaderModeActive) "Collapse Reader Mode" else "Expand Reader Mode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Reader Mode Active Controls & Big Display
                    AnimatedVisibility(visible = isReaderModeActive) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            // Speaking Status indicator
                            if (isSpeaking) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isAr) "جارٍ القراءة الصوتية بصوت فصيح الآن..." else "Speaking Arabic text aloud now...",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }

                                        IconButton(
                                            onClick = onStopSpeaking,
                                            modifier = Modifier.size(28.dp).testTag("reader_stop_speaking_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Stop,
                                                contentDescription = "Stop",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            // Speech & Dictation trigger buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Voice Dictation for hands-free or non-literate input
                                Button(
                                    onClick = onMicClick,
                                    modifier = Modifier.weight(1f).height(46.dp).testTag("reader_dictate_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isListeningToVoice) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                        contentColor = if (isListeningToVoice) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isListeningToVoice) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isListeningToVoice) {
                                            if (isAr) "إيقاف الإملاء" else "Stop"
                                        } else {
                                            if (isAr) "تحدث بصوتك" else "Dictate"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        val text = if (result.correctedText.isNotBlank()) result.correctedText else inputText
                                        onSpeakText(text)
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp).testTag("reader_speak_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Hearing,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "انطق النص الآن" else "Read Aloud",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (isSpeaking) {
                                    FilledTonalButton(
                                        onClick = onStopSpeaking,
                                        modifier = Modifier.height(46.dp).testTag("reader_stop_btn"),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isAr) "إيقاف" else "Stop",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Accessibility Settings: Font Size & High Contrast
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Font Size Controls
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "حجم الخط: ${readerFontSize}sp" else "Size: ${readerFontSize}sp",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = onDecreaseFontSize,
                                        modifier = Modifier.size(32.dp).testTag("reader_font_decrease_btn")
                                    ) {
                                        Text("A-", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    IconButton(
                                        onClick = onIncreaseFontSize,
                                        modifier = Modifier.size(32.dp).testTag("reader_font_increase_btn")
                                    ) {
                                        Text("A+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }

                                // High Contrast Toggle
                                FilterChip(
                                    selected = isHighContrast,
                                    onClick = onToggleHighContrast,
                                    label = {
                                        Text(
                                            text = if (isAr) "تباين عالٍ" else "Contrast",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Big, High-Legibility Text Display Box for effortless reading
                            val textToDisplay = if (result.correctedText.isNotBlank()) result.correctedText else inputText
                            if (textToDisplay.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isHighContrast) androidx.compose.ui.graphics.Color.Black else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        width = if (isHighContrast) 2.dp else 1.dp,
                                        color = if (isHighContrast) androidx.compose.ui.graphics.Color.Yellow else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isAr) "معاينة القراءة المكبرة والمسموعة:" else "Accessible Reading View:",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isHighContrast) androidx.compose.ui.graphics.Color.Yellow else MaterialTheme.colorScheme.primary
                                            )
                                            IconButton(
                                                onClick = { onSpeakText(textToDisplay) },
                                                modifier = Modifier.size(28.dp).testTag("reader_inline_speak_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = "Speak",
                                                    tint = if (isHighContrast) androidx.compose.ui.graphics.Color.Yellow else MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = textToDisplay,
                                            style = TextStyle(
                                                fontSize = readerFontSize.sp,
                                                lineHeight = (readerFontSize * 1.6f).sp,
                                                textDirection = TextDirection.Rtl,
                                                textAlign = TextAlign.Right,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isHighContrast) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Write With AI Section (الكتابة بالذكاء الاصطناعي)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("write_with_ai_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleWriteAi() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isAr) "الكتابة بالذكاء الاصطناعي" else "Write with AI",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Gemini",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isAr) "صياغة نصوص فصيحة، رسائل ومقالات" else "Compose & rephrase Arabic texts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onToggleWriteAi,
                            modifier = Modifier.testTag("toggle_write_ai_button")
                        ) {
                            Icon(
                                imageVector = if (isWriteAiExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isWriteAiExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Expandable Body
                    AnimatedVisibility(visible = isWriteAiExpanded) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            // Quick Inspiration Prompts
                            Text(
                                text = if (isAr) "نماذج وقوالب جاهزة:" else "Quick templates:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val templates = if (isAr) listOf(
                                    "رسالة شكر وتقدير رسمية" to "اكتب رسالة شكر وتقدير رسمية موجهة لمدير العمل أو الزملاء بأسلوب فصيح ومؤثر.",
                                    "طلب إجازة رسمي" to "صياغة طلب إجازة رسمي لجهة العمل مع خالص الاحترام والتقدير.",
                                    "مقدمة مقال عن اللغة العربية" to "اكتب مقدمة مقال رصين وجذاب عن مكانة اللغة العربية وجماليات النحو والبلاغة.",
                                    "إعادة صياغة النص بأسلوب أفصح" to "أعد صياغة النص المكتوب في المحرر بأسلوب عربي فصيح ورصين ومترابط."
                                ) else listOf(
                                    "Formal thank-you letter" to "Write a formal Arabic thank-you letter to management with eloquence.",
                                    "Official leave request" to "Draft an official leave request in formal Arabic.",
                                    "Arabic essay intro" to "Write a compelling Arabic essay introduction on literature and grammar.",
                                    "Rephrase current text" to "Rephrase the current text into polished, eloquent Arabic."
                                )

                                templates.forEach { (label, promptText) ->
                                    FilterChip(
                                        selected = aiWritingPrompt == promptText,
                                        onClick = {
                                            onAiPromptChange(promptText)
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Prompt input field
                            OutlinedTextField(
                                value = aiWritingPrompt,
                                onValueChange = onAiPromptChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_prompt_input"),
                                placeholder = {
                                    Text(
                                        text = if (isAr)
                                            "ماذا تريد أن يكتب الذكاء الاصطناعي؟ (مثال: خطاب تهنئة، مقدمة تقرير...)"
                                        else
                                            "What should AI write? (e.g. formal email, report introduction...)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    textDirection = if (isAr) TextDirection.Rtl else TextDirection.Ltr,
                                    textAlign = if (isAr) TextAlign.Right else TextAlign.Left,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                ),
                                minLines = 2,
                                maxLines = 4
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tone selection chips
                            Text(
                                text = if (isAr) "الأسلوب / النبرة:" else "Tone of voice:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val tones = listOf(
                                    "formal" to if (isAr) "رسمي وفصيح" else "Formal",
                                    "creative" to if (isAr) "إبداعي وبلاغي" else "Creative",
                                    "concise" to if (isAr) "موجز ومباشر" else "Concise",
                                    "academic" to if (isAr) "أكاديمي رصين" else "Academic",
                                    "persuasive" to if (isAr) "إقناعي مؤثر" else "Persuasive"
                                )
                                tones.forEach { (toneKey, toneLabel) ->
                                    FilterChip(
                                        selected = aiTone == toneKey,
                                        onClick = { onAiToneSelect(toneKey) },
                                        label = { Text(toneLabel, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Generate Button
                            Button(
                                onClick = {
                                    val promptToUse = if (aiWritingPrompt.isNotBlank()) {
                                        aiWritingPrompt
                                    } else if (inputText.isNotBlank()) {
                                        if (isAr) "أعد صياغة وتحسين النص التالي بأسلوب عربي فصيح" else "Rephrase and polish the following text"
                                    } else {
                                        if (isAr) "اكتب رسالة شكر وتقدير رسمية بأسلوب عربي فصيح" else "Write an eloquent Arabic thank-you note"
                                    }
                                    onAiGenerate(promptToUse, aiTone)
                                },
                                enabled = !isGeneratingWithAi,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("ai_generate_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                if (isGeneratingWithAi) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isAr) "جارٍ الصياغة والتوليد..." else "Generating with AI...")
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isAr) "اكتب الآن بالذكاء الاصطناعي" else "Generate with AI",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Display AI Generated Result
                            if (!aiGeneratedText.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ai_generated_result_card"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isAr) "النص المكتوب بالذكاء الاصطناعي:" else "AI Generated Text:",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Row {
                                                // Copy button
                                                IconButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                        clipboard?.setPrimaryClip(ClipData.newPlainText("AI Text", aiGeneratedText))
                                                        Toast.makeText(context, if (isAr) "تم نسخ النص المولد" else "Copied", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(30.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copy",
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                // Clear button
                                                IconButton(
                                                    onClick = onClearAiGeneratedText,
                                                    modifier = Modifier.size(30.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Clear,
                                                        contentDescription = "Clear",
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = aiGeneratedText,
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                lineHeight = 26.sp,
                                                textDirection = TextDirection.Rtl,
                                                textAlign = TextAlign.Right,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = onApplyAiText,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("apply_ai_text_button"),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isAr) "استبدال بالمحرر" else "Use in Editor",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            OutlinedButton(
                                                onClick = onAppendAiText,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("append_ai_text_button"),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isAr) "إلحاق بالمحرر" else "Append",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Arabic Text Input Area Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("arabic_input_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title and Input Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Spellcheck,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "مساحة إدخال النص العربي" else "Arabic Text Input",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Voice Dictation (Microphone) button
                            IconButton(
                                onClick = onMicClick,
                                modifier = Modifier
                                    .testTag("dictate_mic_button")
                                    .then(
                                        if (isListeningToVoice)
                                            Modifier.background(MaterialTheme.colorScheme.errorContainer, shape = CircleShape)
                                        else
                                            Modifier
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isListeningToVoice) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = if (isAr) "الإملاء الصوتي" else "Voice dictation",
                                    tint = if (isListeningToVoice) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Read text aloud button for non-readers
                            IconButton(
                                onClick = { onSpeakText(inputText) },
                                modifier = Modifier.testTag("speak_input_text_button")
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.RecordVoiceOver,
                                    contentDescription = if (isAr) "استمع للنص" else "Listen to text",
                                    tint = if (isSpeaking) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Paste button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                                    if (!clip.isNullOrBlank()) {
                                        onInputTextChange(clip)
                                        Toast.makeText(context, if (isAr) "تم لصق النص" else "Text pasted", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.testTag("paste_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Clear button
                            if (inputText.isNotEmpty()) {
                                IconButton(
                                    onClick = onClearClick,
                                    modifier = Modifier.testTag("clear_text_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text Input Area with RTL Support
                    val currentFontSize = if (isReaderModeActive) readerFontSize.sp else 17.sp
                    val currentLineHeight = if (isReaderModeActive) (readerFontSize * 1.6f).sp else 28.sp

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = onInputTextChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = if (isReaderModeActive) 180.dp else 150.dp, max = 320.dp)
                                .testTag("arabic_text_input"),
                            placeholder = {
                                Text(
                                    text = if (isAr)
                                        "اكتب أو الصق النص العربي هنا للتدقيق اللغوي والتصحيح النحوي..."
                                    else
                                        "Type or paste Arabic text here for grammar and spell checking...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDirection = TextDirection.Rtl,
                                        textAlign = TextAlign.Right
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = currentFontSize,
                                lineHeight = currentLineHeight,
                                textDirection = TextDirection.Rtl,
                                textAlign = TextAlign.Right,
                                fontWeight = if (isReaderModeActive) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isReaderModeActive && isHighContrast) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isReaderModeActive && isHighContrast) androidx.compose.ui.graphics.Color.Yellow else MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (isReaderModeActive && isHighContrast) androidx.compose.ui.graphics.Color.Yellow.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                focusedContainerColor = if (isReaderModeActive && isHighContrast) androidx.compose.ui.graphics.Color.Black else MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = if (isReaderModeActive && isHighContrast) androidx.compose.ui.graphics.Color.Black else MaterialTheme.colorScheme.surface
                            ),
                            keyboardOptions = KeyboardOptions.Default
                        )
                    }

                    // Live Voice Dictation Active Banner
                    AnimatedVisibility(
                        visible = isListeningToVoice,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("dictation_active_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isAr) "جارٍ الاستماع لإملائك الصوتي باللغة العربية..." else "Listening to Arabic voice dictation...",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                OutlinedButton(
                                    onClick = onStopDictation,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp).testTag("stop_dictation_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "إيقاف" else "Stop",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Stats Counter Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val wordCount = if (inputText.isBlank()) 0 else inputText.trim().split(Regex("\\s+")).size
                        val charCount = inputText.length

                        Text(
                            text = "$wordCount ${if (isAr) "كلمات" else "words"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$charCount ${if (isAr) "أحرف" else "characters"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (result.items.isNotEmpty()) {
                                "${result.items.size} ${if (isAr) "ملاحظات للتصحيح" else "issues detected"}"
                            } else {
                                if (isAr) "جاهز للتدقيق" else "Ready"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (result.items.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons Row: Fix Errors & Check/Analyze
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Fix All Errors Button (Direct 1-tap solution)
                        Button(
                            onClick = onFixErrorsClick,
                            enabled = !isAnalyzing && inputText.isNotBlank() && result.items.isNotEmpty(),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(52.dp)
                                .testTag("fix_errors_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "تصحيح الأخطاء (${result.items.size})" else "Fix Errors (${result.items.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Re-check / Analyze Button
                        FilledTonalButton(
                            onClick = onCorrectClick,
                            enabled = !isAnalyzing && inputText.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("correct_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "تدقيق" else "Check",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Corrected Result Section (Appears after correction)
        if (result.correctedText.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("corrected_result_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.DoneAll,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "النص بعد التصحيح" else "Corrected Text",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Speak corrected text button
                                FilledTonalButton(
                                    onClick = { onSpeakText(result.correctedText) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("speak_corrected_button")
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Hearing,
                                        contentDescription = "Speak",
                                        tint = if (isSpeaking) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "استمع" else "Listen",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Copy corrected text button
                                FilledTonalButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        val clip = ClipData.newPlainText("Corrected Arabic Text", result.correctedText)
                                        clipboard?.setPrimaryClip(clip)
                                        Toast.makeText(
                                            context,
                                            if (isAr) "تم نسخ النص المصحح إلى الحافظة" else "Corrected text copied",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("copy_corrected_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "نسخ" else "Copy",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                if (result.items.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = onApplyAll,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        modifier = Modifier.testTag("apply_to_input_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isAr) "تطبيق الكل" else "Fix All",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Box displaying the corrected text in Arabic typography
                        val correctedFontSize = if (isReaderModeActive) readerFontSize.sp else 17.sp
                        val correctedLineHeight = if (isReaderModeActive) (readerFontSize * 1.6f).sp else 28.sp

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isReaderModeActive && isHighContrast) androidx.compose.ui.graphics.Color.Black
                                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                )
                                .border(
                                    width = if (isReaderModeActive && isHighContrast) 2.dp else 1.dp,
                                    color = if (isReaderModeActive && isHighContrast) androidx.compose.ui.graphics.Color.Yellow
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Text(
                                text = result.correctedText,
                                style = TextStyle(
                                    fontSize = correctedFontSize,
                                    lineHeight = correctedLineHeight,
                                    textDirection = TextDirection.Rtl,
                                    textAlign = TextAlign.Right,
                                    fontWeight = if (isReaderModeActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isReaderModeActive && isHighContrast) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (result.items.isEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "نصك سليم 100% ولا يحتوي على أخطاء نحوية أو إملائية." else "Your text is grammatically correct with no detected issues.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Detailed Corrections Breakdown
        if (result.items.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "قائمة الملاحظات والتصويبات (${result.items.size}):" else "Corrections Breakdown (${result.items.size}):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            items(result.items, key = { it.id }) { item ->
                CorrectionItemCard(
                    item = item,
                    isAr = isAr,
                    onApply = { onApplySingle(item) },
                    onSpeak = { text -> onSpeakText(text) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("footer_scroll_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "العودة إلى أعلى الصفحة ⬆" else "Back to Top ⬆",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Floating Quick Scroll Controls (Up & Down)
    Column(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            SmallFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier = Modifier.testTag("scroll_to_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isAr) "تمرير للأعلى" else "Scroll to top",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = showScrollToBottom,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            SmallFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val totalItems = listState.layoutInfo.totalItemsCount
                        if (totalItems > 0) {
                            listState.animateScrollToItem(totalItems - 1)
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier = Modifier.testTag("scroll_to_bottom_button")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isAr) "تمرير للأسفل" else "Scroll to bottom",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
}

@Composable
fun CorrectionItemCard(
    item: CorrectionItem,
    isAr: Boolean,
    onApply: () -> Unit,
    onSpeak: (String) -> Unit = {}
) {
    val badgeColor = when (item.category) {
        CorrectionCategory.GRAMMAR -> BadgeGrammar
        CorrectionCategory.PUNCTUATION -> BadgePunctuation
        CorrectionCategory.SPELLING_HAMZA, CorrectionCategory.SPELLING_GENERAL -> BadgeSpelling
        CorrectionCategory.AGREEMENT -> BadgeAgreement
    }

    val catTitle = if (isAr) item.category.titleAr else item.category.titleEn
    val desc = if (isAr) item.issueDescriptionAr else item.issueDescriptionEn
    val explanation = if (isAr) item.explanationAr else item.explanationEn

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("correction_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category Badge & Quick Fix Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = catTitle,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val speechContent = "${if (isAr) "الصواب هو" else "Correction is"}: ${item.suggestedReplacement}. $desc"
                            onSpeak(speechContent)
                        },
                        modifier = Modifier.size(32.dp).testTag("speak_item_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak correction",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    FilledTonalButton(
                        onClick = onApply,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("apply_item_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Apply",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAr) "تطبيق" else "Apply",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Original Snippet vs Suggested Replacement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.originalSnippet,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "  ←  ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = item.suggestedReplacement,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
