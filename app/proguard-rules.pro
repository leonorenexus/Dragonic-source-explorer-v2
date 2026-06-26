# Dragonic Source Explorer ProGuard rules

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Gson model classes (GitHub API responses)
-keep class com.leonoretech.dragonicexplorer.data.model.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper

# Room
-keep class androidx.room.** { *; }
