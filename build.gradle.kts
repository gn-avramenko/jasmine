apply<com.gridnine.jasmine.gradle.plugin.JasminePlugin>()

buildscript{
    repositories{
        mavenCentral()
    }
    dependencies{
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    }
}
plugins {
    id("com.github.node-gradle.node")
}

//node{
//    download = true
//}
//
//task<com.moowork.gradle.node.npm.NpmTask>("_setupNode"){
//    setArgs(arrayListOf("install", "mocha"))
//    group = "idea"
//}

repositories {
    mavenCentral() // or jcentrer
    jcenter()
}



