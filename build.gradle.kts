import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
}

allprojects {
    group = "br.com.gamemods"
    version = "0.1.0-SNAPSHOT"

    repositories {
        jcenter()
    }
    
    afterEvaluate {
        if (!plugins.hasPlugin("kotlin")) {
            return@afterEvaluate
        }
        
        val moduleName = "br.com.gamemods.litecraft"+path.replace(':', '.').replace('-','.')

        tasks.withType<JavaCompile>().configureEach {
            options.javaModuleVersion.set(provider { project.version as String })
            
            // this is needed because we have a separate compile step in this example with the 'module-info.java' is in 'main/java' and the Kotlin code is in 'main/kotlin'
            //options.compilerArgs = listOf("--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}")
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions.jvmTarget = "13"
            kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.contracts.ExperimentalContracts"
        }
        
        tasks.named<JavaCompile>("compileJava") {
            dependsOn(":compileKotlin")
            inputs.property("moduleName", moduleName)
            doFirst {
                options.compilerArgs = options.compilerArgs + listOf(
                    // include Gradle dependencies as modules
                    "--module-path", sourceSets["main"].compileClasspath.asPath,
                    "--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}"
                )
                sourceSets["main"].compileClasspath = files()
            }
        }

        sourceSets.main.configure {
            //java.setSrcDirs(listOf("src/main/kotlin"))
        }
        
        plugins.withType<JavaPlugin>().configureEach {
            configure<JavaPluginExtension> {
                modularity.inferModulePath.set(true)
            }
        }

        dependencies {
            api(kotlin("stdlib-jdk8"))
        }
    }
    
}

sourceSets {
    main {
        java.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
    
    test {
        java.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}

kotlin.sourceSets {
    main { kotlin.setSrcDirs(emptyList<String>()) }
    test { kotlin.setSrcDirs(emptyList<String>()) }
}
