val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    id("java")
    id("application")
    kotlin("jvm") version "1.8.21"
    id("io.ktor.plugin") version "2.3.0"
    kotlin("plugin.serialization") version "1.8.21"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id ("org.beryx.runtime") version "1.13.0"
}



javafx {
    version = "17"
    modules = mutableListOf("javafx.controls")
}


group = "com.example"
version = "0.0.1"

application {
    mainClass.set("ui.MyApp")
    /* val isDevelopment: Boolean = project.ext.has("development")
     if (isDevelopment) {
         applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
     }*/
}
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "ui.MyApp"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("id.zelory:compressor:3.0.1")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")


}
