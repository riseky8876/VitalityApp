package com.vitality.app.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitality.app.data.model.AppPowerInfo
import com.vitality.app.data.model.RiskLevel
import com.vitality.app.ui.components.*
import com.vitality.app.ui.theme.*
import com.vitality.app.viewmodel.HealthViewModel

@Composable
fun AppsScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hasPermission by viewModel.hasUsagePermission.collectAsStateWithLifecycle()
    val context = LocalContext.current

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
                Text(
                    "Aktivitas Aplikasi",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                )
            }

            Spacer(Modifier.height(20.dp))

            if (!hasPermission) {
                // Permission required
                NeuCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("🔐", fontSize = 40.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Izin Diperlukan",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Untuk menampilkan aplikasi mana yang paling banyak aktif di latar belakang, Vitality memerlukan akses ke data penggunaan aplikasi.\n\nData ini hanya dibaca oleh Vitality dan tidak dikirim ke mana pun.",
                            fontSize  = 13.sp,
                            color     = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp,
                        )
                        Spacer(Modifier.height(16.dp))
                        NeuButton(
                            onClick = {
                                val intent = android.content.Intent(
                                    android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
                                )
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                Icons.Rounded.PrivacyTip,
                                contentDescription = null,
                                tint = BrandTeal,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Buka Pengaturan",
                                color      = BrandTeal,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 14.sp,
                            )
                        }
                    }
                }
            } else if (uiState.appPowerList.isEmpty()) {
                NeuCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("✅", fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Semua Aman!",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tidak ada aplikasi yang terdeteksi aktif secara berlebihan dalam 24 jam terakhir.",
                            fontSize  = 13.sp,
                            color     = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                        )
                    }
                }
            } else {
                // Summary card
                val highRisk   = uiState.appPowerList.count { it.riskLevel == RiskLevel.HIGH }
                val mediumRisk = uiState.appPowerList.count { it.riskLevel == RiskLevel.MEDIUM }

                NeuCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        AppRiskSummaryItem(
                            count = highRisk,
                            label = "Boros Daya",
                            color = PoorCoral,
                            emoji = "🔴",
                        )
                        AppRiskSummaryItem(
                            count = mediumRisk,
                            label = "Perlu Pantau",
                            color = AttentionYellow,
                            emoji = "🟡",
                        )
                        AppRiskSummaryItem(
                            count = uiState.appPowerList.size - highRisk - mediumRisk,
                            label = "Normal",
                            color = HealthyGreen,
                            emoji = "🟢",
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Explanation
                NeuCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("ℹ️", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Aplikasi di bawah ini diurutkan berdasarkan seberapa banyak mereka aktif dalam 24 jam terakhir. Yang berwarna merah atau kuning patut diperhatikan karena bisa menguras baterai meski tidak sedang digunakan.",
                            fontSize  = 12.sp,
                            color     = TextSecondary,
                            lineHeight = 18.sp,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // App list
                uiState.appPowerList.forEachIndexed { index, app ->
                    AppDetailCard(app = app, rank = index + 1)
                    Spacer(Modifier.height(12.dp))
                }

                // General tip
                NeuCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            "💡 Cara Membatasi Aktivitas Latar Belakang",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                        )
                        Spacer(Modifier.height(8.dp))
                        listOf(
                            "Buka Pengaturan → Aplikasi → pilih aplikasi",
                            "Ketuk 'Penggunaan data & baterai'",
                            "Nonaktifkan 'Izinkan aktivitas latar belakang'",
                        ).forEachIndexed { i, step ->
                            Row(
                                modifier          = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    modifier         = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(BrandTeal.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("${i + 1}", fontSize = 10.sp, color = BrandTeal, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(step, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppRiskSummaryItem(count: Int, label: String, color: Color, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Text("$count", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = TextTertiary)
    }
}

@Composable
private fun AppDetailCard(app: AppPowerInfo, rank: Int) {
    val color = when (app.riskLevel) {
        RiskLevel.HIGH   -> PoorCoral
        RiskLevel.MEDIUM -> AttentionYellow
        RiskLevel.LOW    -> HealthyGreen
    }

    NeuCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header row
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Rank badge
                Box(
                    modifier         = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "#$rank",
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color      = color,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        app.appName,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                    )
                    Text(
                        app.packageName,
                        fontSize = 10.sp,
                        color    = TextTertiary,
                    )
                }
                StatusChip(
                    label = when (app.riskLevel) {
                        RiskLevel.HIGH   -> "Boros Daya"
                        RiskLevel.MEDIUM -> "Perlu Pantau"
                        RiskLevel.LOW    -> "Normal"
                    },
                    color = color,
                )
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = NeuShadowDark.copy(alpha = 0.2f))
            Spacer(Modifier.height(10.dp))

            // Stats
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AppStatMini(
                    "⏱️",
                    "${app.backgroundTimeMinutes} mnt",
                    "Aktif (24j)",
                    color,
                )
                AppStatMini(
                    "🔋",
                    "${String.format("%.1f", app.batteryUsagePercent)}%",
                    "Penggunaan",
                    color,
                )
                AppStatMini(
                    "🔔",
                    "${app.wakeCount}x",
                    "Bangunkan",
                    color,
                )
            }

            Spacer(Modifier.height(10.dp))

            // Description
            Text(
                app.friendlyDescription,
                fontSize  = 12.sp,
                color     = TextSecondary,
                lineHeight = 17.sp,
            )

            if (app.riskLevel != RiskLevel.LOW) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.08f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text("💡", fontSize = 13.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        app.suggestion,
                        fontSize  = 11.sp,
                        color     = color,
                        lineHeight = 16.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppStatMini(emoji: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 14.sp)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = color)
        Text(label, fontSize = 10.sp, color = TextTertiary)
    }
}
