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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitality.app.data.model.BatteryInfo
import com.vitality.app.ui.components.*
import com.vitality.app.ui.theme.*
import com.vitality.app.viewmodel.HealthViewModel
import java.util.Locale

@Composable
fun BatteryDetailScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val battery = uiState.batteryInfo

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
                    Icon(Icons.Rounded.ArrowBackIosNew, "Kembali", tint = TextPrimary)
                }
                Spacer(Modifier.width(8.dp))
                Text("Detail Baterai", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(Modifier.height(20.dp))

            // Big health ring for battery
            NeuCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Kondisi Baterai",
                        fontSize   = 13.sp,
                        color      = TextSecondary,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(12.dp))
                    VitalityRing(
                        score  = battery.healthScore,
                        status = com.vitality.app.data.model.HealthStatus.HEALTHY.let {
                            when {
                                battery.healthScore >= 75 -> com.vitality.app.data.model.HealthStatus.HEALTHY
                                battery.healthScore >= 50 -> com.vitality.app.data.model.HealthStatus.ATTENTION
                                else                      -> com.vitality.app.data.model.HealthStatus.POOR
                            }
                        },
                        size   = 180.dp,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text      = "\"${buildDetailedNarrative(battery)}\"",
                        fontSize  = 13.sp,
                        color     = TextSecondary,
                        lineHeight = 19.sp,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Capacity comparison
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionTitle("⚡ Kapasitas Baterai")
                    Spacer(Modifier.height(12.dp))

                    DetailRow(
                        label = "Kapasitas saat baru",
                        value = "${battery.chargeFullDesign / 1000} mAh",
                        icon  = "🆕",
                    )
                    Spacer(Modifier.height(8.dp))
                    DetailRow(
                        label = "Kapasitas sekarang",
                        value = "${battery.chargeFull / 1000} mAh",
                        icon  = "🔋",
                        highlight = true,
                        highlightColor = scoreColor(battery.healthScore),
                    )
                    Spacer(Modifier.height(8.dp))
                    DetailRow(
                        label = "Penurunan kapasitas",
                        value = "${String.format(Locale.US, "%.1f", 100f - battery.healthPercent)}%",
                        icon  = "📉",
                    )

                    Spacer(Modifier.height(14.dp))

                    // Visual capacity bar
                    Column {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Kondisi baterai vs baru", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                "${String.format(Locale.US, "%.1f", battery.healthPercent)}%",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = scoreColor(battery.healthScore),
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        NeuProgressBar(
                            progress = battery.healthPercent / 100f,
                            color    = scoreColor(battery.healthScore),
                            height   = 12.dp,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text     = if (battery.healthPercent >= 80)
                                "✅ Kondisi baterai masih sangat baik"
                            else if (battery.healthPercent >= 70)
                                "⚠️ Kapasitas mulai berkurang, wajar untuk usia pemakaian"
                            else
                                "⚠️ Kapasitas cukup berkurang, pertimbangkan pemeriksaan",
                            fontSize = 11.sp,
                            color    = TextTertiary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Cycle count detail
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionTitle("🔄 Siklus Pengisian")
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AttentionYellow.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text       = "${battery.cycleCount}",
                                    fontSize   = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = AttentionYellow,
                                )
                                Text("siklus", fontSize = 9.sp, color = TextTertiary)
                            }
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text       = cycleLabel(battery.cycleCount),
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = TextPrimary,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text      = battery.cycleDescription,
                                fontSize  = 12.sp,
                                color     = TextSecondary,
                                lineHeight = 17.sp,
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Cycle progress bar (out of 800 typical lifespan)
                    val typicalMax = 800
                    val cyclePct = (battery.cycleCount.toFloat() / typicalMax).coerceIn(0f, 1f)
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Dari estimasi ${typicalMax} siklus normal", fontSize = 11.sp, color = TextTertiary)
                        Text(
                            "${battery.cycleCount}/${typicalMax}",
                            fontSize = 11.sp,
                            color    = if (cyclePct < 0.6f) HealthyGreen else if (cyclePct < 0.85f) AttentionYellow else PoorCoral,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    NeuProgressBar(
                        progress = cyclePct,
                        color    = if (cyclePct < 0.6f) HealthyGreen else if (cyclePct < 0.85f) AttentionYellow else PoorCoral,
                        height   = 10.dp,
                    )

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text      = "⏳ Estimasi sisa umur: ${battery.estimatedLifeRemaining}",
                        fontSize  = 12.sp,
                        color     = TextSecondary,
                        lineHeight = 17.sp,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Real-time stats
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionTitle("📊 Data Real-Time")
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        RealtimeStatBox(
                            modifier = Modifier.weight(1f),
                            emoji    = "🌡️",
                            value    = "${String.format(Locale.US, "%.1f", battery.temperature)}°C",
                            label    = "Suhu",
                            color    = if (battery.temperature < 40f) HealthyGreen
                                       else if (battery.temperature < 48f) AttentionYellow
                                       else PoorCoral,
                            note     = if (battery.temperature < 40f) "Normal"
                                       else if (battery.temperature < 48f) "Agak hangat"
                                       else "Terlalu panas",
                        )
                        RealtimeStatBox(
                            modifier = Modifier.weight(1f),
                            emoji    = "⚡",
                            value    = "${String.format(Locale.US, "%.0f", battery.voltageNow)} mV",
                            label    = "Tegangan",
                            color    = HealthyGreen,
                            note     = "Normal",
                        )
                        RealtimeStatBox(
                            modifier = Modifier.weight(1f),
                            emoji    = "🔌",
                            value    = "${String.format(Locale.US, "%.0f", battery.currentNow)} mA",
                            label    = "Arus",
                            color    = BrandBlue,
                            note     = if (battery.isCharging) "Sedang mengisi" else "Sedang pakai",
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tips card
            ChargingTipsCard()
        }
    }
}

@Composable
private fun ChargingTipsCard() {
    NeuCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionTitle("💡 Tips Menjaga Baterai")
            Spacer(Modifier.height(10.dp))

            val tips = listOf(
                "Hindari mengisi baterai hingga 100% setiap saat — idealnya antara 20%–80%",
                "Hindari membiarkan baterai kosong total (di bawah 5%) terlalu sering",
                "Jangan biarkan HP dalam suhu panas saat mengisi (misal: di bawah bantal)",
                "Gunakan charger original atau yang bersertifikat untuk menjaga kesehatan baterai",
                "Hindari pengisian semalam suntuk secara rutin jika tidak diperlukan",
            )

            tips.forEach { tip ->
                Row(
                    modifier          = Modifier.padding(vertical = 5.dp),
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
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text       = text,
        fontSize   = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color      = TextPrimary,
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: String,
    highlight: Boolean = false,
    highlightColor: Color = TextPrimary,
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, color = TextSecondary)
        }
        Text(
            text       = value,
            fontSize   = 14.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color      = if (highlight) highlightColor else TextPrimary,
        )
    }
}

@Composable
private fun RealtimeStatBox(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    label: String,
    color: Color,
    note: String,
) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = TextTertiary)
        Spacer(Modifier.height(3.dp))
        Text(note, fontSize = 10.sp, color = color.copy(alpha = 0.8f))
    }
}

private fun buildDetailedNarrative(b: BatteryInfo): String {
    val pct = String.format(Locale.US, "%.1f", b.healthPercent)
    return "Baterai Anda saat ini berada di $pct% dari kondisi baru dengan ${b.cycleCount} siklus pengisian. ${b.cycleDescription}"
}

private fun cycleLabel(cycles: Int): String = when {
    cycles < 300  -> "Baterai Masih Segar 🌟"
    cycles < 600  -> "Pemakaian Normal 👍"
    cycles < 900  -> "Sudah Banyak Digunakan"
    cycles < 1200 -> "Pemakaian Intensif"
    else          -> "Perlu Dipantau"
}
