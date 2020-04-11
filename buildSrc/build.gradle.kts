//plugins {
//    id("com.github.node-gradle.node") version "2.2.3"
//}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
        "classpath"(files("../lib/spf-1.0.jar"))
    }
}

repositories {
    mavenCentral() // or jcentrer
    jcenter()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}


apply{
    plugin("kotlin")
}

dependencies{
    "compile"(files("../lib/spf-1.0.jar"))
    "compile"("com.github.node-gradle:gradle-node-plugin:2.2.3")
    "compile"("junit:junit:4.12")
    //"compile"("gradle.plugin.com.github.jengelman.gradle.plugins:gradle-processes:0.5.0")
}

