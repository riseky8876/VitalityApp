# 🌿 Vitality — Android Health Monitor

<p align="center">
  <img src="app/src/main/res/drawable/ic_vitality_foreground.xml" width="100" />
</p>

<p align="center">
  <b>Analisis kesehatan perangkat Android secara mendalam, disajikan dalam bahasa yang mudah dipahami</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-12%2B-green?logo=android" />
  <img src="https://img.shields.io/badge/Kotlin-2.0-blue?logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-2024-teal" />
  <img src="https://img.shields.io/badge/Root-KernelSU%20%7C%20Magisk-orange" />
  <img src="https://img.shields.io/badge/License-MIT-lightgrey" />
</p>

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|---|---|
| 🌀 **Vitality Ring** | Skor kesehatan 0–100 dengan animasi lingkaran gradient |
| 🔋 **Analisis Baterai** | Kapasitas, siklus pengisian, suhu, degradasi real-data |
| 💾 **Monitor RAM** | Penggunaan memori + zRAM + status lega/padat |
| 📦 **Kesehatan Storage** | Estimasi umur, usage %, narasi edukatif |
| ⚡ **Detektor Aplikasi** | Aplikasi yang boros daya di latar belakang |
| 🧹 **Optimasi Otomatis** | Bersihkan cache + rapikan memori dengan 1 tap |
| 📊 **Riwayat Harian** | Grafik tren skor kesehatan dari waktu ke waktu |

---

## 📱 Kompatibilitas (Diverifikasi)

Aplikasi ini dibangun dan dioptimalkan berdasarkan hasil diagnostic nyata dari:

- **Device**: POCO / Xiaomi (`fleur` — POCO M4 Pro 4G)
- **SoC**: MediaTek Helio G96 (MT6781)
- **Android**: 15 (SDK 35)
- **Root**: KernelSU v1.0.9
- **ROM**: LineagePlus (custom kernel)

### Path yang Digunakan (Verified Readable)
```
/sys/class/power_supply/battery/capacity        ✅
/sys/class/power_supply/battery/charge_full     ✅
/sys/class/power_supply/battery/charge_full_design ✅
/sys/class/power_supply/battery/cycle_count     ✅
/sys/class/power_supply/battery/temp            ✅
/sys/class/power_supply/battery/voltage_now     ✅
/sys/class/power_supply/bms/charge_full         ✅
/sys/class/power_supply/bms/cycle_count         ✅
/proc/meminfo                                   ✅
/proc/diskstats                                 ✅
```

---

## 🏗️ Arsitektur

```
com.vitality.app/
├── data/
│   ├── model/          # Data classes (BatteryInfo, RamInfo, dll)
│   ├── source/         # RootDataSource, AppUsageDataSource
│   ├── repository/     # HealthRepository (MVVM bridge)
│   └── receiver/       # BootReceiver
├── viewmodel/          # HealthViewModel
└── ui/
    ├── theme/          # Neumorphic colors, typography
    ├── components/     # VitalityRing, NeuCard, HealthCards
    └── screens/        # Dashboard, Battery, Storage, Apps, Optimize, History
```

**Design Pattern**: MVVM + Repository Pattern  
**UI Framework**: Jetpack Compose  
**State Management**: StateFlow + collectAsStateWithLifecycle

---

## 🚀 Cara Build

### Prerequisites
- Android Studio Ladybug (2024.2+)
- JDK 11+
- Android SDK 35

### Steps
```bash
# 1. Clone repository
git clone https://github.com/yourusername/vitality-android.git
cd vitality-android

# 2. Buka di Android Studio
# File → Open → pilih folder VitalityApp

# 3. Sync Gradle
# Android Studio akan otomatis sync

# 4. Build & Install
./gradlew installDebug
# atau tekan Run ▶ di Android Studio
```

---

## 🔐 Akses Root

Vitality bekerja **dengan atau tanpa root**:

| Mode | Yang Tersedia |
|---|---|
| **Tanpa Root** | RAM, Storage, battery capacity dari BatteryManager API |
| **Dengan Root (KernelSU/Magisk)** | + cycle count, charge_full, BMS data, cache clearing, RAM trim |

Root access bersifat **read-only** untuk data sensing. Operasi tulis hanya dilakukan saat optimasi (cache clearing yang aman).

---

## 🎨 Design System

**Minimalist Neumorphism**

```kotlin
NeuBackground  = Color(0xFFEEF0F5)  // Base abu-abu hangat
NeuShadowDark  = Color(0xFFCBCDD4)  // Shadow bawah-kanan
NeuShadowLight = Color(0xFFFFFFFF)  // Shadow atas-kiri

// Status colors
HealthyGreen   = Color(0xFF4ECDC4)  // Teal — Sehat
AttentionYellow= Color(0xFFFFB347)  // Peach — Perhatian
PoorCoral      = Color(0xFFFF6B6B)  // Coral — Perlu Tindakan
```

---

## 📋 Izin yang Diperlukan

| Izin | Kegunaan |
|---|---|
| `BATTERY_STATS` | Data baterai detail |
| `PACKAGE_USAGE_STATS` | Melihat aktivitas aplikasi |
| `RECEIVE_BOOT_COMPLETED` | (Future) refresh setelah reboot |
| `FOREGROUND_SERVICE` | (Future) monitoring background |

---

## 🗺️ Roadmap

- [ ] Widget homescreen (skor real-time)
- [ ] Notifikasi harian ringkasan kesehatan
- [ ] Thermal monitoring (per zona)
- [ ] Export laporan PDF
- [ ] Dark mode (Neumorphic dark)
- [ ] Backup & restore riwayat

---

## 📄 Lisensi

```
MIT License

Copyright (c) 2026 Vitality Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software...
```

---

<p align="center">Made with ❤️ for Android users who care about their device health</p>
