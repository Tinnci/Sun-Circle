plugins {
    id("com.android.application")
    
}

android {
    namespace = "com.dudu.watchface.suncircle"
    compileSdk = 33
    
    defaultConfig {
        applicationId = "com.dudu.watchface.suncircle"
        minSdk = 21
        targetSdk = 33
        versionCode = 2
        versionName = "1.1"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    signingConfigs {
        
        create("lunch-community") {
            keyAlias = "Lunch Community"
            keyPassword = "lunch-is-the-best"
            storeFile = file("../lunch-community.keystore")
            storePassword = "lunch-is-the-best"
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("lunch-community")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("lunch-community")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = false
        
    }
    
}

dependencies {
    implementation("com.google.android.support:wearable:2.9.0")
    compileOnly("com.google.android.wearable:wearable:2.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation(files("./libs/watchface-dev-utils.aar"))
    implementation("com.blankj:utilcodex:1.31.1")
}

// 已集成 AutoSwitchWatchFaceService 用于日夜自动切换表盘
