import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

class AndroidNetworkConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("converter.moshi").get())
                add("implementation", libs.findLibrary("retrofit").get())
                add("implementation", libs.findLibrary("converter.gson").get())
                add("implementation", libs.findLibrary("okhttp").get())
                add("implementation", libs.findLibrary("logging.interceptor").get())
            }
        }

    }
}