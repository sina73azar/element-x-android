import com.android.build.gradle.LibraryExtension
import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class XmlLibsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure(LibraryExtension::class.java) {
                dependencies {
                    "implementation"(libs.findLibrary("constraintlayout").get())
                    "implementation"(libs.findLibrary("androidx.fragment").get())
                    "implementation"(libs.findLibrary("android.design").get())
                    "implementation"(libs.findLibrary("swiperefreshlayout").get())
                    "implementation"(libs.findLibrary("recyclerview").get())
                    "implementation"(libs.findLibrary("android.arch.lifecycle").get())
                    "implementation"(libs.findLibrary("pinview").get())
                    "implementation"(libs.findLibrary("indicatorseekbar").get())
                    "implementation"(libs.findLibrary("skeleton").get())
                    "implementation"(libs.findLibrary("shimmerlayout").get())
                    "implementation"(libs.findLibrary("loading.button.android").get())
                    "implementation"(libs.findLibrary("android.segmented").get())
                    "implementation"(libs.findLibrary("android.spinkit").get())
                    "implementation"(libs.findLibrary("sdp.android").get())
                    "implementation"(libs.findLibrary("target.tooltip").get())
                    "implementation"(libs.findLibrary("sample.tooltip").get())
                    "implementation"(libs.findLibrary("pretty.persian.numbers").get())
                    "implementation"(libs.findLibrary("lottie.android").get())
                    "implementation"(libs.findLibrary("styleabletoast").get())
                    "implementation"(libs.findLibrary("truetime.android").get())
                }
            }
        }
    }
}