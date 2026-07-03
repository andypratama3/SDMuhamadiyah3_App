# Keep Kotlin serialization and Ktor models
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.sdm3.parent.**$$serializer { *; }
-keepclassmembers class com.sdm3.parent.** {
    *** Companion;
}
-keepclasseswithmembers class com.sdm3.parent.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Koin
-keep class org.koin.** { *; }
-keep class com.sdm3.parent.** { *; }

# KVault
-keep class com.liftric.kvault.** { *; }

# SQLDelight
-keep class com.sdm3.parent.cache.** { *; }

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Biometric
-keep class androidx.biometric.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
