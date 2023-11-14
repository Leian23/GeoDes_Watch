plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.geodes_____watch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.geodes_____watch"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

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

    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.wear:wear:1.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")

    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.10")
    implementation("org.osmdroid:osmdroid-android:6.1.6")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.6")
    implementation("org.osmdroid:osmdroid-wms:6.1.6")
    implementation ("androidx.preference:preference:1.1.1")

    implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("com.google.android.gms:play-services-location:16.0.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
}