# 🚀 Panduan Upload & Build di GitHub

## 📋 Langkah-langkah Lengkap

---

## STEP 1 — Siapkan Project di Komputer

### 1a. Extract & Setup Gradle Wrapper
Setelah extract `VitalityApp.zip`:

```bash
cd VitalityApp

# Jika sudah ada Gradle di komputer:
gradle wrapper --gradle-version 8.9

# Atau buka langsung di Android Studio → dia akan setup otomatis
```

### 1b. Verifikasi struktur (opsional)
```
VitalityApp/
├── .github/
│   └── workflows/
│       └── build.yml       ← GitHub Actions config
├── app/
│   ├── build.gradle.kts
│   └── src/
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar     ← dibuat saat setup
│       └── gradle-wrapper.properties
├── gradlew                 ← untuk Linux/Mac
├── gradlew.bat             ← untuk Windows
├── build.gradle.kts
└── settings.gradle.kts
```

---

## STEP 2 — Buat Repository GitHub

1. Buka **https://github.com/new**
2. Isi:
   - **Repository name**: `vitality-android`
   - **Visibility**: Public (untuk open source) atau Private
   - ❌ Jangan centang "Add README" (project sudah punya)
3. Klik **Create repository**

---

## STEP 3 — Upload ke GitHub

### Cara A: Via Git (Terminal)
```bash
cd VitalityApp

# Inisialisasi git
git init
git add .
git commit -m "🌿 Initial commit: Vitality Android App"

# Hubungkan ke GitHub (ganti USERNAME dengan username GitHub Anda)
git remote add origin https://github.com/USERNAME/vitality-android.git
git branch -M main
git push -u origin main
```

### Cara B: Via GitHub Desktop (lebih mudah)
1. Download **GitHub Desktop**: https://desktop.github.com
2. File → Add Local Repository → pilih folder `VitalityApp`
3. Publish repository → pilih nama & visibility

### Cara C: Drag & Drop di GitHub Web
1. Buka repo yang sudah dibuat
2. Klik **"uploading an existing file"**
3. Drag & drop semua file/folder
4. Commit changes

---

## STEP 4 — GitHub Actions Build Otomatis

Setelah push, GitHub Actions langsung jalan otomatis!

### Cek progress build:
1. Buka repository di GitHub
2. Klik tab **Actions** (di atas)
3. Lihat workflow **"🏗️ Build Vitality APK"** berjalan
4. Tunggu ~5-8 menit

### Download APK hasil build:
1. Klik workflow yang sudah selesai ✅
2. Scroll ke bawah → bagian **Artifacts**
3. Download **`vitality-debug-apk`**
4. Extract ZIP → install APK-nya ke HP!

---

## STEP 5 — Buat Release (Opsional)

Untuk buat versi resmi yang bisa didownload semua orang:

```bash
# Buat tag versi baru
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions akan otomatis:
- Build release APK
- Buat GitHub Release page
- Upload APK ke Release

---

## 🔐 Setup Signing (Untuk Release APK yang Signed)

Opsional — untuk APK yang bisa diinstall tanpa warning:

### Buat Keystore:
```bash
keytool -genkey -v \
  -keystore vitality-release.keystore \
  -alias vitality \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### Tambahkan ke GitHub Secrets:
1. Buka repo → Settings → Secrets → Actions → **New repository secret**
2. Tambahkan 4 secrets:

| Secret Name | Value |
|---|---|
| `KEYSTORE_BASE64` | `base64 vitality-release.keystore` |
| `KEY_ALIAS` | `vitality` |
| `KEY_PASSWORD` | password key Anda |
| `STORE_PASSWORD` | password keystore Anda |

---

## 🔄 Workflow Build Otomatis

| Event | Yang Terjadi |
|---|---|
| Push ke `main` | Build Debug + Release APK |
| Push ke branch lain | Build Debug APK saja |
| Push tag `v*` | Build + Create GitHub Release |
| Manual trigger | Pilih debug atau release |

---

## ❓ FAQ

**Q: Build gagal dengan error Gradle?**
```
A: Pastikan gradle-wrapper.jar ada di gradle/wrapper/
   Buka di Android Studio, lalu sync, lalu push lagi.
```

**Q: "Keystore not found" error?**
```
A: Normal jika belum setup signing. APK unsigned tetap bisa diinstall
   untuk development/personal use.
```

**Q: Berapa lama build selesai?**
```
A: Sekitar 5-10 menit untuk pertama kali (Gradle download dependencies).
   Build berikutnya lebih cepat karena cache.
```

**Q: APK bisa langsung install ke HP?**
```
A: Ya! Download artifact dari Actions, extract ZIP, install .apk nya.
   Pastikan "Install dari sumber tidak dikenal" sudah diaktifkan.
```
