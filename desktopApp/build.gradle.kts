import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins { id("org.jetbrains.kotlin.jvm"); id("org.jetbrains.kotlin.plugin.compose"); id("org.jetbrains.compose") }
dependencies { implementation(project(":shared")); implementation(compose.desktop.currentOs); implementation(compose.material3); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
  // SWT's base POM depends on the literal placeholder org.eclipse.swt.${osgi.platform}
  // (Eclipse's own build sets that property; Gradle never does), so plain
  // implementation("...:org.eclipse.swt.win32.win32.x86_64:3.130.0") fails
  // resolution in run #18. Declare both jars directly and exclude the broken edges.
  implementation("org.eclipse.platform:org.eclipse.swt.win32.win32.x86_64:3.130.0") { exclude(group = "org.eclipse.platform", module = "org.eclipse.swt") }
  implementation("org.eclipse.platform:org.eclipse.swt:3.130.0") { exclude(group = "org.eclipse.platform", module = "org.eclipse.swt.\${osgi.platform}") } }
compose.desktop { application { mainClass = "com.shivytyahoo.glidebrowser.MainKt"
  nativeDistributions { targetFormats(TargetFormat.Msi); packageName = "GlideBrowser"; packageVersion = "1.0.0" } } }
