package com.vitality.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitality.app.data.model.StorageInfo
import com.vitality.app.ui.components.*
import com.vitality.app.ui.theme.*
import com.vitality.app.viewmodel.HealthViewModel
import java.util.Locale

@Composable
fun StorageDetailScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit,
    onOptimize: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val storage = uiState.storageInfo

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBackIosNew, "Kembali", tint = TextPrimary)
                }
                Spacer(Modifier.width(8.dp))
                Text("Detail Penyimpanan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(Modifier.height(20.dp))

            // Visual storage pie-like card
            NeuCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Penggunaan Penyimpanan", fontSize = 13.sp, color = TextSecondary)
                    Spacer(Modifier.height(12.dp))

                    VitalityRing(
                        score  = storage.healthScore,
                        status = when {
                            storage.healthScore >= 75 -> com.vitality.app.data.model.HealthStatus.HEALTHY
                            storage.healthScore >= 50 -> com.vitality.app.data.model.HealthStatus.ATTENTION
                            else                      -> com.vitality.app.data.model.HealthStatus.POOR
                        },
                        size = 180.dp,
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        StorageStatItem(
                            label = "Terpakai",
                            value = "${String.format(Locale.US, "%.1f", storage.usedGb)} GB",
                            color = scoreColor(storage.healthScore),
                        )
                        StorageStatItem(
                            label = "Kosong",
                            value = "${String.format(Locale.US, "%.1f", storage.freeGb)} GB",
                            color = HealthyGreen,
                        )
                        StorageStatItem(
                            label = "Total",
                            value = "${String.format(Locale.US, "%.0f", storage.totalGb)} GB",
                            color = BrandBlue,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Storage health narrative
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("🗂️ Kondisi Penyimpanan", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text      = storage.description,
                        fontSize  = 13.sp,
                        color     = TextSecondary,
                        lineHeight = 19.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    NeuProgressBar(
                        progress = storage.usagePercent / 100f,
                        color    = scoreColor(storage.healthScore),
                        height   = 12.dp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("0%", fontSize = 11.sp, color = TextTertiary)
                        Text(
                            "${String.format(Locale.US, "%.1f", storage.usagePercent)}% terpakai",
                            fontSize = 11.sp,
                            color    = scoreColor(storage.healthScore),
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text("100%", fontSize = 11.sp, color = TextTertiary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Storage health estimate for eMMC/UFS
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("💾 Estimasi Umur Penyimpanan", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(Modifier.height(10.dp))

                    val (narrative, life, healthColor) = estimateStorageLife(storage)
                    Text(narrative, fontSize = 13.sp, color = TextSecondary, lineHeight = 19.sp)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(healthColor.copy(alpha = 0.1f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("⏳", fontSize = 20.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Estimasi sisa umur", fontSize = 11.sp, color = TextTertiary)
                            Text(life, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = healthColor)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tips for freeing storage
            if (storage.usagePercent > 70f) {
                NeuCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("🧹 Cara Membebaskan Ruang", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(Modifier.height(10.dp))

                        listOf(
                            "Hapus foto & video duplikat atau yang tidak diperlukan",
                            "Pindahkan foto ke Google Photos / cloud storage",
                            "Hapus cache aplikasi yang jarang digunakan",
                            "Uninstall aplikasi yang sudah tidak dipakai",
                            "Pindahkan file besar ke kartu memori (jika tersedia)",
                        ).forEach { tip ->
                            Row(
                                modifier          = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Text("•", fontSize = 13.sp, color = BrandTeal, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text      = tip,
                                    fontSize  = 12.sp,
                                    color     = TextSecondary,
                                    lineHeight = 18.sp,
                                    modifier  = Modifier.weight(1f),
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        NeuCard(
                            modifier     = Modifier
                                .fillMaxWidth()
                                .clickable { onOptimize() },
                            cornerRadius = 14.dp,
                            elevation    = 5f,
                        ) {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment     = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector        = Icons.Rounded.AutoFixHigh,
                                    contentDescription = null,
                                    tint               = BrandTeal,
                                    modifier           = Modifier.size(20.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Bersihkan Cache Sekarang",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = BrandTeal,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = TextTertiary)
    }
}

private fun estimateStorageLife(s: StorageInfo): Triple<String, String, Color> {
    // Estimate based on usage percentage
    // eMMC/UFS chips typically last 10+ years under normal use
    return when {
        s.usagePercent < 50 -> Triple(
            "Penyimpanan Anda masih sangat sehat. Chip flash pada perangkat ini (eMMC/UFS) dirancang untuk bertahan lama dan Anda masih sangat jauh dari batas keausan.",
            "Masih sangat aman (5–10 tahun lagi)",
            HealthyGreen,
        )
        s.usagePercent < 75 -> Triple(
            "Penyimpanan dalam kondisi baik. Penggunaan di kisaran ini masih sangat normal untuk perangkat sehari-hari.",
            "Aman digunakan (3–7 tahun lagi)",
            HealthyGreen,
        )
        s.usagePercent < 88 -> Triple(
            "Penyimpanan mulai padat. Kondisi ini bisa sedikit memperlambat kecepatan baca/tulis perangkat. Disarankan untuk mulai membebaskan sebagian ruang.",
            "Perlu perhatian (2–5 tahun lagi)",
            AttentionYellow,
        )
        else -> Triple(
            "Penyimpanan hampir penuh! Ini dapat membuat perangkat melambat dan mengganggu kinerja sistem. Segera bebaskan ruang penyimpanan.",
            "Segera luangkan ruang",
            PoorCoral,
        )
    }
}
