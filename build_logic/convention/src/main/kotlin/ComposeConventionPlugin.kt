
import com.android.build.gradle.LibraryExtension
import convention.configureAndroidCompose
import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

                dependencies {
                    "implementation"(libs.findLibrary("androidx.activity.compose").get())
                    "implementation"(platform(libs.findLibrary("androidx.compose.bom").get()))
                    "implementation"(libs.findLibrary("androidx.compose.ui").get())
                    "implementation"(libs.findLibrary("androidx.activity.compose").get())
                    "implementation"(libs.findLibrary("androidx.compose.compose.material3").get())
                    "implementation"(libs.findLibrary("androidx.compose.compose.material").get())
                    "implementation"(libs.findLibrary("androidx.ui.tooling.preview.android").get())
                    "implementation"(libs.findLibrary("androidx.compose.foundation").get())
                    "implementation"(libs.findLibrary("androidx.compose.ui.graphics").get())
                    "implementation"(libs.findLibrary("constraintlayout.compose").get())
                    "implementation"(libs.findLibrary("androidx.lifecycle.runtime.compose").get())
                    "implementation"(libs.findLibrary("lottie.compose").get())
//                }
            }

        }
    }

}