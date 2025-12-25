plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.dineo"
<<<<<<< HEAD
    compileSdk = 34
=======
    compileSdk = 36   // ✅ Correct syntax
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8

    defaultConfig {
        applicationId = "com.example.dineo"
        minSdk = 24
<<<<<<< HEAD
        targetSdk = 34
=======
        targetSdk = 36
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
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
<<<<<<< HEAD
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
=======
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
    }
}

dependencies {
<<<<<<< HEAD

=======
    // From version catalog
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

<<<<<<< HEAD
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.squareup.picasso:picasso:2.8")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

=======
    // Firebase
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // RecyclerView + CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Testing
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
