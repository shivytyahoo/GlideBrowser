plugins { id("org.jetbrains.kotlin.jvm") version "2.0.21"; id("org.jetbrains.compose") version "1.7.0" }
dependencies { implementation(project(":shared")); implementation(compose.desktop.currentOs); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1") }
compose.desktop { application { mainClass = "com.shivytyahoo.glidebrowser.MainKt" } }
