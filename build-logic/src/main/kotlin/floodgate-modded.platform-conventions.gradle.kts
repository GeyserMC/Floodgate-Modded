plugins {
    id("floodgate-modded.publish-conventions")
    id("java-library")
    id("architectury-plugin")
    id("dev.architectury.loom")
}

// These are provided by Minecraft already, no need to include em
provided("com.google.code.gson", "gson")

architectury {
    minecraft = "1.20.5"
}

loom {
    silentMojangMappingsLicense()
}

configurations {
    create("includeTransitive").isTransitive = true
}

tasks {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this task, sources will not be generated.
    sourcesJar {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    shadowJar {
        // Mirrors the example fabric project, otherwise tons of dependencies are shaded that shouldn't be
        configurations = listOf(project.configurations.shadow.get())
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
    val providedDependenciesSet = getProvidedDependenciesForProject(project.name)
    configurations["includeTransitive"].resolvedConfiguration.resolvedArtifacts.forEach { dep ->
        if (!providedDependenciesSet.contains("${dep.moduleVersion.id.group}:${dep.moduleVersion.id.name}")) {
            println("Including dependency via JiJ: ${dep.moduleVersion.id}")
            dependencies.add("include", dep.moduleVersion.id.toString())
        } else {
            println("Not including ${dep.id} as it is already provided on the ${project.name} platform!")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.5")
    mappings(loom.officialMojangMappings())
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) // Even though this is the default value, apparently this prevents GitHub Actions caching the token?
    projectId.set("wKkoqHrH")
    versionNumber.set(project.version as String + "-" + System.getenv("GITHUB_RUN_NUMBER"))
    versionType.set("beta")
    changelog.set("A changelog can be found at https://github.com/GeyserMC/Floodgate-Modded/commits")

    syncBodyFrom.set(rootProject.file("README.md").readText())

    uploadFile.set(tasks.getByPath("remapModrinthJar"))
    gameVersions.addAll("1.20.5", "1.20.6")
    failSilently.set(true)
}