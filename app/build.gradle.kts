plugins {
   id("com.android.application")
   id("org.jetbrains.kotlin.android")
   id("dagger.hilt.android.plugin")
   // kotlin("kapt")
   id("com.google.devtools.ksp")
}
android {
   namespace = "de.rogallab.mobile"
   compileSdk = 34

   defaultConfig {
      applicationId = "de.rogallab.mobile"
      minSdk = 26
      targetSdk = 34
      versionCode = 1
      versionName = "1.0"

      javaCompileOptions {
         annotationProcessorOptions {
            arguments += mapOf(
               "room.schemaLocation" to "$projectDir/schemas",
               "room.incremental" to "true"
            )
         }
      }

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      vectorDrawables {
         useSupportLibrary = true
      }
   }

   buildTypes {
      release {
         isMinifyEnabled = false
         proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      }
   }
   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
   }
   lint {
      abortOnError = false
      disable  += "unchecked"
   }
   kotlinOptions {
      jvmTarget = "17"
   }
   kotlin {
      jvmToolchain(17)
   }
   buildFeatures {
      compose = true
   }
   composeOptions {
      kotlinCompilerExtensionVersion = "1.5.3"
   }
   packaging {
      resources {
         excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
   }
}

dependencies {

   // https://developer.android.com/jetpack/androidx/releases/activity
   val activityCompose = "1.8.1"
   implementation("androidx.activity:activity-compose:$activityCompose")

   // https://developer.android.com/jetpack/androidx/releases/core
   val core = "1.12.0"
   implementation("androidx.core:core-ktx:$core")

   // A BOM is a Maven module that declares a set of libraries with their versions.
   // It will greatly simplify the way you define Compose library versions in your
   // Gradle dependencies block.
   // https://developer.android.com/jetpack/compose/bom/bom-mapping
   val compose = "1.5.4"
   implementation(platform("androidx.compose:compose-bom:2023.10.01"))
   implementation("androidx.compose.ui:ui")
   implementation("androidx.compose.ui:ui-graphics")
   implementation("androidx.compose.ui:ui-tooling-preview")
   val material3 = "1.1.2"
   implementation("androidx.compose.material3:material3:$material3")
   implementation("androidx.compose.material:material-icons-extended:$compose")

   // Lifecycle,
   // https://developer.android.com/jetpack/androidx/releases/lifecycle
   val lifecycleVersion = "2.6.2"
   // val archVersion = "2.2.0"
   // ViewModel
   implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
   // ViewModel utilities for Compose
   implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
   // LiveData
   implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
   // Livecycle Utilities for Compose
   implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
   // Saved state Module for ViewModel
   implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")

   // Navigation
   // https://developer.android.com/jetpack/androidx/releases/navigation
   val navigationVersion = "2.7.5"
   implementation( "androidx.navigation:navigation-ui-ktx:$navigationVersion")
   implementation("androidx.navigation:navigation-compose:$navigationVersion")

   // Coroutines
   // https://kotlinlang.org/docs/releases.html
   val kotlinCoroutines = "1.7.3"
   implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutines")
   implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutines")

   // Room Database
   // https://developer.android.com/jetpack/androidx/releases/room
   val roomVersion = "2.6.1"
   implementation("androidx.room:room-runtime:$roomVersion")
   implementation("androidx.room:room-ktx:$roomVersion")
// kapt("androidx.room:room-compiler:$roomVersion")
   ksp("androidx.room:room-compiler:$roomVersion")

   // Dagger, Hilt
   // https://developer.android.com/training/dependency-injection/hilt-android
   // https://dagger.dev/hilt/
   // https://developer.android.com/jetpack/androidx/releases/hilt
   val hiltAndroidVersion = "2.49"
   val hiltVersion = "1.1.0"
   implementation ("com.google.dagger:hilt-android:$hiltAndroidVersion")
   ksp("com.google.dagger:hilt-compiler:$hiltAndroidVersion")
   implementation ("androidx.hilt:hilt-navigation-compose:$hiltVersion")
   ksp            ("androidx.hilt:hilt-compiler:$hiltVersion")


   // Image loading
   // https://coil-kt.github.io/coil/
   val coilComposeVersion = "2.5.0"
   implementation("io.coil-kt:coil-compose:$coilComposeVersion")

   // TESTS -----------------------
   testImplementation("junit:junit:4.13.2")

   // ANDROID TESTS ---------------
   // https://developer.android.com/jetpack/androidx/releases/test
   val androidTestCore = "1.5.0"
   // To use the androidx.test.core APIs
   androidTestImplementation("androidx.test:core:$androidTestCore")
   androidTestImplementation("androidx.test:core-ktx:$androidTestCore")
   val espresso = "3.5.1"
   androidTestImplementation("androidx.test.espresso:espresso-core:$espresso")
   val extJunit = "1.1.5"
   androidTestImplementation("androidx.test.ext:junit:$extJunit")
   androidTestImplementation("androidx.test.ext:junit-ktx:$extJunit")
   val truth = "1.5.0"
   androidTestImplementation("androidx.test.ext:truth:$truth")
   val runner = "1.5.2"
   androidTestImplementation("androidx.test:runner:$runner")
   // Assertions
   // androidTestImplementation ("com.google.truth:truth:1.1.5")

   // To use Compose Testing
   androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
   androidTestImplementation("androidx.compose.ui:ui-test-junit4")
   debugImplementation("androidx.compose.ui:ui-tooling")
   val uiTestManifest = "1.5.4"
   debugImplementation("androidx.compose.ui:ui-test-manifest:$uiTestManifest")

   androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltAndroidVersion")
   kspAndroidTest           ("com.google.dagger:hilt-compiler:$hiltAndroidVersion")
   androidTestImplementation("androidx.hilt:hilt-navigation-compose:$hiltVersion")
   kspAndroidTest           ("androidx.hilt:hilt-compiler:$hiltVersion")
   //androidTestImplementation ("com.squareup.okhttp3:mockwebserver:5.0.0-alpha.11")
   //androidTestImplementation ("io.mockk:mockk-android:1.13.8")

   //androidTestImplementation ("org.mockito.kotlin:mockito-kotlin:5.1.0")
   androidTestImplementation("androidx.navigation:navigation-testing:$navigationVersion")
   androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutines")
}