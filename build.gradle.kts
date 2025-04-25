buildscript {
    repositories {
        google()
        mavenCentral()
    }
//    dependencies {
//        classpath("com.android.tools.build:gradle:8.10.2")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
//    }
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}

plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.android.library)
//    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose) apply false
//    alias(libs.plugins.hilt)
//    plugins {
//        id("com.android.application") version "8.2.0" apply false
//        id("com.android.library") version "8.8.1" apply false
//        id("org.jetbrains.kotlin.android") version "1.9.22" apply false
//    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("com.google.dagger.hilt.android") version "2.55" apply false
//    }

}
