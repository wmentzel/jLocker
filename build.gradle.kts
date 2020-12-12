plugins {
    java
    application
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

    testCompile("junit", "junit", "4.12")
}
