plugins {
    `java-library`
    id("floodgate-modded.build-logic")
    alias(libs.plugins.lombok) apply false
}

val platforms = setOf(
    projects.fabric,
    projects.neoforge,
    projects.shared
).map { it.dependencyProject }

subprojects {
    apply {
        plugin("java-library")
        plugin("io.freefair.lombok")
        plugin("floodgate-modded.build-logic")
    }

    when (this) {
        in platforms -> plugins.apply("floodgate-modded.platform-conventions")
        else -> plugins.apply("floodgate-modded.base-conventions")
    }
}

//repositories {
//    // Floodgate, Cumulus etc.
//    maven("https://repo.opencollab.dev/main")
//
//    // NeoForge
//    maven("https://maven.neoforged.net/releases")
//
//    // Minecraft
//    maven("https://libraries.minecraft.net") {
//        name = "minecraft"
//        mavenContent { releasesOnly() }
//    }
//
//    mavenCentral()
//}