plugins { id("org.jetbrains.kotlin.jvm"); id("org.jetbrains.compose") }
dependencies { implementation(project(":shared")); implementation(compose.desktop.currentOs); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1") }
compose.desktop { application { mainClass = "com.shivytyahoo.glidebrowser.MainKt" } }
