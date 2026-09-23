package com.example.nahwifix.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nahwifix.model.AppLanguage

/**
 * A smooth, animated loading indicator card that plays while the application is processing
 * and analyzing the text for Arabic grammar, punctuation, and morphology.
 */
@Composable
fun ArabicProcessingLoadingIndicator(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val infiniteTransition = rememberInfiniteTransition(label = "processing_animation")

    // Rotation angle for circular orbits
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsing glow scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Shimmer wave progress for the progress bar
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = 650f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // 3 Dots bounce
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 0, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("animated_processing_indicator"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = primaryColor.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Orbital Ring with Center Arabic Letter & AI Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Background Soft Aura
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.12f))
                )

                // Spinning Two-Tone Dual Arc Orbit
                Canvas(
                    modifier = Modifier
                        .size(68.dp)
                        .rotate(rotationAngle)
                ) {
                    // Outer primary arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                primaryColor.copy(alpha = 0.1f),
                                primaryColor,
                                secondaryColor,
                                primaryColor.copy(alpha = 0.1f)
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Accent counter arc
                    drawArc(
                        color = tertiaryColor,
                        startAngle = 290f,
                        sweepAngle = 40f,
                        useCenter = false,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner badge with Arabic 'ن' (for Nahwi)
                Surface(
                    shape = CircleShape,
                    color = primaryColor,
                    modifier = Modifier.size(38.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "ن",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Status Text with pulsating dots
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAr) "جارٍ تدقيق النص العربي بالذكاء الاصطناعي" else "Analyzing Arabic text with AI",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                // 3 bouncing animated dots
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = dot1Alpha))
                    )
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = dot2Alpha))
                    )
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = dot3Alpha))
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Explanatory Subtitle
            Text(
                text = if (isAr)
                    "فحص الإعراب، مطابقة الفعل والفاعل، ضبط علامات الترقيم ورسم الهمزات..."
                else
                    "Checking grammar cases, agreement, punctuation marks & Hamza...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Smooth Shimmer Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(6.dp)) {
                    val brush = Brush.linearGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.2f),
                            secondaryColor,
                            primaryColor,
                            primaryColor.copy(alpha = 0.2f)
                        ),
                        start = Offset(shimmerTranslate, 0f),
                        end = Offset(shimmerTranslate + 180f, 0f)
                    )
                    drawRect(brush = brush)
                }
            }
        }
    }
}
