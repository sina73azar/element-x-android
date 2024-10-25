import androidx.room.gradle.RoomExtension
import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
//            pluginManager.apply("androidx.room")
           /* extensions.configure(RoomExtension::class.java) {
                // The schemas directory contains a schema file for each version of the Room database.
                // This is required to enable Room auto migrations.
                // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                schemaDirectory("$projectDir/schemas")
            }*/

            dependencies {
                add("implementation", libs.findBundle("room").get())
                add("implementation", libs.findLibrary("sqlcipher").get())
                add("implementation", libs.findLibrary("sqlite").get())
                add("kapt", libs.findLibrary("room.compiler").get())
            }
        }
    }
}