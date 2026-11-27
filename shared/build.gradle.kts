plugins { id("org.jetbrains.kotlin.multiplatform") }
kotlin { jvm(); androidTarget()
  sourceSets { val commonMain by getting { dependencies { implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1") } } } }
