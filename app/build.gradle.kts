plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.shopping.nhom5"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.shopping.nhom5"
        minSdk = 33
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.preference:preference:1.2.0")
    val nav_version = "2.7.5"

    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation("com.squareup.picasso:picasso:2.8")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Java language implementation
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")
    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    // FirebaseUI for Firebase Realtime Database
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("com.google.firebase:firebase-auth")

    implementation("com.google.firebase:firebase-storage")

    val lifecycle_version = "2.6.2"

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Facebook Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")
}