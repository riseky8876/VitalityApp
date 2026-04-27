package com.vitality.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitality.app.data.model.HealthHistory
import com.vitality.app.ui.components.*
import com.vitality.app.ui.theme.*
import com.vitality.app.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit,
) {
    val history by viewModel.historyState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeuBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Kembali",
                        tint               = TextPrimary,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Riwayat Kesehatan",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (history.isEmpty()) {
                EmptyHistoryCard()
            } else {
                // Chart card
                NeuCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text       = "Tren Skor Kesehatan",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                        )
                        Text(
                            text     = "${history.size} hari terakhir",
                            fontSize = 12.sp,
                            color    = TextTertiary,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HealthScoreChart(history = history)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Latest summary
                val latest = history.lastOrNull()
                if (latest != null) {
                    NeuCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text       = "Kondisi Terkini",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = TextPrimary,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                HistoryScoreItem("Keseluruhan", latest.overallScore)
                                HistoryScoreItem("Baterai",     latest.batteryScore)
                                HistoryScoreItem("Memori",      latest.ramScore)
                                HistoryScoreItem("Simpanan",    latest.storageScore)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Trend insight
                TrendInsightCard(history = history)

                Spacer(modifier = Modifier.height(16.dp))

                // History list
                NeuCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text       = "Log Harian",
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        history.reversed().take(14).forEach { h ->
                            HistoryListItem(history = h)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthScoreChart(history: List<HealthHistory>) {
    val animProgress by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "chartAnim",
    )

    val scores = history.map { it.overallScore.toFloat() }
    val minScore = (scores.minOrNull() ?: 0f) - 5f
    val maxScore = (scores.maxOrNull() ?: 100f) + 5f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        if (scores.size < 2) return@Canvas

        val width    = size.width
        val height   = size.height
        val padLeft  = 32f
        val padRight = 16f
        val padTop   = 16f
        val padBot   = 24f

        val chartW = width - padLeft - padRight
        val chartH = height - padTop - padBot

        // Grid lines
        for (i in 0..4) {
            val y = padTop + chartH * (1f - i / 4f)
            drawLine(
                color       = NeuShadowDark.copy(alpha = 0.25f),
                start       = Offset(padLeft, y),
                end         = Offset(width - padRight, y),
                strokeWidth = 1f,
            )
        }

        // Build path
        val points = scores.mapIndexed { idx, score ->
            val x = padLeft + idx.toFloat() / (scores.size - 1).toFloat() * chartW
            val y = padTop + chartH * (1f - ((score - minScore) / (maxScore - minScore)))
            Offset(x, y)
        }

        // Animated points (draw only up to animProgress)
        val visibleCount = (points.size * animProgress).toInt().coerceAtLeast(2)
        val visiblePoints = points.take(visibleCount)

        // Fill gradient
        if (visiblePoints.size >= 2) {
            val fillPath = Path().apply {
                moveTo(visiblePoints.first().x, padTop + chartH)
                visiblePoints.forEach { lineTo(it.x, it.y) }
                lineTo(visiblePoints.last().x, padTop + chartH)
                close()
            }
            drawPath(
                path  = fillPath,
                brush = Brush.verticalGradient(
                    colors     = listOf(
                        RingGradientStart.copy(alpha = 0.3f),
                        RingGradientStart.copy(alpha = 0.0f),
                    ),
                    startY = padTop,
                    endY   = padTop + chartH,
                ),
            )

            // Line
            val linePath = Path().apply {
                moveTo(visiblePoints.first().x, visiblePoints.first().y)
                visiblePoints.forEachIndexed { i, pt ->
                    if (i > 0) {
                        val prev = visiblePoints[i - 1]
                        val cp1x = (prev.x + pt.x) / 2f
                        cubicTo(cp1x, prev.y, cp1x, pt.y, pt.x, pt.y)
                    }
                }
            }
            drawPath(
                path  = linePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(RingGradientStart, RingGradientEnd)
                ),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )

            // Dots
            visiblePoints.forEach { pt ->
                drawCircle(color = NeuBackground, radius = 5f, center = pt)
                drawCircle(color = RingGradientStart, radius = 3.5f, center = pt)
            }
        }
    }
}

@Composable
private fun HistoryScoreItem(label: String, score: Int) {
    val color = scoreColor(score)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = "$score",
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = color,
        )
        Text(text = label, fontSize = 11.sp, color = TextTertiary)
    }
}

@Composable
private fun TrendInsightCard(history: List<HealthHistory>) {
    if (history.size < 2) return

    val recent = history.takeLast(3).map { it.overallScore }
    val older  = history.dropLast(3).takeLast(3).map { it.overallScore }

    val recentAvg = recent.average()
    val olderAvg  = if (older.isNotEmpty()) older.average() else recentAvg
    val delta     = recentAvg - olderAvg

    val (icon, message, color) = when {
        delta > 5  -> Triple("📈", "Perangkat Anda semakin sehat belakangan ini! Pertahankan kebiasaan baik ini.", HealthyGreen)
        delta < -5 -> Triple("📉", "Skor kesehatan sedikit menurun. Coba jalankan optimasi untuk membantu perangkat Anda.", AttentionYellow)
        else       -> Triple("📊", "Kondisi perangkat Anda stabil. Tidak ada perubahan signifikan.", BrandBlue)
    }

    NeuCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text       = "Analisis Tren",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text      = message,
                    fontSize  = 12.sp,
                    color     = TextSecondary,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun HistoryListItem(history: HealthHistory) {
    val dateStr = remember(history.date) {
        SimpleDateFormat("EEE, dd MMM HH:mm", Locale("id")).format(Date(history.date))
    }
    val color = scoreColor(history.overallScore)

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NeuBackground)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = dateStr, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniScore("🔋", history.batteryScore)
                MiniScore("💾", history.ramScore)
                MiniScore("📦", history.storageScore)
            }
        }
        Box(
            modifier         = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = "${history.overallScore}",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = color,
            )
        }
    }
}

@Composable
private fun MiniScore(emoji: String, score: Int) {
    Text(
        text     = "$emoji ${score}",
        fontSize = 11.sp,
        color    = TextTertiary,
    )
}

@Composable
private fun EmptyHistoryCard() {
    NeuCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier            = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "📅", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = "Belum Ada Riwayat",
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
                textAlign  = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text      = "Riwayat akan muncul setelah aplikasi digunakan beberapa kali. Buka kembali besok untuk melihat tren kesehatan perangkat Anda.",
                fontSize  = 13.sp,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
            )
        }
    }
}
