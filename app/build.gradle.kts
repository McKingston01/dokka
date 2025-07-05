import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.huawei.agconnect")
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) // Add the Google services Gradle plugin
    id("org.jetbrains.dokka") version "2.0.0"
}

android {
    namespace = "com.example.emmaintegrationtest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.emmaintegrationtest"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Modify to 'isMinifyEnabled=true', that Enables code shrinking, obfuscation, and
            // optimization for only your project's release build type. Make sure to use a build
            // variant with `isDebuggable=false`.
            isMinifyEnabled = true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            // isShrinkResources = true

            proguardFiles(
                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                getDefaultProguardFile("proguard-android-optimize.txt"),

                // Includes a local, custom Proguard rules file
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Implementaciones para SDK EMMA y terceros

    implementation(libs.emmasdk)                // Implementaciones de integración SDK EMMA
    implementation(libs.ads.identifier)         // Implementación para Huawei
    implementation(libs.firebase.messaging)     // Firebase
    implementation(platform(libs.firebase.bom)) // Import the Firebase BoM (control versión biblioteca)

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.analytics)
    implementation(libs.inapp.plugin.prism)

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    dokkaPlugin(libs.android.documentation.plugin)
    dokkaPlugin(libs.html.mermaid.dokka.plugin)
}

dokka {
    moduleName.set("EMMA Integration Test")

    dokkaPublications {
        html {
            suppressInheritedMembers.set(true)
            failOnWarning.set(true)
            outputDirectory.set(rootDir.resolve("docs/dokka"))
        }
    }

    dokkaSourceSets.configureEach {
        includes.from("Module.md")
        sourceLink {
            localDirectory.set(file("src/main/java/"))
            remoteUrl.set(uri("https://github.com/mckingston01/test-main/blob/main/src/main/java"))
            remoteLineSuffix.set("#L")
        }
        documentedVisibilities(
            VisibilityModifier.Public,
            VisibilityModifier.Private
        )
    }

    pluginsConfiguration.html {
        footerMessage.set("2025 © EMMA by Arkana Apps")
        customAssets.from("../docs/assets/emma-logo.png")
        customStyleSheets.from(
            "../docs/assets/style.css",
            "../docs/assets/logo-styles.css"
        )
    }
}






