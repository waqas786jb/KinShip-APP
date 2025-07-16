import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //id ("kotlin-kapt")
    //id("com.google.dagger.hilt.android")
    kotlin("kapt")
   // id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
android {
    namespace = "com.kinship.mobile.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kinship.mobile.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        setProperty("archivesBaseName", "kinship_" + formatDateWithOrdinal(Date()))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            buildConfigField("boolean", "EnableAnim", "true")
            buildConfigField("String", "BASE_URL", "\"https://demo.iroidsolutions.com:8001/api/v1/\"") //please put your respective base url
            //buildConfigField("String", "BASE_URL", "\"https://api.findkinship.com/api/v1/\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
            buildConfigField("boolean", "EnableAnim", "true")
           // buildConfigField("String", "BASE_URL", "\"https://api.findkinship.com/api/v1/\"")
            buildConfigField("String", "BASE_URL", "\"https://demo.iroidsolutions.com:8001/api/v1/\"") //please put your respective base url
            buildConfigField("String", "MAP_API_KEY", "\"AIzaSyAWcqR_P9DXtv8_eD3Itki1e_PoGrp1fYs\"")
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
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
fun formatDateWithOrdinal(date: Date): String {
    val daySuffix = getDaySuffix(date)
    val formattedDate = SimpleDateFormat("d'$daySuffix'MMMMyyyy").apply {
        timeZone = TimeZone.getDefault()
    }.format(date)
    return formattedDate
}

fun getDaySuffix(date: Date): String {
    val day = SimpleDateFormat("d").apply {
        timeZone = TimeZone.getDefault()
    }.format(date)

    val dayInt = day.toInt()
    var suffix = "th"

    if (dayInt >= 11 && dayInt <= 13) return suffix

    val suffixMap = arrayOf(null, "st", "nd", "rd") +
            (4..20).map { "th" } + arrayOf("st", "nd", "rd") +
            (24..30).map { "th" } + arrayOf("st")

    suffix = suffixMap[dayInt % 100].toString()
    return suffix
}

dependencies {
    implementation(libs.core.ktx.v1150)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.androidx.datastore.core)

    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.places)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.ui.test.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.firebase.messaging.ktx)

    //firebase crashlytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    //Android
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.datastore.preferences)

    // Inject - Dagger hilt
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.hilt.android)

    //compose
    implementation(libs.androidx.material3)
    implementation(libs.compose.material3.windowsize)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    // Android Architecture Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.lifecycle.runtime.ktx)

    // Logging
    implementation(libs.kermit)

    //country code
    implementation(libs.komposecountrycodepicker)

    //coroutine
    implementation(libs.kotlinx.coroutines.android)

    // Toast
    implementation(libs.toasty)

    //
    implementation(libs.compose)
    implementation(libs.coil)
    implementation(libs.coil.kt.coil.compose)

    //  Socket
    implementation(libs.socket.io.client)

    //paging 3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Icons
    implementation(libs.androidx.material.icons.extended)
    // image load
    implementation(libs.coil.kt.coil.compose)
    // google places api
    implementation(libs.places)

    // compressImage
    implementation(libs.compressor)

    implementation(libs.jsoup)
    // status bar colors changes
    implementation(libs.accompanist.systemuicontroller)

    // pager
    implementation(libs.accompanist.pager)

    implementation (libs.accompanist.swiperefresh)

}

