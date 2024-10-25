
import com.android.build.api.dsl.ApplicationExtension
import convention.configureAndroidCompose
import convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin:Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            with(pluginManager){
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.devtools.ksp")
                apply("kotlin-kapt")

            }
            extensions.configure (ApplicationExtension::class.java){
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                compileSdk = 34
                buildFeatures {
                    viewBinding = true
                    buildConfig = true
                }
                defaultConfig{
                    applicationId = "com.drp.refahland"
                    targetSdk=34
                    minSdk=21
                    versionCode=39
                    versionName="3.8.0"
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }
        }
    }
}