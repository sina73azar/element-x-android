plugins {
    alias(libs.plugins.modular.android.library)
    alias(libs.plugins.modular.android.compose)
    alias(libs.plugins.modular.android.xml.libs)
    kotlin("plugin.serialization") version "1.9.0"

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.drp.shared_ui"

}

dependencies {
    implementation(libs.sdp.android)
    implementation(libs.pretty.persian.numbers)
    implementation(project(":core:utils"))
//    implementation(project(":core:data"))
//    implementation(project(":feature:persiancalendar"))
    implementation ("com.github.M-Erfan-Dm:persian-material-datepicker:1.0.1")
    api ("com.github.samanzamani:PersianDate:1.7.1")
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.ir.debitcard.scanner)
    api(libs.zxing.core)
    api(libs.zxing.embedded)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
