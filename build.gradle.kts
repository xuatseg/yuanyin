buildscript {
    val kotlinVersion = project.property("kotlin_version") as String
    val composeVersion = project.property("compose_version") as String
    val composeCompilerVersion = project.property("compose_compiler_version") as String
    val agpVersion = project.property("agp_version") as String
    val kspVersion = project.property("ksp_version") as String
    val hiltVersion = project.property("hilt_version") as String

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
} 