architectury {
    common("neoforge", "fabric")
}

loom {
    accessWidenerPath = file("src/main/resources/floodgate.classtweaker")
}

dependencies {
    api(libs.floodgate.core)
    api(libs.floodgate.api)
    api(libs.guice)

    compileOnly(libs.mixin)
    compileOnly(libs.asm)
    compileOnly(libs.geyser.mod) { isTransitive = false }
    compileOnly(libs.geyser.core) { isTransitive = false }

    // Only here to suppress "unknown enum constant EnvType.CLIENT" warnings.
    compileOnly(libs.fabric.loader)
}

afterEvaluate {
    // We don't need these
    tasks.named("renameTask").configure {
        enabled = false
    }

    tasks.named("modrinth").configure {
        enabled = false
    }

    tasks.named("mergeShadowAndJarJar").configure {
        enabled = false
    }
}
