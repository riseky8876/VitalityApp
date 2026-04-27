package com.vitality.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitality.app.data.model.HealthStatus
import com.vitality.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VitalityRing(
    score: Int,
    status: HealthStatus,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    strokeWidth: Dp = 18.dp,
) {
    val animatedScore by animateFloatAsState(
        targetValue   = score.toFloat(),
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label         = "scoreAnim",
    )

    // Rotating shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAngle by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerAngle",
    )

    val ringColor = when (status) {
        HealthStatus.HEALTHY   -> listOf(RingGradientStart, RingGradientEnd)
        HealthStatus.ATTENTION -> listOf(AttentionYellow, AttentionYellow.copy(alpha = 0.6f))
        HealthStatus.POOR      -> listOf(PoorCoral, PoorCoral.copy(alpha = 0.6f))
    }

    val trackColor = NeuShadowDark.copy(alpha = 0.2f)

    Box(
        modifier          = modifier.size(size),
        contentAlignment  = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center      = Offset(this.size.width / 2f, this.size.height / 2f)
            val strokePx    = strokeWidth.toPx()
            val radius      = (this.size.width / 2f) - strokePx / 2f
            val ringSize    = Size(radius * 2, radius * 2)
            val topLeft     = Offset(center.x - radius, center.y - radius)
            val sweepAngle  = (animatedScore / 100f) * 360f

            // Track (background ring)
            drawArc(
                color       = trackColor,
                startAngle  = -90f,
                sweepAngle  = 360f,
                useCenter   = false,
                topLeft     = topLeft,
                size        = ringSize,
                style       = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            // Progress arc with gradient
            val gradientBrush = Brush.sweepGradient(
                colors  = ringColor,
                center  = center,
            )
            drawArc(
                brush       = gradientBrush,
                startAngle  = -90f,
                sweepAngle  = sweepAngle,
                useCenter   = false,
                topLeft     = topLeft,
                size        = ringSize,
                style       = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            // Glowing dot at progress tip
            if (sweepAngle > 5f) {
                val angleRad  = Math.toRadians((-90f + sweepAngle).toDouble())
                val dotX      = center.x + radius * cos(angleRad).toFloat()
                val dotY      = center.y + radius * sin(angleRad).toFloat()
                val dotColor  = ringColor.last()

                // Glow
                drawCircle(
                    color  = dotColor.copy(alpha = 0.25f),
                    radius = strokePx * 1.0f,
                    center = Offset(dotX, dotY),
                )
                // Dot
                drawCircle(
                    color  = dotColor,
                    radius = strokePx * 0.55f,
                    center = Offset(dotX, dotY),
                )
            }
        }

        // Center text content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text       = "${animatedScore.toInt()}",
                fontSize   = 52.sp,
                fontWeight = FontWeight.Bold,
                color      = when (status) {
                    HealthStatus.HEALTHY   -> HealthyGreenDark
                    HealthStatus.ATTENTION -> AttentionYellowDark
                    HealthStatus.POOR      -> PoorCoralDark
                },
                textAlign  = TextAlign.Center,
            )
            Text(
                text      = "/ 100",
                fontSize  = 14.sp,
                color     = TextTertiary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text      = when (status) {
                    HealthStatus.HEALTHY   -> "Kondisi Prima ✨"
                    HealthStatus.ATTENTION -> "Perlu Perhatian"
                    HealthStatus.POOR      -> "Perlu Tindakan"
                },
                fontSize  = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color     = when (status) {
                    HealthStatus.HEALTHY   -> HealthyGreen
                    HealthStatus.ATTENTION -> AttentionYellow
                    HealthStatus.POOR      -> PoorCoral
                },
                textAlign = TextAlign.Center,
            )
        }
    }
}
