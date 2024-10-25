import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.modular.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)

}
val properties = Properties()
val propertiesFile = rootProject.file("local.properties")
if (propertiesFile.canRead()) {
    properties.load(FileInputStream(propertiesFile))
}
android {
    namespace = "com.drp.utils"
    defaultConfig {
        buildConfigField("String", "encryptRSA", properties.getProperty("rsaEncrption"))
    }
}

dependencies {
    implementation(project(mapOf("path" to ":feature:persiancalendar")))
    //Hilt
    implementation(libs.bundles.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
