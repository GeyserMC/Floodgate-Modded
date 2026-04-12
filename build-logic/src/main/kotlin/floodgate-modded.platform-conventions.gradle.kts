plugins {
    id("floodgate-modded.publish-conventions")
    id("architectury-plugin")
    id("dev.architectury.loom-no-remap")
    id("com.modrinth.minotaur")
}

// These are all provided by Minecraft/server platforms
provided("com.google.code.gson", "gson")
provided("org.slf4j", ".*")
provided("com.google.guava", "guava")
provided("org.ow2.asm", "asm")
provided("com.nukkitx.fastutil", ".*")

// these we just don't want to include
provided("org.checkerframework", ".*")
provided("com.google.errorprone", ".*")
provided("com.github.spotbugs", "spotbugs-annotations")
provided("com.google.code.findbugs", ".*")

// cloud-fabric/cloud-neoforge jij's all cloud depends already
provided("org.incendo", ".*")
provided("io.leangen.geantyref", "geantyref")

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
    sourcesJar {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    shadowJar {
        // Mirrors the example fabric project, otherwise tons of dependencies are shaded that shouldn't be
        configurations = listOf(project.configurations.shadow.get())
        archiveBaseName.set("${project.name}-shaded")
        mergeServiceFiles()

        // Relocate these
        relocate("org.bstats", "org.geysermc.floodgate.shadow.bstats")
        relocate("com.google.inject", "org.geysermc.floodgate.shadow.google.inject")
        relocate("org.yaml", "org.geysermc.floodgate.shadow.org.yaml")
    }

    // This task combines the output of the "jar" task, which includes JiJ dependencies,
    // and the shadowJar for the final jar.
    // thanks bluemap
    // https://github.com/BlueMap-Minecraft/BlueMap/blob/cfe73115dc4d1bdd97bc659f41364da65a6a2179/implementations/fabric/build.gradle.kts#L93-L107
    register<Jar>("mergeShadowAndJarJar") {
        dependsOn( tasks.shadowJar, tasks.jar )
        // from sources / final name are configured in the respective projects
        archiveVersion.set("")
        archiveClassifier.set("")
    }

    tasks.register<Copy>("renameTask") {
        val sourceJar = tasks.named<Jar>("mergeShadowAndJarJar")
        dependsOn(sourceJar)

        val modrinthFileName = "${versionName(project)}.jar"
        val libsFile = sourceJar.get().destinationDirectory.get().asFile

        from(sourceJar.get().archiveFile)
        rename { modrinthFileName }
        into(libsFile)

        outputs.file(libsFile.resolve(modrinthFileName))
    }

    // Readme sync
    modrinth.get().dependsOn(tasks.modrinthSyncBody)
    modrinth.get().dependsOn(tasks.getByName("renameTask"))

    build {
        dependsOn(tasks.getByName("mergeShadowAndJarJar"))
    }
}

afterEvaluate {
    val providedDependencies = getProvidedDependenciesForProject(project.name)

    // These are shaded, no need to JiJ them
    configurations["shadow"].resolvedConfiguration.resolvedArtifacts.forEach {shadowed ->
        val string = "${shadowed.moduleVersion.id.group}:${shadowed.moduleVersion.id.name}"
        println("Not including shadowed dependency: $string")
        providedDependencies.add(string)
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
    token.set(System.getenv("MODRINTH_TOKEN") ?: "") // Even though this is the default value, apparently this prevents GitHub Actions caching the token?
    debugMode.set(System.getenv("MODRINTH_TOKEN") == null)
    projectId.set("bWrNNfkb")
    versionName.set(versionName(project))
    versionNumber.set(projectVersion(project))
    versionType.set("release")
    changelog.set("A changelog can be found at https://github.com/GeyserMC/Floodgate-Modded/commits")
    syncBodyFrom.set(rootProject.file("README.md").readText())

    uploadFile.set(project.layout.buildDirectory.file("libs/${versionName(project)}.jar"))
    gameVersions.addAll(libs.minecraft.get().version as String, "26.1.1", "26.1.2")
    failSilently.set(false)
}
