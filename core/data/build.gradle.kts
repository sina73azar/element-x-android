plugins {
    alias(libs.plugins.modular.android.library)
    alias(libs.plugins.modular.android.hilt)
    alias(libs.plugins.modular.android.room)
    alias(libs.plugins.modular.android.network)
    kotlin("plugin.serialization") version "1.9.0"

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.drp.data"

}

dependencies {
    implementation(libs.androidx.security.crypto )
    implementation(project(":core:shared_ui"))
    implementation(project(":core:utils"))
//    testImplementation(libs.test.junit)
//    androidTestImplementation(libs.androidx.test.ext.junit)
//    androidTestImplementation(libs.espresso.core)
}
