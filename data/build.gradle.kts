import java.util.Properties

private val keystorePropertiesFile = rootProject.file("keystore.properties")

private val keystoreProperties = keystorePropertiesFile.inputStream().use { inputStream ->
    Properties().apply {
        load(inputStream)
    }
}

private val apiKey = keystoreProperties.getProperty("NEWS_API_KEY")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "NEWS_API_KEY", "\"${apiKey}\"")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))

    // Room
    implementation(libs.androidx.room.runtime) // ORM для локальной SQLite базы данных
    ksp(libs.androidx.room.compiler) // Кодогенератор для Room
    implementation(libs.androidx.room.ktx) // Kotlin-расширения для Room (поддержка корутин и Flow)

    // Ktor
    implementation(platform(libs.ktor.bom)) // BOM для согласования версий Ktor
    implementation(libs.ktor.client.core) // Ядро HTTP-клиента Ktor
    implementation(libs.ktor.client.cio) // CIO движок для Ktor (асинхронный, на корутинах)
    implementation(libs.ktor.client.logging) // Логирование HTTP-запросов и ответов
    implementation(libs.ktor.client.content.negotiation) // Сериализация / десериализация (JSON и др.)
    implementation(libs.ktor.serialization.kotlinx.json) // Сериализация JSON через kotlinx.serialization для Ktor

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json) // Сериализация/десериализация JSON

    // Hilt
    implementation(libs.hilt.android) // DI-фреймворк для Android
    ksp(libs.hilt.android.compiler) // Кодогенератор для Hilt

    implementation(libs.androidx.core.ktx)  // Kotlin-расширения для Android Core API

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}