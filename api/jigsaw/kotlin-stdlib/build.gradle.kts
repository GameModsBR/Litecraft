plugins {
    kotlin("jvm")
}

tasks {
    compileJava {
        options.compilerArgs = options.compilerArgs + listOf("-Xlint:-requires-transitive-automatic")
    }
}
