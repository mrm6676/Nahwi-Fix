package com.example.nahwifix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nahwifix.model.AppLanguage
import com.example.nahwifix.ui.NahwiFixViewModel
import com.example.nahwifix.ui.components.AppScreen
import com.example.nahwifix.ui.components.NahwiFixLogo
import com.example.nahwifix.ui.screens.ArabicCorrectionScreen
import com.example.nahwifix.ui.screens.AuthScreen
import com.example.nahwifix.ui.screens.HomeScreen
import com.example.nahwifix.ui.screens.PricingScreen
import com.example.nahwifix.ui.screens.RulesGuideScreen
import com.example.nahwifix.ui.screens.TermsScreen
import com.example.nahwifix.ui.theme.NahwiFixTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NahwiFixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val language by viewModel.language.collectAsState()

            val layoutDirection = if (language == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

            NahwiFixTheme(darkTheme = isDarkMode) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    MainAppScaffold(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: NahwiFixViewModel) {
    val language by viewModel.language.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isAr = language == AppLanguage.ARABIC

    var currentScreen by remember { mutableStateOf(AppScreen.CHECKER) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    NahwiFixLogo(
                        textSize = 21.sp,
                        iconSize = 22.dp,
                        nahwiColor = MaterialTheme.colorScheme.onSurface,
                        fixColor = MaterialTheme.colorScheme.primary,
                        badgeColor = MaterialTheme.colorScheme.primary,
                        showBadge = true
                    )
                },
                actions = {
                    val isReaderActive by viewModel.isReaderModeActive.collectAsState()
                    val isSpeaking by viewModel.isSpeaking.collectAsState()

                    IconButton(
                        onClick = { viewModel.toggleReaderMode() },
                        modifier = Modifier.testTag("reader_mode_top_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.RecordVoiceOver,
                            contentDescription = if (isAr) "وضع القارئ الصوتي" else "Audio Reader Mode",
                            tint = if (isReaderActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("lang_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                AppScreen.values().forEach { screen ->
                    val selected = currentScreen == screen
                    val label = if (isAr) screen.titleAr else screen.titleEn
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.CHECKER -> ArabicCorrectionScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.RULES -> RulesGuideScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.PRICING -> PricingScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.AUTH -> AuthScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.TERMS -> TermsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
        }
    }
}
