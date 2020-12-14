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
    "wrapper"(Wrapper::class) {
        gradleVersion = "6.3"
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "com.randomlychosenbytes.jlocker.main.MainFrame"
        }
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
