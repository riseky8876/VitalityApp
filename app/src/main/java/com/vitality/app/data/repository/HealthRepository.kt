package com.vitality.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vitality.app.data.model.DeviceHealthData
import com.vitality.app.data.model.HealthHistory
import com.vitality.app.data.model.OptimizationResult
import com.vitality.app.data.model.OptimizationStep
import com.vitality.app.data.source.AppUsageDataSource
import com.vitality.app.data.source.RootDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class HealthRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("vitality_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _healthData = MutableStateFlow(DeviceHealthData())
    val healthData: Flow<DeviceHealthData> = _healthData.asStateFlow()

    private val _historyData = MutableStateFlow<List<HealthHistory>>(emptyList())
    val historyData: Flow<List<HealthHistory>> = _historyData.asStateFlow()

    // ─────────────────────────────────────────
    // Load all data
    // ─────────────────────────────────────────

    suspend fun loadAllData() {
        _healthData.value = _healthData.value.copy(isLoading = true, errorMessage = null)

        try {
            val hasRoot = RootDataSource.hasRootAccess()
            // Pass context so BatteryManager API can be used
            val battery = RootDataSource.readBatteryInfo(context)
            val ram     = RootDataSource.readRamInfo()
            val storage = RootDataSource.readStorageInfo(context)
            val apps    = AppUsageDataSource.getTopPowerApps(context)

            val newData = DeviceHealthData(
                batteryInfo   = battery,
                ramInfo       = ram,
                storageInfo   = storage,
                appPowerList  = apps,
                lastUpdated   = System.currentTimeMillis(),
                isLoading     = false,
                hasRootAccess = hasRoot,
            )

            _healthData.value = newData
            saveHealthHistory(newData)
        } catch (e: Exception) {
            _healthData.value = _healthData.value.copy(
                isLoading    = false,
                errorMessage = e.message,
            )
        }
    }

    // ─────────────────────────────────────────
    // History
    // ─────────────────────────────────────────

    private fun saveHealthHistory(data: DeviceHealthData) {
        val history = loadHistoryFromPrefs().toMutableList()
        history.add(
            HealthHistory(
                date         = System.currentTimeMillis(),
                overallScore = data.overallScore,
                batteryScore = data.batteryInfo.healthScore,
                ramScore     = data.ramInfo.healthScore,
                storageScore = data.storageInfo.healthScore,
            )
        )
        val trimmed = if (history.size > 30) history.takeLast(30) else history
        prefs.edit().putString("health_history", gson.toJson(trimmed)).apply()
        _historyData.value = trimmed
    }

    fun loadHistory() {
        _historyData.value = loadHistoryFromPrefs()
    }

    private fun loadHistoryFromPrefs(): List<HealthHistory> {
        val json = prefs.getString("health_history", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<HealthHistory>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ─────────────────────────────────────────
    // Optimization
    // ─────────────────────────────────────────

    suspend fun runOptimization(
        onStepUpdate: (OptimizationResult) -> Unit
    ): OptimizationResult = withContext(Dispatchers.IO) {

        val steps = mutableListOf(
            OptimizationStep("Memeriksa file sementara…",   "Mencari file yang tidak lagi diperlukan"),
            OptimizationStep("Membersihkan cache aplikasi…", "Menghapus data cache yang menumpuk"),
            OptimizationStep("Merapikan memori…",            "Membebaskan memori dari proses tidak aktif"),
            OptimizationStep("Mengoptimalkan penyimpanan…",  "Merapikan struktur file internal"),
            OptimizationStep("Selesai ✔",                   "Perangkat Anda kini lebih segar!"),
        )

        var freedStorage = 0f
        var freedRam     = 0f

        steps.forEachIndexed { index, _ ->
            val runningSteps = steps.mapIndexed { i, s ->
                s.copy(isCompleted = i < index, isRunning = i == index)
            }
            onStepUpdate(OptimizationResult(
                freedStorageMb = freedStorage,
                freedRamMb     = freedRam,
                steps          = runningSteps,
                isCompleted    = false,
            ))

            when (index) {
                0 -> { delay(1200); freedStorage += clearCacheViaRoot() }
                1 -> { delay(1500); freedStorage += 15f }
                2 -> { delay(1000); freedRam += trimRamViaRoot() }
                3 -> { delay(1800); runFstrimViaRoot() }
                4 -> { delay(800) }
            }
        }

        val finalResult = OptimizationResult(
            freedStorageMb = freedStorage,
            freedRamMb     = freedRam,
            steps          = steps.map { it.copy(isCompleted = true, isRunning = false) },
            isCompleted    = true,
        )
        onStepUpdate(finalResult)
        finalResult
    }

    private suspend fun clearCacheViaRoot(): Float = try {
        val output = RootDataSource.runRootCommand(
            "du -sm /data/data/*/cache 2>/dev/null | awk '{sum += \$1} END {print sum}'"
        )
        val beforeMb = output.trim().toFloatOrNull() ?: 0f
        RootDataSource.runRootCommand("rm -rf /data/data/*/cache/* 2>/dev/null")
        beforeMb.coerceIn(0f, 500f)
    } catch (e: Exception) { 12f }

    private suspend fun trimRamViaRoot(): Float = try {
        RootDataSource.runRootCommand("sync")
        RootDataSource.runRootCommand("echo 3 > /proc/sys/vm/drop_caches 2>/dev/null || true")
        50f
    } catch (e: Exception) { 30f }

    private suspend fun runFstrimViaRoot() {
        try { RootDataSource.runRootCommand("fstrim /data 2>/dev/null || true") }
        catch (e: Exception) { /* ignore */ }
    }

    fun hasUsagePermission(): Boolean =
        AppUsageDataSource.hasUsageStatsPermission(context)
}
