package com.vitality.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitality.app.data.model.*
import com.vitality.app.ui.theme.*
import java.util.Locale

// ─────────────────────────────────────────────────────────────
// BATTERY CARD
// ─────────────────────────────────────────────────────────────

@Composable
fun BatteryCard(
    battery: BatteryInfo,
    modifier: Modifier = Modifier,
) {
    val statusColor = scoreColor(battery.healthScore)

    NeuCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            CardHeader(
                icon      = Icons.Rounded.BatteryChargingFull,
                iconColor = statusColor,
                title     = "Kesehatan Baterai",
                statusLabel = battery.friendlyHealth,
                statusColor = statusColor,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Health percentage bar
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    text       = "Kondisi baterai",
                    fontSize   = 13.sp,
                    color      = TextSecondary,
                )
                Text(
                    text       = "${String.format(Locale.US, "%.1f", battery.healthPercent)}%",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = statusColor,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            NeuProgressBar(
                progress      = battery.healthPercent / 100f,
                color         = statusColor,
                height        = 10.dp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BatteryStatItem(
                    modifier = Modifier.weight(1f),
                    label    = "Isi Sekarang",
                    value    = "${battery.capacityPercent}%",
                    icon     = Icons.Rounded.Battery4Bar,
                    color    = statusColor,
                )
                BatteryStatItem(
                    modifier = Modifier.weight(1f),
                    label    = "Siklus Isi",
                    value    = "${battery.cycleCount}x",
                    icon     = Icons.Rounded.Refresh,
                    color    = AttentionYellow,
                )
                BatteryStatItem(
                    modifier = Modifier.weight(1f),
                    label    = "Suhu",
                    value    = "${String.format(Locale.US, "%.1f", battery.temperature)}°C",
                    icon     = Icons.Rounded.Thermostat,
                    color    = if (battery.temperature < 45f) HealthyGreen else PoorCoral,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Narrative
            InsightBox(
                icon    = Icons.Rounded.Info,
                message = buildBatteryNarrative(battery),
                color   = statusColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cycle description
            Text(
                text      = battery.cycleDescription,
                fontSize  = 12.sp,
                color     = TextSecondary,
                lineHeight = 18.sp,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text      = "⏳ ${battery.estimatedLifeRemaining}",
                fontSize  = 12.sp,
                color     = TextTertiary,
                lineHeight = 16.sp,
            )
        }
    }
}

private fun buildBatteryNarrative(b: BatteryInfo): String {
    val healthStr = String.format(Locale.US, "%.1f", b.healthPercent)
    return "Baterai Anda saat ini berada di $healthStr% dari kondisi saat baru. " +
        when {
            b.healthPercent >= 90 -> "Kondisi sangat bagus! Terus jaga kebiasaan pengisian yang baik."
            b.healthPercent >= 80 -> "Sedikit berkurang dari kondisi awal, namun masih sangat normal."
            b.healthPercent >= 70 -> "Kapasitas mulai berkurang karena usia dan pemakaian — ini wajar."
            else                  -> "Kapasitas sudah cukup berkurang. Pertimbangkan untuk memeriksa baterai."
        }
}

@Composable
private fun BatteryStatItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint        = color,
            modifier    = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text       = value,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            color      = color,
        )
        Text(
            text      = label,
            fontSize  = 10.sp,
            color     = TextTertiary,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// RAM CARD
// ─────────────────────────────────────────────────────────────

@Composable
fun RamCard(
    ram: RamInfo,
    modifier: Modifier = Modifier,
) {
    val statusColor = scoreColor(ram.healthScore)

    NeuCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardHeader(
                icon        = Icons.Rounded.Memory,
                iconColor   = statusColor,
                title       = "Memori (RAM)",
                statusLabel = ram.friendlyStatus,
                statusColor = statusColor,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Terpakai", fontSize = 13.sp, color = TextSecondary)
                Text(
                    "${String.format(Locale.US, "%.0f", ram.usagePercent)}%",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = statusColor,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            NeuProgressBar(progress = ram.usagePercent / 100f, color = statusColor, height = 10.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MemStatText(
                    label = "Digunakan",
                    value = "${String.format(Locale.US, "%.0f", ram.usedMb)} MB",
                )
                MemStatText(
                    label = "Tersedia",
                    value = "${String.format(Locale.US, "%.0f", ram.availableMb)} MB",
                )
                MemStatText(
                    label = "Total",
                    value = "${String.format(Locale.US, "%.0f", ram.totalMb / 1024f)} GB",
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            InsightBox(
                icon    = Icons.Rounded.Info,
                message = ram.description,
                color   = statusColor,
            )

            // zRAM info
            if (ram.swapTotalKb > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val swapUsed = ram.swapTotalKb - ram.swapFreeKb
                val swapPct  = if (ram.swapTotalKb > 0) swapUsed.toFloat() / ram.swapTotalKb * 100f else 0f
                Text(
                    text      = "💡 Memori virtual aktif (${String.format(Locale.US, "%.0f", swapPct)}% terpakai) — membantu saat memori utama padat.",
                    fontSize  = 11.sp,
                    color     = TextTertiary,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun MemStatText(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Text(label, fontSize = 11.sp, color = TextTertiary)
    }
}

// ─────────────────────────────────────────────────────────────
// STORAGE CARD
// ─────────────────────────────────────────────────────────────

@Composable
fun StorageCard(
    storage: StorageInfo,
    modifier: Modifier = Modifier,
) {
    val statusColor = scoreColor(storage.healthScore)

    NeuCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardHeader(
                icon        = Icons.Rounded.Storage,
                iconColor   = statusColor,
                title       = "Penyimpanan",
                statusLabel = storage.friendlyStatus,
                statusColor = statusColor,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Terpakai", fontSize = 13.sp, color = TextSecondary)
                Text(
                    "${String.format(Locale.US, "%.1f", storage.usagePercent)}%",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = statusColor,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            NeuProgressBar(progress = storage.usagePercent / 100f, color = statusColor, height = 10.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MemStatText(
                    label = "Digunakan",
                    value = "${String.format(Locale.US, "%.1f", storage.usedGb)} GB",
                )
                MemStatText(
                    label = "Kosong",
                    value = "${String.format(Locale.US, "%.1f", storage.freeGb)} GB",
                )
                MemStatText(
                    label = "Total",
                    value = "${String.format(Locale.US, "%.0f", storage.totalGb)} GB",
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            InsightBox(
                icon    = Icons.Rounded.Info,
                message = storage.storageHealthNarrative,
                color   = statusColor,
            )

            if (storage.usagePercent > 80) {
                Spacer(modifier = Modifier.height(8.dp))
                InsightBox(
                    icon    = Icons.Rounded.Warning,
                    message = "💡 Saran: Hapus foto duplikat, video lama, atau pindahkan ke cloud untuk membebaskan ruang.",
                    color   = AttentionYellow,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// APP POWER CARD
// ─────────────────────────────────────────────────────────────

@Composable
fun AppPowerCard(
    apps: List<AppPowerInfo>,
    modifier: Modifier = Modifier,
) {
    NeuCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardHeader(
                icon        = Icons.Rounded.ElectricBolt,
                iconColor   = AttentionYellow,
                title       = "Aplikasi Aktif di Latar Belakang",
                statusLabel = if (apps.isEmpty()) "Bersih" else "${apps.size} Aktif",
                statusColor = if (apps.isEmpty()) HealthyGreen else AttentionYellow,
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (apps.isEmpty()) {
                Text(
                    text     = "✅ Tidak ada aplikasi yang terdeteksi aktif secara berlebihan di latar belakang.",
                    fontSize = 13.sp,
                    color    = TextSecondary,
                    lineHeight = 18.sp,
                )
            } else {
                apps.take(5).forEach { app ->
                    AppPowerItem(app = app)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun AppPowerItem(app: AppPowerInfo) {
    val color = when (app.riskLevel) {
        RiskLevel.HIGH   -> PoorCoral
        RiskLevel.MEDIUM -> AttentionYellow
        RiskLevel.LOW    -> HealthyGreen
    }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.07f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon circle
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = when (app.riskLevel) {
                    RiskLevel.HIGH   -> Icons.Rounded.Warning
                    RiskLevel.MEDIUM -> Icons.Rounded.Info
                    RiskLevel.LOW    -> Icons.Rounded.CheckCircle
                },
                contentDescription = null,
                tint               = color,
                modifier           = Modifier.size(18.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = app.appName,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
            )
            Text(
                text      = app.friendlyDescription,
                fontSize  = 11.sp,
                color     = TextSecondary,
                lineHeight = 15.sp,
            )
            if (app.riskLevel != RiskLevel.LOW) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text      = "💡 ${app.suggestion}",
                    fontSize  = 10.sp,
                    color     = color,
                    lineHeight = 14.sp,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        StatusChip(
            label = when (app.riskLevel) {
                RiskLevel.HIGH   -> "Tinggi"
                RiskLevel.MEDIUM -> "Sedang"
                RiskLevel.LOW    -> "Normal"
            },
            color = color,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// SHARED COMPONENTS
// ─────────────────────────────────────────────────────────────

@Composable
private fun CardHeader(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    statusLabel: String,
    statusColor: Color,
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = iconColor,
                    modifier           = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text       = title,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
            )
        }
        StatusChip(label = statusLabel, color = statusColor)
    }
}

@Composable
private fun InsightBox(
    icon: ImageVector,
    message: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = color,
            modifier           = Modifier.size(16.dp).padding(top = 1.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text       = message,
            fontSize   = 12.sp,
            color      = TextSecondary,
            lineHeight = 18.sp,
            modifier   = Modifier.weight(1f),
        )
    }
}
