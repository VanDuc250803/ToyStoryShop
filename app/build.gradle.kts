plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.datn_toystoryshop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.datn_toystoryshop"
        minSdk = 27
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
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)
    implementation(libs.gridlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:21.0.3")
    implementation ("com.google.android.gms:play-services-auth:20.3.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.7.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("io.realm:realm-android-library:10.7.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.paypal.sdk:paypal-android-sdk:2.16.0")
    implementation ("org.mindrot:jbcrypt:0.4")
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
    implementation ("org.java-websocket:Java-WebSocket:1.5.3")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")


}