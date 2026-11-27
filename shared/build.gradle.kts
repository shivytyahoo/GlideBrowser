plugins { id("org.jetbrains.kotlin.multiplatform"); id("com.android.library") }
kotlin { jvm(); androidTarget()
  sourceSets { val commonMain by getting { dependencies { implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1") } } } }
android { namespace = "com.glidebrowser.shared"; compileSdk = 34
  defaultConfig { minSdk = 26 } }
