import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.sonarqube)
    jacoco
}

val p = Properties()
p.load(project.rootProject.file("local.properties").reader())

android {
    namespace = "com.nfq.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "PASS_PHRASE", "\"${p.getProperty("PASS_PHRASE")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField("String", "BASIC_TOKEN", "\"${p.getProperty("BASIC_TOKEN")}\"")
            buildConfigField("String", "BASE_URL", "\"${p.getProperty("BASE_URL")}\"")
        }
        debug {
            isMinifyEnabled = false
            /*buildConfigField("String", "BASIC_TOKEN", "\"${p.getProperty("DEV_BASIC_TOKEN")}\"")
            buildConfigField("String", "BASE_URL", "\"${p.getProperty("DEV_BASE_URL")}\"")*/
            buildConfigField("String", "BASIC_TOKEN", "\"${p.getProperty("BASIC_TOKEN")}\"")
            buildConfigField("String", "BASE_URL", "\"${p.getProperty("BASE_URL")}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        buildConfig = true
    }
}

// JaCoCo configuration for code coverage
jacoco {
    toolVersion = "0.8.11"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/di/*.*",
        "**/models/*.*"
    )

    val debugTree = fileTree("${buildDir}/intermediates/javac/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.buildDir) {
        include("jacoco/testDebugUnitTest.exec", "outputs/code-coverage/connected/*coverage.ec")
    })
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.ktor.android)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.functions)
    implementation(libs.supabase.gotrue)
    implementation(libs.security.crypto)
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.compiler)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.dagger.hilt.android.compiler)

    implementation(libs.kotlinx.serialization)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.arrow)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.sqlcipher)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    implementation(libs.androidx.dataStore.core)
    implementation(libs.androidx.dataStore.pref)

    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}

sqldelight {
    databases {
        create("SummitDatabase") {
            packageName.set("com.nfq.data.cache")
        }
    }
}