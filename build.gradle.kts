plugins {
    java
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.iridium"
version = "1.0.9"
description = "IridiumChunkBusters"

repositories {
    maven("https://repo.mvdw-software.com/content/groups/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://ci.ender.zone/plugin/repository/everything/")
    maven("https://jitpack.io")
    maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.jeff-media.com/public/")
    mavenCentral()
}

dependencies {
    // Dependencies that we want to shade in
    implementation("org.jetbrains:annotations:26.0.1")
    implementation("com.iridium:IridiumCore:2.0.9")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("com.j256.ormlite:ormlite-core:6.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("de.jeff_media:SpigotUpdateChecker:1.3.2")

    // Other dependencies that are not required or already available at runtime
    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")

    // Enable lombok annotation processing
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

tasks {
    // "Replace" the build task with the shadowJar task (probably bad but who cares)
    jar {
        dependsOn("shadowJar")
        enabled = false
    }

    shadowJar {
        fun relocate(origin: String) =
            relocate(origin, "com.iridium.iridiumchunkbusters.dependencies${origin.substring(origin.lastIndexOf('.'))}")

        // Remove the archive classifier suffix
        archiveClassifier.set("")

        // Relocate dependencies
        relocate("com.iridium.iridiumcore")
        relocate("com.j256.ormlite")
        relocate("de.jeff_media.updatechecker")
        relocate("org.bstats")
        relocate("org.intellij.lang.annotations")
        relocate("org.jetbrains.annotations")
        relocate("org.jnbt")

        // Relocate IridiumCore dependencies
        relocate("de.tr7zw.changeme.nbtapi")
        relocate("com.iridium.iridiumcolorapi")
        relocate("org.yaml.snakeyaml")
        relocate("io.papermc.lib")
        relocate("com.cryptomorin.xseries")
        relocate("com.fasterxml.jackson")
        relocate("org.apache.commons")

        // Remove unnecessary files from the jar
        minimize()
    }

    // Set UTF-8 as the encoding
    compileJava {
        options.encoding = "UTF-8"
    }

    // Process Placeholders for the plugin.yml
    processResources {
        filesMatching("**/plugin.yml") {
            expand(rootProject.project.properties)
        }

        // Always re-run this task
        outputs.upToDateWhen { false }
    }

    compileJava {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    compileTestJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }
}

// Maven publishing
publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
