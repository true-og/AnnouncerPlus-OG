plugins {
    java
    eclipse
    id("io.github.goooler.shadow") version "8.1.8"
    id("org.jetbrains.kotlin.jvm") version "2.0.20"
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("net.kyori.indra.git") version "2.0.0"
}

group = "net.trueog.announcerplus-og"
version = "1.0"
val apiVersion = "1.19"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.jpenilla.xyz/snapshots/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        content { includeGroup("me.clip") }
    }
    maven("https://jitpack.io") {
        content { includeGroupByRegex("com\\.github\\..*") }
    }
}

dependencies {
    compileOnly("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("org.purpurmc.purpur:purpur-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("net.essentialsx:EssentialsX:2.20.1") { isTransitive = false }
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.mojang:datafixerupper:7.0.14")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.2.3")
    compileOnly("net.luckperms:api:5.4")
    implementation("xyz.jpenilla:reflection-remapper:0.1.1")
    implementation(platform("org.incendo:cloud-bom:2.0.0-rc.2"))
    implementation("org.incendo:cloud-kotlin-extensions")
    implementation(platform("org.incendo:cloud-minecraft-bom:2.0.0-beta.9"))
    implementation("org.incendo:cloud-paper")
    implementation("org.incendo:cloud-minecraft-extras")
    implementation(platform("org.incendo:cloud-translations-bom:1.0.0-SNAPSHOT"))
    implementation("org.incendo:cloud-translations-core")
    implementation("org.incendo:cloud-translations-bukkit")
    implementation("org.incendo:cloud-translations-minecraft-extras")

    implementation("net.kyori:adventure-extra-kotlin")
    implementation("net.kyori:adventure-serializer-configurate4")
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("io.insert-koin:koin-core:3.5.6")
    implementation("xyz.jpenilla:legacy-plugin-base:0.0.1+122-SNAPSHOT")
    implementation("io.papermc:paperlib:1.0.8")
    implementation(project(":libs:Utilities-OG"))
    implementation(project(":libs:GxUI-OG"))
    implementation(project(":libs:DiamondBank-OG"))

    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release.set(17)
    }

    named<ProcessResources>("processResources") {
        val props = mapOf(
            "version" to version,
            "apiVersion" to apiVersion
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        from("LICENSE") { into("/") }
        minimize()

        val prefix = "${project.group}.${project.name.lowercase()}.lib"

        // Adjusted relocations
        relocate("com.typesafe.config", "$prefix.com.typesafe.config")
        relocate("io.leangen.geantyref", "$prefix.io.leangen.geantyref")
        relocate("io.papermc.lib", "$prefix.io.papermc.lib")

        // Exclude net.kyori.adventure from relocation
        relocate("net.kyori", "$prefix.net.kyori") {
            exclude("net/kyori/adventure/**")
        }

        relocate("org.incendo", "$prefix.org.incendo")
        relocate("org.koin", "$prefix.org.koin")
        relocate("org.spongepowered.configurate", "$prefix.org.spongepowered.configurate")
        relocate("kotlin", "$prefix.kotlin")
        relocate("xyz.jpenilla.reflectionremapper", "$prefix.xyz.jpenilla.reflectionremapper")

        dependencies {
            exclude(dependency("org.jetbrains:annotations"))
            exclude("io.github.miniplaceholders.*")
        }
    }

    named("build") {
        dependsOn("shadowJar")
    }

    jar {
        archiveClassifier.set("part")
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/AnnouncerPlus-${project.version}.jar"))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.GRAAL_VM
    }
}

fun String.decorateVersion(): String =
    if (endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7)
    ?: error("Failed to determine git hash.")

