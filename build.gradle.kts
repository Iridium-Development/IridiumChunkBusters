plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.iridium"
version = "1.0.3"
description = "IridiumChunkBusters"

repositories {
    maven("https://repo.mvdw-software.com/content/groups/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://ci.ender.zone/plugin/repository/everything/")
    maven("https://jitpack.io")
    maven("https://nexus.savagelabs.net/repository/maven-releases/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.jeff-media.de/maven2/")
}

dependencies {
    // Dependencies that we want to shade in
    implementation("org.jetbrains", "annotations", "16.0.1")
    implementation("com.iridium", "IridiumCore", "1.2.3")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")
    implementation("com.github.j256", "ormlite-core", "master-SNAPSHOT")
    implementation("com.j256.ormlite", "ormlite-jdbc", "5.3")
    implementation("de.jeff_media", "SpigotUpdateChecker", "1.2.4")

    // Other dependencies that are not required or already available at runtime
    compileOnly("org.projectlombok", "lombok", "1.18.20")
    compileOnly("org.spigotmc", "spigot-api", "1.17-R0.1-SNAPSHOT")
    compileOnly("net.prosavage.factionsx", "FactionsX", "1.0.8-RC")
    compileOnly("com.massivecraft", "Factions", "1.6.9.5-U0.5.20") {
        exclude("com.darkblade12")
        exclude("org.spongepowered")
        exclude("org.kitteh")
        exclude("com.mojang")
    }
    compileOnly("com.massivecraft.factions", "Factions", "2.14.0")
    compileOnly("com.massivecraft.massivecore", "MassiveCore", "2.14.0")

    // Enable lombok annotation processing
    annotationProcessor("org.projectlombok", "lombok", "1.18.20")
}

tasks {
    // "Replace" the build task with the shadowJar task (probably bad but who cares)
    jar {
        dependsOn("shadowJar")
        enabled = false
    }

    shadowJar {
        // Remove the archive classifier suffix
        archiveClassifier.set("")

        // Relocate dependencies
        relocate("com.fasterxml.jackson", "com.iridium.iridiumchunkbusters.dependencies.fasterxml")
        relocate("com.j256.ormlite", "com.iridium.iridiumchunkbusters.dependencies.ormlite")
        relocate("org.bstats", "com.iridium.iridiumchunkbusters.dependencies.bstats")
        relocate("de.jeff_media", "com.iridium.iridiumchunkbusters.dependencies")

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
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}

// Maven publishing
publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}