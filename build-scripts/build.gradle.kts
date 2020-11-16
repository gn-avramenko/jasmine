plugins {
    kotlin("jvm") //version "1.4.10"
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

dependencies{
    "compile"(gradleApi())
    "compile"(files("../lib/spf-1.0.jar"))
    "compile"("com.github.node-gradle:gradle-node-plugin:2.2.3")
    "compile"("junit:junit:4.12")
}