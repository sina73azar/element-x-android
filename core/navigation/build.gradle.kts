plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.jvm) apply false
}

android {
    namespace = "com.drp.navigation"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
//    kotlinOptions {
//        jvmTarget = JavaVersion.VERSION_17.toString()
//    }
}

dependencies {

    implementation(libs.androidx.corektx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
