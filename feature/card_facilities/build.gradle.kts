plugins {
    alias(libs.plugins.modular.android.library)
    alias(libs.plugins.modular.android.compose)
    alias(libs.plugins.modular.android.hilt)
    alias(libs.plugins.modular.android.room)
    alias(libs.plugins.modular.android.xml.libs)
    kotlin("plugin.serialization") version "1.9.0"

}

android {
    namespace = "com.drp.card_facilities"
}

dependencies {
    implementation(project(mapOf("path" to ":core:shared_ui")))
    implementation(project(mapOf("path" to ":core:data")))
    implementation(project(mapOf("path" to ":core:utils")))
    api("org.iban4j:iban4j:3.2.1")
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.camera.view)
    implementation(libs.barcode.scanning)
    implementation(libs.text.recognition)
    implementation(libs.gson)
    implementation(libs.ir.debitcard.scanner)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}