package com.vitality.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitality.app.data.model.OptimizationResult
import com.vitality.app.data.model.OptimizationStep
import com.vitality.app.ui.components.*
import com.vitality.app.ui.theme.*
import com.vitality.app.viewmodel.HealthViewModel
import java.util.Locale

@Composable
fun OptimizeScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit,
) {
    val optimizationState by viewModel.optimizationState.collectAsStateWithLifecycle()
    val isRunning = optimizationState != null && !optimizationState!!.isCompleted

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
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Kembali",
                        tint               = TextPrimary,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Optimasi Perangkat",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            NeuCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    OptimizeIconAnimated(isRunning = isRunning)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text       = when {
                            optimizationState == null       -> "Siap Mengoptimalkan"
                            optimizationState!!.isCompleted -> "Selesai! 🎉"
                            else                            -> "Sedang Mengoptimalkan…"
                        },
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        textAlign  = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text      = when {
                            optimizationState == null       -> "Tekan tombol di bawah untuk membersihkan cache, merapikan memori, dan menyegarkan sistem Anda."
                            optimizationState!!.isCompleted -> "Perangkat Anda kini lebih segar dan ringan!"
                            else                            -> "Mohon tunggu, proses ini hanya membutuhkan beberapa detik."
                        },
                        fontSize  = 13.sp,
                        color     = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp,
                    )

                    if (optimizationState?.isCompleted == true) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ResultSummary(result = optimizationState!!)
                    }
                }
            }

            if (optimizationState != null) {
                Spacer(modifier = Modifier.height(16.dp))
                OptimizationStepsList(steps = optimizationState!!.steps)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isRunning) {
                NeuCard(
                    modifier     = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (optimizationState?.isCompleted == true) {
                                viewModel.resetOptimization()
                            } else {
                                viewModel.runOptimization()
                            }
                        },
                    cornerRadius = 20.dp,
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector        = if (optimizationState?.isCompleted == true)
                                Icons.Rounded.Refresh else Icons.Rounded.AutoFixHigh,
                            contentDescription = null,
                            tint               = BrandTeal,
                            modifier           = Modifier.size(22.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text       = if (optimizationState?.isCompleted == true)
                                "Optimalkan Lagi" else "Mulai Optimasi",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = BrandTeal,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NeuCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector        = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint               = HealthyGreen,
                        modifier           = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text       = "Proses ini aman sepenuhnya. Vitality hanya membersihkan file sementara dan merapikan memori — tidak ada data penting yang akan dihapus.",
                        fontSize   = 12.sp,
                        color      = TextSecondary,
                        lineHeight = 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun OptimizeIconAnimated(isRunning: Boolean) {
    // Fix: separate animated vs static, avoid type mismatch between
    // InfiniteRepeatableSpec and SnapSpec in single animateFloat call
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")

    val infiniteRotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "iconRotateInfinite",
    )

    // Use infinite rotation only when running, otherwise show static icon
    val rotation = if (isRunning) infiniteRotation else 0f

    Box(
        modifier         = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(BrandTeal.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector        = Icons.Rounded.AutoFixHigh,
            contentDescription = null,
            tint               = BrandTeal,
            modifier           = Modifier
                .size(40.dp)
                .graphicsLayer { rotationZ = rotation },
        )
    }
}

@Composable
private fun OptimizationStepsList(steps: List<OptimizationStep>) {
    NeuCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text       = "Langkah-langkah",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
            )
            Spacer(modifier = Modifier.height(12.dp))

            steps.forEachIndexed { index, step ->
                OptimizationStepItem(step = step, index = index)
                if (index < steps.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun OptimizationStepItem(step: OptimizationStep, index: Int) {
    val color = when {
        step.isCompleted -> HealthyGreen
        step.isRunning   -> BrandTeal
        else             -> TextTertiary
    }

    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier         = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            if (step.isRunning) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(18.dp),
                    color       = BrandTeal,
                    strokeWidth = 2.dp,
                )
            } else if (step.isCompleted) {
                Icon(
                    imageVector        = Icons.Rounded.Check,
                    contentDescription = null,
                    tint               = HealthyGreen,
                    modifier           = Modifier.size(16.dp),
                )
            } else {
                Text(
                    text       = "${index + 1}",
                    fontSize   = 12.sp,
                    color      = TextTertiary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = step.title,
                fontSize   = 13.sp,
                fontWeight = if (step.isRunning) FontWeight.SemiBold else FontWeight.Normal,
                color      = when {
                    step.isRunning   -> TextPrimary
                    step.isCompleted -> HealthyGreenDark
                    else             -> TextTertiary
                },
            )
            if (step.isRunning || step.isCompleted) {
                Text(
                    text     = step.description,
                    fontSize = 11.sp,
                    color    = TextTertiary,
                )
            }
        }
    }
}

@Composable
private fun ResultSummary(result: OptimizationResult) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HealthyGreenBg)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ResultItem(
            emoji = "🗑️",
            value = "${String.format(Locale.US, "%.0f", result.freedStorageMb)} MB",
            label = "Ruang dibebaskan",
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .background(HealthyGreen.copy(alpha = 0.3f))
        )
        ResultItem(
            emoji = "⚡",
            value = "${String.format(Locale.US, "%.0f", result.freedRamMb)} MB",
            label = "Memori disegarkan",
        )
    }
}

@Composable
private fun ResultItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 20.sp)
        Text(
            text       = value,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            color      = HealthyGreenDark,
        )
        Text(
            text     = label,
            fontSize = 11.sp,
            color    = TextSecondary,
        )
    }
}
