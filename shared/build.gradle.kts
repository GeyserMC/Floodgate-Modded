architectury {
    common("neoforge", "fabric")
}

loom {
    mixin.defaultRefmapName.set("floodgate-refmap.json")
}

dependencies {


    // Only here to suppress "unknown enum constant EnvType.CLIENT" warnings.
    compileOnly(libs.fabric.loader)
}