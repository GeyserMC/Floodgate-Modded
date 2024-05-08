import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("floodgate-modded.publish-conventions")
    id("architectury-plugin")
    id("dev.architectury.loom")
    id("com.modrinth.minotaur")
}

// These are all provided by Minecraft - don't include these.
provided("com.google.code.gson", "gson")
provided("com.nukkitx.fastutil", "fastutil-common")
provided("com.nukkitx.fastutil", "fastutil-int-common")
provided("com.nukkitx.fastutil", "fastutil-int-object-maps")
provided("com.nukkitx.fastutil", "fastutil-int-sets")
provided("com.nukkitx.fastutil", "fastutil-object-common")
provided("com.nukkitx.fastutil", "fastutil-object-sets")

architectury {
    minecraft = libs.versions.minecraft.version.get()
}

loom {
    silentMojangMappingsLicense()
}

configurations {
    create("includeTransitive").isTransitive = true
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    // These are under our own namespace
    shadow(libs.floodgate.api) { isTransitive = false }
    shadow(libs.floodgate.core) { isTransitive = false }

    // Requires relocation
    shadow(libs.bstats) { isTransitive = false }

    // Shadow & relocate these since the (indirectly) depend on quite old dependencies
    shadow(libs.guice) { isTransitive = false }
    shadow(libs.configutils) {
        exclude("org.checkerframework")
        exclude("com.google.errorprone")
        exclude("com.github.spotbugs")
        exclude("com.nukkitx.fastutil")
    }

}

tasks {
    processResources {
        filesMatching(listOf("floodgate.mixins.json")) {
            expand("name" to project.name)
        }
    }

    sourcesJar {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    shadowJar {
        // Mirrors the example fabric project, otherwise tons of dependencies are shaded that shouldn't be
        configurations = listOf(project.configurations.shadow.get())

        // Relocate these
        relocate("org.bstats", "org.geysermc.floodgate.shadow.bstats")
        relocate("com.google.inject", "org.geysermc.floodgate.shadow.google.inject")
        relocate("org.yaml", "org.geysermc.floodgate.shadow.org.yaml")

        // The remapped shadowJar is the final desired mod jar
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("shaded")
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    register("remapModrinthJar", RemapJarTask::class) {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveVersion.set(project.version.toString() + "+build."  + System.getenv("GITHUB_RUN_NUMBER"))
        archiveClassifier.set("")
    }
}

afterEvaluate {
    val providedDependencies = getProvidedDependenciesForProject(project.name)

    // These are shaded, no need to JiJ them
    configurations["shadow"].dependencies.forEach {shadowed ->
        println("Not including shadowed dependency: ${shadowed.group}:${shadowed.name}")
        providedDependencies.add("${shadowed.group}:${shadowed.name}")
    }

    configurations["includeTransitive"].resolvedConfiguration.resolvedArtifacts.forEach { dep ->
        if (!providedDependencies.contains("${dep.moduleVersion.id.group}:${dep.moduleVersion.id.name}")
            and !providedDependencies.contains("${dep.moduleVersion.id.group}:.*")) {
            println("Including dependency via JiJ: ${dep.id}")
            dependencies.add("include", dep.moduleVersion.id.toString())
        } else {
            println("Not including ${dep.id} for ${project.name}!")
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) // Even though this is the default value, apparently this prevents GitHub Actions caching the token?
    projectId.set("bWrNNfkb")
    versionNumber.set(project.version as String + "-" + System.getenv("GITHUB_RUN_NUMBER"))
    versionType.set("beta")
    changelog.set("A changelog can be found at https://github.com/GeyserMC/Floodgate-Modded/commits")

    syncBodyFrom.set(rootProject.file("README.md").readText())

    uploadFile.set(tasks.getByPath("remapModrinthJar"))
    gameVersions.addAll("1.20.5", "1.20.6")
    failSilently.set(true)
}