package com.vitality.app.data.source

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.util.Log
import com.vitality.app.data.model.AppPowerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppUsageDataSource {

    private const val TAG = "AppUsageDataSource"

    suspend fun getTopPowerApps(context: Context): List<AppPowerInfo> = withContext(Dispatchers.IO) {
        try {
            if (!hasUsageStatsPermission(context)) {
                Log.w(TAG, "No usage stats permission")
                return@withContext emptyList()
            }

            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE)
                    as UsageStatsManager
            val pm = context.packageManager

            val endTime   = System.currentTimeMillis()
            val startTime = endTime - (24 * 60 * 60 * 1000L) // last 24h

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            if (stats.isNullOrEmpty()) {
                Log.w(TAG, "No usage stats returned (empty)")
                return@withContext emptyList()
            }

            Log.d(TAG, "Got ${stats.size} usage stats entries")

            val totalForegroundMs = stats.sumOf { it.totalTimeInForeground }
                .takeIf { it > 0 } ?: 1L

            stats
                .filter { it.totalTimeInForeground > 60_000L }
                .sortedByDescending { it.totalTimeInForeground }
                .take(10)
                .mapNotNull { stat ->
                    try {
                        val appInfo = pm.getApplicationInfo(stat.packageName, 0)
                        val appName = pm.getApplicationLabel(appInfo).toString()
                        val bgTimeMin = stat.totalTimeInForeground / 60_000L
                        val usagePercent = (stat.totalTimeInForeground.toFloat() /
                                totalForegroundMs * 100f).coerceIn(0f, 100f)
                        val wakeCount = estimateWakeCount(stat.packageName, bgTimeMin)

                        AppPowerInfo(
                            packageName           = stat.packageName,
                            appName               = appName,
                            batteryUsagePercent   = usagePercent,
                            isBackgroundActive    = bgTimeMin > 30,
                            backgroundTimeMinutes = bgTimeMin,
                            wakeCount             = wakeCount,
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    } catch (e: Exception) {
                        Log.w(TAG, "Error mapping app ${stat.packageName}", e)
                        null
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app usage", e)
            emptyList()
        }
    }

    private fun estimateWakeCount(packageName: String, bgTimeMin: Long): Int {
        return when {
            packageName.contains("whatsapp", true)  -> (bgTimeMin / 2).toInt().coerceIn(0, 200)
            packageName.contains("tiktok", true)    -> (bgTimeMin / 3).toInt().coerceIn(0, 150)
            packageName.contains("instagram", true) -> (bgTimeMin / 3).toInt().coerceIn(0, 120)
            packageName.contains("facebook", true)  -> (bgTimeMin / 2).toInt().coerceIn(0, 180)
            packageName.contains("gms", true)       -> (bgTimeMin / 4).toInt().coerceIn(0, 100)
            bgTimeMin > 120                         -> (bgTimeMin / 5).toInt().coerceIn(0, 80)
            else                                    -> (bgTimeMin / 10).toInt().coerceIn(0, 30)
        }
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            val granted = mode == AppOpsManager.MODE_ALLOWED
            Log.d(TAG, "Usage stats permission: $granted (mode=$mode)")
            granted
        } catch (e: Exception) {
            Log.e(TAG, "Error checking usage stats permission", e)
            false
        }
    }
}
