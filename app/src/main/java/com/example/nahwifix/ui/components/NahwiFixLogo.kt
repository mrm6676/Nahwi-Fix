package com.example.nahwifix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nahwifix.ui.theme.LogoDarkBackground
import com.example.nahwifix.ui.theme.LogoFixBlue
import com.example.nahwifix.ui.theme.LogoWhite

/**
 * Circular verification badge matching the NahwiFix logo mark in image.png:
 * Circular white/contrast border with centered verification checkmark and bottom-right tick accent.
 */
@Composable
fun NahwiFixBadgeIcon(
    modifier: Modifier = Modifier,
    size: Dp = 26.dp,
    tint: Color = LogoWhite
) {
    Box(
        modifier = modifier
            .size(size)
            .border(width = (size.value * 0.08f).coerceAtLeast(1.8f).dp, color = tint, shape = CircleShape)
            .padding((size.value * 0.12f).dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Verified Grammar",
            tint = tint,
            modifier = Modifier.size((size.value * 0.65f).dp)
        )
    }
}

/**
 * Full NahwiFix logo as shown in the reference image:
 * "Nahwi" in bold white/high-contrast + "Fix" in vivid electric blue + circular verification checkmark icon.
 */
@Composable
fun NahwiFixLogo(
    modifier: Modifier = Modifier,
    textSize: TextUnit = 20.sp,
    iconSize: Dp = 24.dp,
    nahwiColor: Color = MaterialTheme.colorScheme.onSurface,
    fixColor: Color = LogoFixBlue,
    badgeColor: Color = MaterialTheme.colorScheme.onSurface,
    showBadge: Boolean = true
) {
    Row(
        modifier = modifier.testTag("app_logo_nahwifix"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Nahwi" text
        Text(
            text = "Nahwi",
            fontSize = textSize,
            fontWeight = FontWeight.Black,
            color = nahwiColor,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        // "Fix" text
        Text(
            text = "Fix",
            fontSize = textSize,
            fontWeight = FontWeight.Black,
            color = fixColor,
            letterSpacing = 0.5.sp
        )
        if (showBadge) {
            Spacer(modifier = Modifier.width(8.dp))
            NahwiFixBadgeIcon(
                size = iconSize,
                tint = badgeColor
            )
        }
    }
}

/**
 * Dark pill container variant matching the exact dark-canvas brand image from the reference.
 */
@Composable
fun NahwiFixBrandPill(
    modifier: Modifier = Modifier,
    textSize: TextUnit = 18.sp,
    iconSize: Dp = 22.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LogoDarkBackground)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        NahwiFixLogo(
            textSize = textSize,
            iconSize = iconSize,
            nahwiColor = LogoWhite,
            fixColor = LogoFixBlue,
            badgeColor = LogoWhite,
            showBadge = true
        )
    }
}
