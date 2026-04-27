# Vitality App ProGuard Rules

# Keep Gson models
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep our data models
-keep class com.vitality.app.data.model.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }
