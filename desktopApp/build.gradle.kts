import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins { id("org.jetbrains.kotlin.jvm"); id("org.jetbrains.kotlin.plugin.compose"); id("org.jetbrains.compose") }
dependencies { implementation(project(":shared")); implementation(compose.desktop.currentOs); implementation(compose.material3); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1"); implementation("org.eclipse.platform:org.eclipse.swt.win32.win32.x86_64:3.130.0") }
compose.desktop { application { mainClass = "com.shivytyahoo.glidebrowser.MainKt"
  nativeDistributions { targetFormats(TargetFormat.Msi); packageName = "GlideBrowser"; packageVersion = "1.0.0" } } }
