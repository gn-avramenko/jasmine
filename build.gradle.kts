apply<com.gridnine.jasmine.gradle.plugin.JasminePlugin>()

buildscript{
    repositories{
        mavenCentral()
    }
    dependencies{
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    }
}

repositories {
    mavenCentral() // or jcentrer
    jcenter()
}


apply{
    plugin("kotlin")
}
