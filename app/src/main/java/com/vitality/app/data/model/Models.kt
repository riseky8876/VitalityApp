package com.vitality.app.data.model

// ─────────────────────────────────────────────
// Battery Models
// ─────────────────────────────────────────────

data class BatteryInfo(
    val capacityPercent: Int = 0,
    val chargeFull: Long = 0L,          // µAh
    val chargeFullDesign: Long = 0L,    // µAh
    val cycleCount: Int = 0,
    val healthStatus: String = "Good",
    val temperature: Float = 0f,        // °C
    val voltageNow: Float = 0f,         // mV
    val currentNow: Float = 0f,         // mA
    val technology: String = "Li-poly",
    val isCharging: Boolean = false,
    val healthPercent: Float = 0f,      // calculated
) {
    val healthScore: Int
        get() {
            // Health % contributes 50%, cycle count contributes 50%
            val healthFactor = (healthPercent / 100f) * 50f
            val cycleFactor = when {
                cycleCount < 200  -> 50f
                cycleCount < 400  -> 45f
                cycleCount < 600  -> 38f
                cycleCount < 800  -> 30f
                cycleCount < 1000 -> 22f
                cycleCount < 1200 -> 15f
                cycleCount < 1500 -> 10f
                else              -> 5f
            }
            return (healthFactor + cycleFactor).toInt().coerceIn(0, 100)
        }

    val friendlyHealth: String
        get() = when {
            healthPercent >= 90 -> "Sangat Baik"
            healthPercent >= 80 -> "Baik"
            healthPercent >= 70 -> "Cukup Baik"
            healthPercent >= 60 -> "Mulai Menurun"
            else                -> "Perlu Diperhatikan"
        }

    val cycleDescription: String
        get() = when {
            cycleCount < 300  -> "Baterai masih sangat segar, baru digunakan sedikit."
            cycleCount < 600  -> "Penggunaan normal, baterai dalam kondisi sehat."
            cycleCount < 900  -> "Sudah cukup banyak digunakan, wajar jika sedikit menurun."
            cycleCount < 1200 -> "Baterai sudah banyak diisi ulang — kapasitas mungkin berkurang."
            else              -> "Baterai telah melewati banyak siklus pengisian. Pertimbangkan untuk memeriksa kondisi baterai."
        }

    val estimatedLifeRemaining: String
        get() = when {
            cycleCount < 300  -> "Masih sangat panjang (3–5 tahun lagi)"
            cycleCount < 600  -> "Sekitar 2–4 tahun lagi"
            cycleCount < 900  -> "Sekitar 1–3 tahun lagi"
            cycleCount < 1200 -> "Sekitar 1–2 tahun lagi"
            else              -> "Kurang dari 1 tahun, perlu dipantau"
        }
}

// ─────────────────────────────────────────────
// RAM Models
// ─────────────────────────────────────────────

data class RamInfo(
    val totalKb: Long = 0L,
    val availableKb: Long = 0L,
    val usedKb: Long = 0L,
    val cachedKb: Long = 0L,
    val swapTotalKb: Long = 0L,
    val swapFreeKb: Long = 0L,
) {
    val usagePercent: Float
        get() = if (totalKb > 0) ((usedKb.toFloat() / totalKb.toFloat()) * 100f) else 0f

    val totalMb: Float get() = totalKb / 1024f
    val availableMb: Float get() = availableKb / 1024f
    val usedMb: Float get() = usedKb / 1024f

    val healthScore: Int
        get() = when {
            usagePercent < 50 -> 100
            usagePercent < 65 -> 85
            usagePercent < 75 -> 70
            usagePercent < 85 -> 50
            usagePercent < 92 -> 30
            else              -> 15
        }

    val friendlyStatus: String
        get() = when {
            usagePercent < 60 -> "Lega & Nyaman"
            usagePercent < 75 -> "Cukup Tersedia"
            usagePercent < 85 -> "Mulai Padat"
            else              -> "Sangat Padat"
        }

    val description: String
        get() = when {
            usagePercent < 60 -> "Memori perangkat Anda masih banyak tersedia. Aplikasi dapat berjalan dengan lancar."
            usagePercent < 75 -> "Penggunaan memori normal. Perangkat bekerja dengan baik."
            usagePercent < 85 -> "Memori mulai ramai. Perangkat mungkin sedikit melambat saat buka banyak aplikasi."
            else              -> "Memori sangat padat. Pertimbangkan menutup beberapa aplikasi yang tidak digunakan."
        }
}

// ─────────────────────────────────────────────
// Storage Models
// ─────────────────────────────────────────────

data class StorageInfo(
    val totalBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val freeBytes: Long = 0L,
    val internalTotalBytes: Long = 0L,
    val internalFreeBytes: Long = 0L,
) {
    val usagePercent: Float
        get() = if (totalBytes > 0) ((usedBytes.toFloat() / totalBytes.toFloat()) * 100f) else 0f

    val totalGb: Float get() = totalBytes / (1024f * 1024f * 1024f)
    val usedGb: Float get() = usedBytes / (1024f * 1024f * 1024f)
    val freeGb: Float get() = freeBytes / (1024f * 1024f * 1024f)

    val healthScore: Int
        get() = when {
            usagePercent < 50 -> 100
            usagePercent < 65 -> 90
            usagePercent < 75 -> 75
            usagePercent < 85 -> 55
            usagePercent < 92 -> 35
            else              -> 20
        }

    val friendlyStatus: String
        get() = when {
            usagePercent < 50 -> "Sangat Lega"
            usagePercent < 70 -> "Cukup Lega"
            usagePercent < 85 -> "Mulai Penuh"
            else              -> "Hampir Penuh"
        }

    val description: String
        get() = when {
            usagePercent < 50 -> "Penyimpanan Anda masih sangat lega. Tidak ada yang perlu dikhawatirkan."
            usagePercent < 70 -> "Ruang penyimpanan cukup tersedia untuk kebutuhan sehari-hari."
            usagePercent < 85 -> "Penyimpanan mulai padat. Pertimbangkan menghapus file yang tidak diperlukan."
            else              -> "Penyimpanan hampir penuh! Segera luangkan ruang agar perangkat tetap bekerja optimal."
        }

    val storageHealthNarrative: String
        get() = when {
            usagePercent < 50 -> "Penyimpanan Anda masih sangat sehat dan lega."
            usagePercent < 70 -> "Kondisi penyimpanan baik, masih ada ruang yang cukup."
            usagePercent < 85 -> "Penyimpanan mulai terisi, namun masih berfungsi normal."
            else              -> "Penyimpanan hampir habis — ini bisa memperlambat perangkat Anda."
        }
}

// ─────────────────────────────────────────────
// App Power Usage Models
// ─────────────────────────────────────────────

data class AppPowerInfo(
    val packageName: String,
    val appName: String,
    val batteryUsagePercent: Float,
    val isBackgroundActive: Boolean,
    val backgroundTimeMinutes: Long,
    val wakeCount: Int,
) {
    val riskLevel: RiskLevel
        get() = when {
            batteryUsagePercent > 10 || wakeCount > 50 -> RiskLevel.HIGH
            batteryUsagePercent > 5 || wakeCount > 20  -> RiskLevel.MEDIUM
            else                                         -> RiskLevel.LOW
        }

    val friendlyDescription: String
        get() = when (riskLevel) {
            RiskLevel.HIGH   -> "Aplikasi ini sangat aktif di latar belakang dan berpotensi menguras baterai lebih cepat."
            RiskLevel.MEDIUM -> "Aplikasi ini cukup sering aktif meskipun tidak sedang digunakan."
            RiskLevel.LOW    -> "Aktivitas normal — tidak perlu khawatir."
        }

    val suggestion: String
        get() = when (riskLevel) {
            RiskLevel.HIGH   -> "Pertimbangkan membatasi aktivitas latar belakang aplikasi ini di Pengaturan."
            RiskLevel.MEDIUM -> "Anda dapat membatasi aktivitas latar belakang jika baterai terasa cepat habis."
            RiskLevel.LOW    -> "Tidak ada tindakan yang diperlukan."
        }
}

enum class RiskLevel { LOW, MEDIUM, HIGH }

// ─────────────────────────────────────────────
// Overall Health Models
// ─────────────────────────────────────────────

data class DeviceHealthData(
    val batteryInfo: BatteryInfo = BatteryInfo(),
    val ramInfo: RamInfo = RamInfo(),
    val storageInfo: StorageInfo = StorageInfo(),
    val appPowerList: List<AppPowerInfo> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true,
    val hasRootAccess: Boolean = false,
    val errorMessage: String? = null,
) {
    val overallScore: Int
        get() {
            val battScore = batteryInfo.healthScore
            val ramScore  = ramInfo.healthScore
            val storScore = storageInfo.healthScore
            return ((battScore * 0.4f) + (ramScore * 0.3f) + (storScore * 0.3f)).toInt()
        }

    val overallStatus: HealthStatus
        get() = when {
            overallScore >= 75 -> HealthStatus.HEALTHY
            overallScore >= 50 -> HealthStatus.ATTENTION
            else               -> HealthStatus.POOR
        }

    val overallMessage: String
        get() = when (overallStatus) {
            HealthStatus.HEALTHY   -> "Perangkat Anda dalam kondisi prima! Terus jaga kebiasaan penggunaan yang baik."
            HealthStatus.ATTENTION -> "Beberapa aspek perangkat perlu sedikit perhatian. Lihat saran di bawah ini."
            HealthStatus.POOR      -> "Perangkat Anda membutuhkan perhatian segera agar tetap bekerja optimal."
        }
}

enum class HealthStatus { HEALTHY, ATTENTION, POOR }

// ─────────────────────────────────────────────
// History Models (for daily score tracking)
// ─────────────────────────────────────────────

data class HealthHistory(
    val date: Long = System.currentTimeMillis(),
    val overallScore: Int = 0,
    val batteryScore: Int = 0,
    val ramScore: Int = 0,
    val storageScore: Int = 0,
)

// ─────────────────────────────────────────────
// Optimization Result
// ─────────────────────────────────────────────

data class OptimizationStep(
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isRunning: Boolean = false,
)

data class OptimizationResult(
    val freedStorageMb: Float = 0f,
    val freedRamMb: Float = 0f,
    val steps: List<OptimizationStep> = emptyList(),
    val isCompleted: Boolean = false,
)
