// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }
}
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.0" apply false // Versi bisa disesuaikan
    id("com.google.gms.google-services") version "4.4.2" apply false

}
