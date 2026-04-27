package com.vitality.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitality.app.data.model.*
import com.vitality.app.ui.components.*
import com.vitality.app.ui.theme.*
import com.vitality.app.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onNavigateToBattery: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToApps: () -> Unit,
    onNavigateToOptimize: () -> Unit,
    onNavigateToHistory: () -> Unit,
) {
    val uiState        by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing   by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Re-check permissions when returning from Settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh    = { viewModel.refresh() },
        state        = pullToRefreshState,
        modifier     = Modifier
            .fillMaxSize()
            .background(NeuBackground),
    ) {
        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(
                top    = 24.dp,
                start  = 20.dp,
                end    = 20.dp,
                bottom = 100.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Header
            item {
                DashboardHeader(
                    lastUpdated  = uiState.lastUpdated,
                    hasRoot      = uiState.hasRootAccess,
                    isRefreshing = isRefreshing,
                    onRefresh    = { viewModel.refresh() },
                )
            }

            if (uiState.isLoading) {
                item { LoadingCard() }
            } else {
                // ── Vitality Ring
                item {
                    VitalityRingSection(
                        data                = uiState,
                        onNavigateToHistory = onNavigateToHistory,
                    )
                }

                // ── Overall message
                item {
                    NeuCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💬", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text       = uiState.overallMessage,
                                fontSize   = 13.sp,
                                color      = TextSecondary,
                                lineHeight = 19.sp,
                                modifier   = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // ── Quick stats row
                item {
                    QuickStatsRow(
                        data           = uiState,
                        onBatteryClick = onNavigateToBattery,
                        onRamClick     = {},
                        onStorageClick = onNavigateToStorage,
                    )
                }

                // ── Battery card
                item {
                    BatteryCard(
                        battery  = uiState.batteryInfo,
                        modifier = Modifier.clickable { onNavigateToBattery() },
                    )
                }

                // ── RAM card
                item { RamCard(ram = uiState.ramInfo) }

                // ── Storage card
                item {
                    StorageCard(
                        storage  = uiState.storageInfo,
                        modifier = Modifier.clickable { onNavigateToStorage() },
                    )
                }

                // ── App power card (or permission request)
                item {
                    if (uiState.appPowerList.isNotEmpty()) {
                        AppPowerCard(
                            apps     = uiState.appPowerList,
                            modifier = Modifier.clickable { onNavigateToApps() },
                        )
                    } else {
                        AppPermissionCard(
                            onGrantClick = {
                                val intent = android.content.Intent(
                                    android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                }

                // ── Optimize button
                item { OptimizeButton(onClick = onNavigateToOptimize) }

                // ── Pull to refresh hint (shown once)
                item {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.SwipeDown,
                            contentDescription = null,
                            tint               = TextTertiary,
                            modifier           = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text     = "Tarik ke bawah untuk memperbarui",
                            fontSize = 11.sp,
                            color    = TextTertiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    lastUpdated: Long,
    hasRoot: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    val timeStr = remember(lastUpdated) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(lastUpdated))
    }

    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text       = "Vitality",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text     = "Diperbarui $timeStr",
                    fontSize = 12.sp,
                    color    = TextTertiary,
                )
                if (hasRoot) {
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusChip(label = "Root ✓", color = HealthyGreen)
                }
            }
        }

        IconButton(onClick = onRefresh) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(24.dp),
                    color       = BrandTeal,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector        = Icons.Rounded.Refresh,
                    contentDescription = "Perbarui",
                    tint               = TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun VitalityRingSection(
    data: DeviceHealthData,
    onNavigateToHistory: () -> Unit,
) {
    NeuCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
        Column(
            modifier            = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = "Skor Kesehatan Perangkat",
                fontSize   = 13.sp,
                color      = TextSecondary,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(16.dp))

            VitalityRing(
                score  = data.overallScore,
                status = data.overallStatus,
                size   = 200.dp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ScoreIndicator(score = data.batteryInfo.healthScore, label = "Baterai")
                ScoreIndicator(score = data.ramInfo.healthScore,     label = "Memori")
                ScoreIndicator(score = data.storageInfo.healthScore, label = "Simpanan")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onNavigateToHistory) {
                Icon(
                    imageVector        = Icons.Rounded.Timeline,
                    contentDescription = null,
                    modifier           = Modifier.size(16.dp),
                    tint               = BrandTeal,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Lihat riwayat", fontSize = 12.sp, color = BrandTeal)
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    data: DeviceHealthData,
    onBatteryClick: () -> Unit,
    onRamClick: () -> Unit,
    onStorageClick: () -> Unit,
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuickStatCard(
            modifier = Modifier.weight(1f),
            icon     = "🔋",
            value    = "${data.batteryInfo.capacityPercent}%",
            label    = "Baterai",
            color    = scoreColor(data.batteryInfo.healthScore),
            onClick  = onBatteryClick,
        )
        QuickStatCard(
            modifier = Modifier.weight(1f),
            icon     = "💾",
            value    = "${data.ramInfo.usagePercent.toInt()}%",
            label    = "RAM",
            color    = scoreColor(data.ramInfo.healthScore),
            onClick  = onRamClick,
        )
        QuickStatCard(
            modifier = Modifier.weight(1f),
            icon     = "📦",
            value    = "${data.storageInfo.usagePercent.toInt()}%",
            label    = "Simpan",
            color    = scoreColor(data.storageInfo.healthScore),
            onClick  = onStorageClick,
        )
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    NeuCard(
        modifier     = modifier.clickable { onClick() },
        cornerRadius = 16.dp,
        elevation    = 6f,
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text       = value,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = color,
                textAlign  = TextAlign.Center,
            )
            Text(
                text      = label,
                fontSize  = 11.sp,
                color     = TextTertiary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AppPermissionCard(onGrantClick: () -> Unit) {
    NeuCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector        = Icons.Rounded.PrivacyTip,
                contentDescription = null,
                tint               = BrandTeal,
                modifier           = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = "Izin Diperlukan",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text      = "Untuk melihat aplikasi mana yang paling banyak menggunakan daya, izinkan akses ke data penggunaan aplikasi.",
                fontSize  = 12.sp,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            NeuButton(onClick = onGrantClick) {
                Text(
                    text       = "Berikan Izin",
                    color      = BrandTeal,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun OptimizeButton(onClick: () -> Unit) {
    NeuCard(
        modifier     = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 20.dp,
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Rounded.AutoFixHigh,
                    contentDescription = null,
                    tint               = BrandTeal,
                    modifier           = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text       = "Optimalkan Sekarang",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary,
                )
                Text(
                    text     = "Bersihkan & segarkan perangkat",
                    fontSize = 12.sp,
                    color    = TextSecondary,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector        = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint               = TextTertiary,
            )
        }
    }
}

@Composable
private fun LoadingCard() {
    NeuCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier            = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                color       = BrandTeal,
                strokeWidth = 3.dp,
                modifier    = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text      = "Sedang menganalisis perangkat Anda…",
                fontSize  = 13.sp,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
