architectury {
    common("neoforge", "fabric")
}

loom {
    mixin.defaultRefmapName.set("floodgate-refmap.json")
    accessWidenerPath = file("src/main/resources/floodgate.accesswidener")
}

dependencies {
    api(libs.floodgate.core)
    api(libs.floodgate.api)
    api(libs.guice)

    // Only here to suppress "unknown enum constant EnvType.CLIENT" warnings.
    compileOnly(libs.fabric.loader)
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://repo.opencollab.dev/main/")
    maven("https://jitpack.io") {
        content {
            includeGroupByRegex("com.github.*")
        }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}