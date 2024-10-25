
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import convention.configureAndroidCompose
import convention.configureKotlinAndroid
import convention.disableUnnecessaryAndroidTests
import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("kotlin-parcelize")
                apply("com.google.devtools.ksp")
                apply("kotlin-kapt")
            }
            extensions.configure(LibraryExtension::class.java) {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                compileSdk = 34
                defaultConfig {
                    minSdk = 21
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                buildFeatures {
                    viewBinding = true
                    buildConfig = true
                }
                buildTypes {
                    debug {
                        isMinifyEnabled = false

                    }
                    release {
                        isMinifyEnabled = true
                        isZipAlignEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
                dependencies {
                    "implementation"(libs.findLibrary("appcompat").get())
                    "implementation"(libs.findLibrary("androidx.core.ktx").get())
                    "implementation"(libs.findLibrary("material").get())
                    "implementation"(libs.findLibrary("kotlinx.serialization").get())
                    "implementation"(libs.findLibrary("kotlin.stdlib").get())
                }

            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                disableUnnecessaryAndroidTests(target)
            }
        }
    }

}