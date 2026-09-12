package com.example.nahwifix.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nahwifix.ui.NahwiFixViewModel

/**
 * Main Home screen for NahwiFix, presenting the Arabic text input area
 * and 'Correct' button.
 */
@Composable
fun HomeScreen(
    viewModel: NahwiFixViewModel,
    modifier: Modifier = Modifier
) {
    ArabicCorrectionScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
