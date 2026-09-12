package com.example.nahwifix.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppScreen(
    val route: String,
    val titleAr: String,
    val titleEn: String,
    val icon: ImageVector
) {
    CHECKER("checker", "المصحح", "Checker", Icons.Default.Spellcheck),
    RULES("rules", "القواعد", "Rules", Icons.AutoMirrored.Filled.MenuBook),
    PRICING("pricing", "الأسعار", "Pricing", Icons.Default.Payments),
    AUTH("auth", "الحساب", "Account", Icons.Default.AccountCircle),
    TERMS("terms", "الشروط", "Terms", Icons.Default.Description)
}
