import com.gridnine.jasmine.gradle.plugin.ServerDescriptionExtension
import com.gridnine.jasmine.gradle.plugin.development
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply<com.gridnine.jasmine.gradle.plugin.JasminePlugin>()



buildscript {
    repositories {
        mavenCentral()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    }
}
plugins {
    id("com.github.node-gradle.node")
}



//
//task<com.moowork.gradle.node.npm.NpmTask>("_setupNode"){
//    setArgs(arrayListOf("install", "mocha"))
//    group = "idea"
//}

repositories {
    mavenCentral() // or jcentrer
    jcenter()
}



