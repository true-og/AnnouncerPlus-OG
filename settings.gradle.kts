plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// Execute bootstrap.sh
exec {
  workingDir(rootDir)
  commandLine("sh", "bootstrap.sh")
}

include("libs:Utilities-OG")
include("libs:GxUI-OG")

rootProject.name = "AnnouncerPlus-OG"
