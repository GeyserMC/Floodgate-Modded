architectury {
    platformSetupLoomIde()
    neoForge()
}

val common: Configuration by configurations.creating
// Without this, the mixin config isn't read properly with the runServer neoforge task
val developmentNeoForge: Configuration = configurations.getByName("developmentNeoForge")
val includeTransitive: Configuration = configurations.getByName("includeTransitive")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentNeoForge.extendsFrom(configurations["common"])
}

dependencies {
    // See https://github.com/google/guava/issues/6618
    modules {
        module("com.google.guava:listenablefuture") {
            replacedBy("com.google.guava:guava", "listenablefuture is part of guava")
        }
    }

    common(project(":shared", configuration = "namedElements")) { isTransitive = false }
    neoForge(libs.neoforge)

    api(libs.floodgate.core)
    api(libs.floodgate.api)
    api(libs.guice)

    modImplementation(libs.cloud.neoforge)
    include(libs.cloud.neoforge)

//    modLocalRuntime(libs.geyser.neoforge) {
//        exclude(group = "io.netty")
//        exclude(group = "io.netty.incubator")
//    }

    shadow(project(path = ":shared", configuration = "transformProductionNeoForge")) { isTransitive = false }
}

tasks {
    processResources {
        from(project(":common").file("src/main/resources/***.accesswidener")) {
            into("/assets/")
        }
    }

    remapJar {
        dependsOn(processResources)
        atAccessWideners.add("***.accesswidener")
        archiveBaseName.set("floodgate-neoforge")
    }
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