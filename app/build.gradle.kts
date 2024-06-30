import org.apache.tools.ant.property.GetProperty
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.shelfie"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shelfie"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        val readProperties = Properties()
        val localPropertiesFile = rootProject.file("apikeys.properties")

        if (localPropertiesFile.exists()) {
            readProperties.load(FileInputStream(localPropertiesFile))
        }
        
        buildConfigField("String", "API_KEY", readProperties.getProperty("ApiKey", "\"default_value_if_missing\""))

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.material3.android)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    // Compose dependencies
    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.ui:ui-tooling:1.6.7")

    // Lifecycle dependencies
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.1")

    // Compose runtime
    implementation("androidx.compose.runtime:runtime-livedata:1.6.7")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.6.7")

    // Compose tooling preview
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.4.0")

    // Hilt (optional, but recommended for DI)
    implementation("com.google.dagger:hilt-android:2.40.5")

    // Lifecycle ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")

    // Hilt navigation compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation ("io.coil-kt:coil-compose:2.5.0")
    implementation ("androidx.compose.material3:material3-android:1.2.1")
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.35.1-alpha")
    implementation ("com.google.guava:guava:31.0.1-android")

    implementation ("androidx.camera:camera-core:1.1.0-alpha10")
    implementation ("androidx.camera:camera-camera2:1.1.0-alpha10")
    implementation ("androidx.camera:camera-lifecycle:1.1.0-alpha10")
    implementation ("androidx.camera:camera-view:1.0.0-alpha23")
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha23")

}
