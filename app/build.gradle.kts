import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.gms)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.sonarqube)
    jacoco
}


android {
    namespace = "com.nfq.nfqsummit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nfq.nfqsummit"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.nfq.nfqsummit.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val p = Properties()
    p.load(project.rootProject.file("local.properties").reader())

    signingConfigs {
        getByName("debug") {
            storeFile = file(p.getProperty("DEBUG_STORE_FILE"))
            storePassword = p.getProperty("DEBUG_STORE_PASSWORD")
            keyAlias = p.getProperty("DEBUG_KEY_ALIAS")
            keyPassword = p.getProperty("DEBUG_KEY_PASSWORD")
        }
        create("release") {
            storeFile = file(p.getProperty("RELEASE_STORE_FILE"))
            storePassword = p.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = p.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = p.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        val supabaseUrl: String = p.getProperty("SUPABASE_URL")
        val supabaseKey: String = p.getProperty("SUPABASE_KEY")
        release {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
            buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
            buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
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
        kotlinCompilerExtensionVersion = "1.5.13"
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
    implementation(libs.navigation)
    implementation(libs.coil)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.google.code.scanner)
    implementation(libs.security.crypto)
    implementation(libs.accompanist.permissions)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.compiler)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.dagger.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.functions)
    implementation(libs.supabase.gotrue)

    implementation(libs.compose.markdown)

    implementation(project(":data"))

    testImplementation(libs.junit) {
        exclude(group = "org.hamcrest")
    }
    testImplementation(libs.mockk) {
        exclude(group = "org.hamcrest")
    }
    testImplementation(libs.hamcrest.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.junit4)

    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.dagger.hilt.android.testing)
    androidTestImplementation(libs.navigation.testing)
    kspAndroidTest(libs.dagger.hilt.android.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
jacoco {
    toolVersion = "0.8.7"
}