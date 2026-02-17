import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    kotlin("native.cocoapods") version "2.3.0"
    `maven-publish`
}

group = "uz.yalla"
version = "1.0.11"

kotlin {
    targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
        namespace = "uz.yalla.maps"
        compileSdk = 36
        minSdk = 26

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "YallaMaps"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.components.resources)

            // Core dependencies
            api(libs.yalla.core)
            implementation(libs.yalla.design)
            implementation(libs.yalla.resources)

            // Lifecycle
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // Geo
            api(libs.geo)
            implementation(libs.geo.compose)

            // MapLibre
            api(libs.maplibre.compose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.google.maps.compose)
            implementation(libs.play.services.maps)
        }
    }

    cocoapods {
        version = "1.0.11"
        summary = "Yalla Maps - Unified KMP map library"
        ios.deploymentTarget = "16.6"
        pod("GoogleMaps")
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            artifactId = artifactId.replace(project.name, "maps")
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/RoyalTaxi/yalla-maps")
            credentials {
                username = findProperty("gpr.user") as String?
                    ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("gpr.key") as String?
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
