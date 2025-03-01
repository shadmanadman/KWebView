import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    id ("signing")
    id("com.vanniktech.maven.publish") version "0.30.0"
    id("com.google.osdetector") version "1.7.3"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "org.adman.kmp.webview")
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
            val fxSuffix = when (osdetector.classifier) {
                "linux-x86_64" -> "linux"
                "linux-aarch_64" -> "linux-aarch64"
                "windows-x86_64" -> "win"
                "osx-x86_64" -> "mac"
                "osx-aarch_64" -> "mac-aarch64"
                else -> throw IllegalStateException("Unknown OS: ${osdetector.classifier}")
            }
            implementation("org.openjfx:javafx-base:19:${fxSuffix}")
            implementation("org.openjfx:javafx-graphics:19:${fxSuffix}")
            implementation("org.openjfx:javafx-controls:19:${fxSuffix}")
            implementation("org.openjfx:javafx-swing:19:${fxSuffix}")
            implementation("org.openjfx:javafx-web:19:${fxSuffix}")
            implementation("org.openjfx:javafx-media:19:${fxSuffix}")
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

}


compose.desktop {
    application {
        mainClass = "org.adman.kmp.webview.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.shadmanadman"
            packageVersion = libs.versions.libVersion.get()
        }
    }
}


android {
    namespace = "io.github.shadmanadman"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        lint.targetSdk = libs.versions.android.targetSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

mavenPublishing {
    coordinates(
        groupId = libs.versions.groupId.get(),
        artifactId = libs.versions.artifactId.get(),
        version = libs.versions.libVersion.get()
    )

    pom {
        name = "KMP WebView"
        description = "A simple kmp webview to show HTML content or URL"
        url = "https://github.com/shadmanadman/Kmp-WebView"
        licenses {
            license {
                name = "Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "shadmanadman"
                name = "Shadman Adman"
                email = "adman.shadman@gmail.com"
            }
        }
        scm {
            connection = "scm:git:https://github.com/shadmanadman/Kmp-WebView"
            developerConnection = "scm:git:git@github.com:shadmanadman/Kmp-WebView.git"
            url = "https://github.com/shadmanadman/Kmp-WebView"
        }
    }


    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}


val keystorePropertiesFile = rootProject.file("local.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

signing {
    useInMemoryPgpKeys(
        keystoreProperties["signing.keyId"].toString(),
        File(keystoreProperties["signing.secretKeyFile"].toString()).readText(),
        keystoreProperties["signing.password"].toString()
    )
}


