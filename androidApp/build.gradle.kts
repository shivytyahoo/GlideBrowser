plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android { namespace = "com.shivytyahoo.glidebrowser"; compileSdk = 34
  defaultConfig { applicationId = "com.shivytyahoo.glidebrowser"; minSdk = 26; targetSdk = 34; versionCode = 1; versionName = "1.0.0"; resConfigs("en", "hi") }
  buildTypes { release { isMinifyEnabled = true; isShrinkResources = true; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro") } }
  splits { abi { isEnable = true; reset(); include("arm64-v8a", "armeabi-v7a", "x86_64") } }
  buildFeatures { compose = true }
  compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
dependencies { implementation("androidx.core:core-ktx:1.13.1"); implementation("androidx.activity:activity-compose:1.9.2"); implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5"); implementation("androidx.compose.ui:ui:1.7.0"); implementation("androidx.compose.material3:material3:1.3.0"); implementation("androidx.compose.material:material-icons-extended:1.7.0"); implementation("androidx.datastore:datastore-preferences:1.1.1"); implementation("androidx.room:room-runtime:2.6.1"); implementation("com.squareup.okhttp3:okhttp:4.12.0"); implementation(project(":shared")) }
