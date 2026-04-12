repositories {
    // mavenLocal()
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases")
    maven("https://repo.opencollab.dev/main/")
    maven("https://jitpack.io") {
        content {
            includeGroupByRegex("com.github.*")
        }
    }
    maven("https://central.sonatype.com/repository/maven-snapshots/")
}
