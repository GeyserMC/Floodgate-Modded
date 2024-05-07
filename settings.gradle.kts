pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://repo.opencollab.dev/maven-snapshots")
        maven("https://jitpack.io") {
            content {
                includeGroupByRegex("com\\.github\\..*")
            }
        }

        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
    }

    plugins {
        id("net.kyori.blossom") version "1.2.0"
        id("net.kyori.indra")
        id("net.kyori.indra.git")
    }

    includeBuild("build-logic")
}

rootProject.name = "floodgate-modded"

include(":shared")
include(":fabric")
include(":neoforge")