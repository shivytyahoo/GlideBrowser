pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}
rootProject.name = "GlideBrowser"
include(":shared", ":androidApp", ":desktopApp")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
