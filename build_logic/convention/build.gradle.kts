plugins {
    `kotlin-dsl`
}
group = "com.drp.buildlogic"


dependencies{
    compileOnly(libs.androidx.room.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}
gradlePlugin{
    plugins{
        register("androidApplication"){
            id="modular.android.application"
            implementationClass="AndroidApplicationConventionPlugin"
        }
        register("androidHilt"){
            id="modular.android.hilt"
            implementationClass="AndroidHiltConventionPlugin"
        }
        register("androidLibrary"){
            id="modular.android.library"
            implementationClass="AndroidLibraryConventionPlugin"
        }
        register("androidRoom"){
            id="modular.android.room"
            implementationClass="AndroidRoomConventionPlugin"
        }
        register("androidNetwork"){
            id="modular.android.network"
            implementationClass="AndroidNetworkConventionPlugin"
        }
        register("androidCompose"){
            id="modular.android.compose"
            implementationClass="ComposeConventionPlugin"
        }
        register("androidXmlLibs"){
            id="modular.android.xml.libs"
            implementationClass="XmlLibsConventionPlugin"
        }
    }
}
