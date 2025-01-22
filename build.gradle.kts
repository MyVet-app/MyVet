// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) version "1.9.25" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

}
//// build.gradle.kts (Project level)
//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//    }
//    dependencies {
//        classpath("com.android.tools.build:gradle:8.1.0")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
//    }
//}
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
//    // UI Tests
//    androidTestImplementation("androidx.test.ext:junit:1.1.3")
//    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0") // לבדוק Intents
//    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")
//    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.4.0") // לניהול משאבים
//    androidTestImplementation("androidx.test:rules:1.4.1-alpha06") // חוקים לבדיקות Espresso
//    androidTestImplementation("androidx.test:runner:1.4.0") // ראנר לבדיקות UI
//
//    // Firebase
//    implementation("com.google.firebase:firebase-analytics-ktx:20.0.2")
//
//    // Firestore
//    implementation("com.google.firebase:firebase-firestore-ktx:24.0.0")
//}