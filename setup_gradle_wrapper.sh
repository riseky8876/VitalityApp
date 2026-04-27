#!/bin/bash
# ============================================================
# setup_gradle_wrapper.sh
# Jalankan SEKALI setelah extract project untuk generate
# gradle-wrapper.jar yang diperlukan untuk build
# ============================================================

echo "🔧 Mengunduh Gradle Wrapper..."

# Cek apakah Gradle sudah terinstall
if command -v gradle &> /dev/null; then
    echo "✅ Gradle ditemukan: $(gradle --version | head -1)"
    gradle wrapper --gradle-version 8.9
    echo "✅ Gradle wrapper berhasil dibuat!"
else
    echo "⚠️  Gradle belum terinstall."
    echo ""
    echo "Pilihan:"
    echo ""
    echo "1. Install via SDKMAN (recommended):"
    echo "   curl -s 'https://get.sdkman.io' | bash"
    echo "   source ~/.sdkman/bin/sdkman-init.sh"
    echo "   sdk install gradle 8.9"
    echo "   gradle wrapper --gradle-version 8.9"
    echo ""
    echo "2. Download manual gradle-wrapper.jar:"
    echo "   URL: https://github.com/gradle/gradle/raw/v8.9.0/gradle/wrapper/gradle-wrapper.jar"
    echo "   Simpan ke: gradle/wrapper/gradle-wrapper.jar"
    echo ""
    echo "3. Buka di Android Studio — Android Studio akan otomatis"
    echo "   menyediakan gradle wrapper saat pertama sync."
fi
