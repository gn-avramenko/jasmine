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
}


apply{
    plugin("kotlin")
}

dependencies{
    "compile"(files("../lib/spf-1.0.jar"))
}

