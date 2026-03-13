# Gson configuration
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# OkHttp configuration
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Retrofit configuration
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Kotlin configuration
-keep class kotlin.** { *; }
-keep interface kotlin.** { *; }
-dontwarn kotlin.**

# Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# App-specific configuration
-keep class com.leetcode.tracker.api.** { *; }
-keep class com.leetcode.tracker.data.** { *; }
-keep class com.leetcode.tracker.ui.** { *; }
-keep class com.leetcode.tracker.widget.** { *; }
-keep class com.leetcode.tracker.notifications.** { *; }
-keep class com.leetcode.tracker.MainActivity { *; }

# Keep enum values and valueOf methods
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
