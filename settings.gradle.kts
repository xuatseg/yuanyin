pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LooiRobot"

// 简化模块声明
include(":app")
include(":domain")
include(":data")
include(":presentation")
include(":core:robot-control")
include(":ui")

// 设置项目目录
project(":presentation").projectDir = file("presentation")
project(":domain").projectDir = file("domain")
project(":data").projectDir = file("data")
project(":core:robot-control").projectDir = file("core/robot-control")
project(":ui").projectDir = file("ui")
project(":app").projectDir = file("app")

gradle.beforeProject {
    val properties = gradle.startParameter.projectProperties
    extra["kotlin_version"] = properties["kotlin_version"] ?: "1.9.10"
    extra["compose_version"] = properties["compose_version"] ?: "1.5.3"
    extra["compose_compiler_version"] = properties["compose_compiler_version"] ?: "1.5.3"
    extra["agp_version"] = properties["agp_version"] ?: "8.3.0"
    extra["ksp_version"] = properties["ksp_version"] ?: "1.9.10-1.0.13"
    extra["hilt_version"] = properties["hilt_version"] ?: "2.50"
} 