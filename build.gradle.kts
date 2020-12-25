import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.4.20"
}

application {
    mainClassName = "com.randomlychosenbytes.jlocker.main.MainFrame"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.jgrapht:jgrapht-ext:0.9.0")
    implementation("org.jgrapht:jgrapht-core:0.9.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23")
    testImplementation("org.mockito:mockito-core:3.6.28")
    testImplementation("org.mockito:mockito-inline:3.6.28")
    testImplementation("org.mockito:mockito-junit-jupiter:3.4.6")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
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
        gradleVersion = "6.7.1"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
