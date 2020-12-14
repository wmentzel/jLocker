import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.4.20"
}

application {
    mainClass.set("com.randomlychosenbytes.jlocker.main.MainFrame")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.jgrapht:jgrapht-ext:0.9.0")
    implementation("org.jgrapht:jgrapht-core:0.9.0")

    testImplementation("junit", "junit", "4.12")
}

tasks {

    register<Jar>("fatJar") {
        manifest {
            attributes["Main-Class"] = "com.randomlychosenbytes.jlocker.main.MainFrame"
        }
        archiveBaseName.set("${project.name}")
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        with(jar.get())
    }

    "wrapper"(Wrapper::class) {
        gradleVersion = "6.3"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
