plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.example.presentation"
    compileSdk = 37

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":domain"))

    // Hilt
    implementation(libs.hilt.android) // DI-фреймворк для Android
    ksp(libs.hilt.android.compiler) // Кодогенератор для Hilt

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)  // Kotlin-расширения для жизненного цикла (lifecycleScope)
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // Kotlin-расширения для ViewModel (viewModelScope)
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel для Compose (viewModel())
    implementation(libs.androidx.lifecycle.runtime.compose)

    // UI
    implementation(libs.androidx.activity.compose)  // Интеграция Compose с Activity (setContent {})
    implementation(platform(libs.androidx.compose.bom))  // BOM для согласования версий всех Compose-библиотек
    implementation(libs.androidx.compose.ui)  // Базовые UI-компоненты Compose
    implementation(libs.androidx.compose.ui.graphics)  // Графические примитивы Compose
    implementation(libs.androidx.compose.ui.tooling.preview)  // Предпросмотр @Composable в Android Studio
    debugImplementation(libs.androidx.compose.ui.tooling)  // Рендерер превью (ComposeViewAdapter)
    implementation(libs.androidx.compose.material3)  // Компоненты Material Design 3
    implementation(libs.androidx.compose.material.icons.extended) // Расширенный набор иконок Material

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Hilt Navigation Compose
    implementation(libs.androidx.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Splash Screen API
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}