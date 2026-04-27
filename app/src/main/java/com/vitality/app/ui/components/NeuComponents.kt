package com.vitality.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitality.app.ui.theme.*

// ─────────────────────────────────────────────────────────────
// NEUMORPHIC MODIFIER EXTENSIONS
// ─────────────────────────────────────────────────────────────

fun Modifier.neumorphicSurface(
    cornerRadius: Dp = 20.dp,
    elevation: Float = 8f,
    backgroundColor: Color = NeuBackground,
): Modifier = this
    .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val shadowOffset = elevation

        // Dark shadow (bottom-right)
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        elevation * 1.5f,
                        shadowOffset,
                        shadowOffset,
                        NeuShadowDark.copy(alpha = 0.7f).toArgb()
                    )
                }
            }
            canvas.drawRoundRect(
                left   = 0f,
                top    = 0f,
                right  = size.width,
                bottom = size.height,
                radiusX = radiusPx,
                radiusY = radiusPx,
                paint  = paint,
            )
        }

        // Light shadow (top-left)
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        elevation * 1.5f,
                        -shadowOffset,
                        -shadowOffset,
                        NeuShadowLight.copy(alpha = 0.9f).toArgb()
                    )
                }
            }
            canvas.drawRoundRect(
                left   = 0f,
                top    = 0f,
                right  = size.width,
                bottom = size.height,
                radiusX = radiusPx,
                radiusY = radiusPx,
                paint  = paint,
            )
        }
    }
    .clip(RoundedCornerShape(cornerRadius))
    .background(backgroundColor)

fun Modifier.neumorphicPressed(
    cornerRadius: Dp = 20.dp,
    backgroundColor: Color = NeuBackground,
): Modifier = this
    .drawBehind {
        val radiusPx = cornerRadius.toPx()

        // Inset shadow effect (pressed look)
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = NeuShadowDark.copy(alpha = 0.5f).toArgb()
                }
            }
            canvas.drawRoundRect(
                left   = 0f,
                top    = 0f,
                right  = size.width,
                bottom = size.height,
                radiusX = radiusPx,
                radiusY = radiusPx,
                paint  = paint,
            )
        }
    }
    .clip(RoundedCornerShape(cornerRadius))
    .background(backgroundColor.copy(alpha = 0.95f))

// ─────────────────────────────────────────────────────────────
// NEUMORPHIC CARD
// ─────────────────────────────────────────────────────────────

@Composable
fun NeuCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Float = 10f,
    backgroundColor: Color = NeuBackground,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .neumorphicSurface(
                cornerRadius    = cornerRadius,
                elevation       = elevation,
                backgroundColor = backgroundColor,
            )
            .padding(20.dp),
        content = content,
    )
}

// ─────────────────────────────────────────────────────────────
// NEUMORPHIC BUTTON
// ─────────────────────────────────────────────────────────────

@Composable
fun NeuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = 16.dp,
    content: @Composable RowScope.() -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .then(
                if (pressed) Modifier.neumorphicPressed(cornerRadius)
                else Modifier.neumorphicSurface(cornerRadius, elevation = 8f)
            )
            .clickable(
                enabled             = enabled,
                interactionSource   = remember { MutableInteractionSource() },
                indication          = null,
                onClick             = onClick,
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically,
        content               = content,
    )
}

// ─────────────────────────────────────────────────────────────
// STATUS CHIP
// ─────────────────────────────────────────────────────────────

@Composable
fun StatusChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = label,
            color      = color,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// ANIMATED PROGRESS BAR (Neumorphic style)
// ─────────────────────────────────────────────────────────────

@Composable
fun NeuProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = HealthyGreen,
    trackColor: Color = NeuShadowDark.copy(alpha = 0.3f),
    height: Dp = 8.dp,
    cornerRadius: Dp = 8.dp,
) {
    val animatedProgress by animateFloatAsState(
        targetValue  = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label        = "progressAnim",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(trackColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(color, color.copy(alpha = 0.7f))
                    )
                ),
        )
    }
}

// ─────────────────────────────────────────────────────────────
// SCORE INDICATOR
// ─────────────────────────────────────────────────────────────

@Composable
fun ScoreIndicator(
    score: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    val color = scoreColor(score)

    Column(
        modifier          = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .neumorphicSurface(cornerRadius = 12.dp, elevation = 6f)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = "$score",
                color      = color,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text      = label,
            fontSize  = 11.sp,
            color     = TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

fun scoreColor(score: Int): Color = when {
    score >= 75 -> HealthyGreen
    score >= 50 -> AttentionYellow
    else        -> PoorCoral
}

fun scoreStatusLabel(score: Int): String = when {
    score >= 75 -> "Sehat"
    score >= 50 -> "Perlu Perhatian"
    else        -> "Perlu Tindakan"
}
