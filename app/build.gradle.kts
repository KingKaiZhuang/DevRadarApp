plugins {
    alias(libs.plugins.android.application)
    // --- 關鍵修正：手動統一所有 Kotlin 插件版本，確保與 KSP 完美匹配 ---
    id("org.jetbrains.kotlin.android") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"

    // KSP 必須與 Kotlin 2.0.21 對應 (版本 2.0.21-1.0.26)
    id("com.google.devtools.ksp") version "2.0.21-1.0.26"
}

android {
    namespace = "com.example.devradarapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.devradarapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ----------------------------------------------------------
    // 1. Android Core & Lifecycle
    // ----------------------------------------------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    // ----------------------------------------------------------
    // 2. Jetpack Compose UI
    // ----------------------------------------------------------
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // ----------------------------------------------------------
    // 3. Navigation
    // ----------------------------------------------------------
    implementation(libs.androidx.navigation.compose)

    // ----------------------------------------------------------
    // 4. Data & Storage (Room / SQLite / Serialization)
    // ----------------------------------------------------------
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // 使用 add("ksp", ...) 來避免 IDE 紅字
    add("ksp", "androidx.room:room-compiler:$roomVersion")

    // ----------------------------------------------------------
    // 5. Authentication
    // ----------------------------------------------------------
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation(libs.play.services.basement)
    implementation(libs.play.services.base)
    implementation("androidx.browser:browser:1.8.0")

    // ----------------------------------------------------------
    // 6. Testing
    // ----------------------------------------------------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ----------------------------------------------------------
    // 7. Network (Retrofit)
    // ----------------------------------------------------------
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}