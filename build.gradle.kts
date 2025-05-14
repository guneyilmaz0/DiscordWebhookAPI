plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.9.23"
    `maven-publish`
}

kotlin {
    jvmToolchain(17)
}

group = "net.guneyilmaz0.webhook"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    compileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.guneyilmaz0.webhook"
            artifactId = "DiscordWebhookAPI"
            version = "1.0"

            from(components["java"])
        }
    }
}