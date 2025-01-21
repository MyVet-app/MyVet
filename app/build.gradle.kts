plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
}
//dependencies {
//    // JUnit לטסטים יחידתיים
//    testImplementation("junit:junit:4.13.2")
//
//    // Mockito למוקים
//    testImplementation("org.mockito:mockito-core:4.2.0")
//
//    // Mockito-Kotlin (אם את משתמשת בקוטלין)
//    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
//
//    // Espresso לטסטים כלליים
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
//
//    //UI Tests
//    androidTestImplementation("androidx.test.ext:junit:1.1.3")
//
//    // Firebase
//    implementation("com.google.firebase:firebase-analytics-ktx:20.0.2")
//
//    // Firestore
//    implementation("com.google.firebase:firebase-firestore-ktx:24.0.0")
//
//
//}
dependencies {
    // JUnit לטסטים יחידתיים
    testImplementation("junit:junit:4.13.2")

    // Mockito למוקים
    testImplementation("org.mockito:mockito-core:4.2.0")

    // Mockito-Kotlin (אם את משתמשת בקוטלין)
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    // Espresso לטסטים כלליים
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // UI Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0") // לבדוק Intents
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.4.0") // לניהול משאבים
    androidTestImplementation("androidx.test:rules:1.4.1-alpha06") // חוקים לבדיקות Espresso
    androidTestImplementation("androidx.test:runner:1.4.0") // ראנר לבדיקות UI

    // Firebase
    implementation("com.google.firebase:firebase-analytics-ktx:20.0.2")

    // Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:24.0.0")
}



android {
    namespace = "com.myvet.myvet"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.myvet.myvet"
        minSdk = 35
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.ui.auth)
    implementation(libs.facebook.login)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation (libs.firebase.firestore)
    implementation (libs.geofire.android.common)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation (libs.okhttp)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

}