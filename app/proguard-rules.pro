# EthioShop ProGuard Rules

# Keep model classes
-keep class com.ethio.shop.data.models.** { *; }
-keep class com.ethio.shop.domain.model.** { *; }

# Firebase
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Kotlin
-keep class kotlin.reflect.** { *; }
-keep interface kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    <fields>;
}
-keepclassmembers class kotlinx.coroutines.internal.JobSupport {
    volatile java.lang.Object _state;
}

# Serialization
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.ethio.shop.data.models.**$$serializer { *; }
-keepclassmembers class com.ethio.shop.data.models.**$* { *; }

# Coil
-keep class coil.** { *; }

# Gson (if used)
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Retrofit (if used)
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp (if used)
-dontwarn okhttp3.**
-dontwarn okio.**

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Remove logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}