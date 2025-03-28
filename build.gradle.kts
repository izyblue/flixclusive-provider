import com.android.build.gradle.BaseExtension
import com.flixclusive.gradle.FlixclusiveProviderExtension
import com.flixclusive.gradle.getFlixclusive


buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
        mavenLocal() // <- For testing
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        // Flixclusive gradle plugin which makes everything work and builds providers
        classpath("com.github.flixclusiveorg.core-gradle:core-gradle:1.2.5")
        // Kotlin support. Remove if you want to use Java
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

fun Project.flxProvider(configuration: FlixclusiveProviderExtension.() -> Unit) = extensions.getFlixclusive().configuration()

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()

subprojects {
    apply(plugin = "flx-provider")
    apply(plugin = "kotlin-android") // Remove if using Java

    // Fill out with your info
    flxProvider {
        /**
         *
         * Add the author(s) of this repository.
         *
         * Optionally, you can add your
         * own github profile link
         * */
        author(
            name = "izyblue",
            image = "https://avatars.githubusercontent.com/u/110403491?v=4",
            socialLink = "https://github.com/izyblue",
        )
        // author( ... )

        setRepository("https://github.com/izyblue/flixclusive-provider/providers")
    }

    android {
        namespace = "com.github.izyblue.flixclusive-provider.${name.replaceFirstChar { it.lowercase() }}"
    }

    dependencies {
        val implementation by configurations
        val fatImplementation by configurations // <- use when you have non-supported libraries.
        val testImplementation by configurations
        val coreLibraryDesugaring by configurations

        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

        val coreStubsModule = "com.github.flixclusiveorg.core-stubs:provider"
        val coreStubsVersion = "1.2.5"

        // Stubs for all Flixclusive classes
        implementation("$coreStubsModule:$coreStubsVersion")

        // ============= START: FOR TESTING ===============
        testImplementation("$coreStubsModule:$coreStubsVersion")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
        testImplementation("junit:junit:4.13.2")
        testImplementation("io.mockk:mockk:1.13.8")
        // ============== END: FOR TESTING ================
    }
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
