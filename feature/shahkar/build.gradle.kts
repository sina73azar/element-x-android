import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.modular.android.library)
    alias(libs.plugins.modular.android.xml.libs)
    alias(libs.plugins.modular.android.hilt)
    alias(libs.plugins.modular.android.network)
    kotlin("plugin.serialization") version "1.9.0"
    alias(libs.plugins.compose.compiler)
}

val properties = Properties()
val propertiesFile = rootProject.file("local.properties")
if (propertiesFile.canRead()) {
    properties.load(FileInputStream(propertiesFile))
}

android {
    namespace = "com.drp.superapp.shahkar"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "certificate", properties.getProperty("certificatepinner"))
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.loading.button.android)
    implementation(libs.constraintlayout)
    implementation(project(mapOf("path" to ":core:shared_ui")))
    implementation(project(mapOf("path" to ":core:data")))
    implementation(project(mapOf("path" to ":core:utils")))
    implementation(libs.kotlinx.serialization)
    implementation(project(":feature:persiancalendar"))
}
