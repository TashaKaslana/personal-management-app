import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.personal_management_app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.personal_management_app"
        val envUrl = providers.fileContents(rootProject.layout.projectDirectory.file(".env")).asText.map { contents ->
            contents.lineSequence().map { it.trim() }
                .firstOrNull { it.startsWith("BACKEND_URL=") }
                ?.substringAfter('=')?.trim()?.removeSurrounding("\"")?.removeSurrounding("'")
                ?: "http://10.0.2.2:8080/"
        }
        val backendUrl = providers.gradleProperty("backendUrl")
            .orElse(envUrl).orElse("http://10.0.2.2:8080/").get().trimEnd('/') + "/"
        val uri = URI(backendUrl)
        require(uri.scheme in listOf("http", "https") && uri.host != null &&
            uri.userInfo == null && uri.query == null && uri.fragment == null) {
            "BACKEND_URL must be an HTTP or HTTPS base URL"
        }
        buildConfigField("String", "BACKEND_URL", "\"$backendUrl\"")
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.retrofit)
    implementation(libs.converter.kotlinx.serialization.v2110)
    implementation(libs.okhttp)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
