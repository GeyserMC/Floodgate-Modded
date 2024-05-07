plugins {
    id("floodgate-modded.build-logic")
}

val platforms = setOf(
    projects.fabric,
    projects.neoforge,
    projects.shared
).map { it.dependencyProject }

subprojects {
    when (this) {
        in platforms -> plugins.apply("floodgate-modded.platform-conventions")
        else -> plugins.apply("floodgate-modded.base-conventions")
    }
}