import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    id("org.sonarqube") version "2.8"
    jacoco
}

allprojects {
    group = "br.com.gamemods"
    version = "0.1.0-SNAPSHOT"
    
    repositories { 
        mavenCentral()
        jcenter()
    }

    afterEvaluate {
        if (!plugins.hasPlugin("kotlin")) {
            return@afterEvaluate
        }

        if (!plugins.hasPlugin("org.sonarqube")) {
            apply(plugin = "org.sonarqube")
        }
        
        if (!plugins.hasPlugin("jacoco")) {
            apply(plugin = "jacoco")
        }

        sonarqube {
            properties {
                property("sonar.sources", "src")
            }
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
            api(kotlin("stdlib-jdk8", embeddedKotlinVersion))
            api(kotlin("reflect", embeddedKotlinVersion))
            
            testImplementation(kotlin("test-junit5", embeddedKotlinVersion))
            
            testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0-M1")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0-M1")

            testImplementation("org.mockito:mockito-junit-jupiter:3.3.3")
            testImplementation("org.mockito:mockito-inline:3.3.3")
            testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
            
            testImplementation("org.hamcrest:hamcrest:2.2")
            testImplementation("com.natpryce:hamkrest:1.7.0.3")
        }

        tasks.withType<Test> {
            useJUnitPlatform()
            testLogging.showStandardStreams = true
            testLogging {
                events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")
            }
        }

        jacoco {
            //toolVersion = jacocoVersion
            reportsDir = file("$buildDir/reports/jacoco")
        }

        tasks {
            named<JacocoReport>("jacocoTestReport") {
                classDirectories.setFrom(files("${buildDir}/classes"))
                reports {
                    xml.isEnabled = true
                    html.isEnabled = true
                }
            }
        }
    }
    
}

listOf(project(":api:jigsaw"), project(":api:jigsaw")).forEach {
    it.sonarqube {
        isSkipProject = true
    }
}

sonarqube {
    properties {
        property("sonar.scm.provider", "git")
        //property("sonar.jacoco.reportPaths", allTestCoverageFile)
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", "<secret>")
        property("sonar.organization", "gamemods")
        property("sonar.projectKey","GameModsBR_Litecraft")
        property("sonar.projectName", "Litecraft")
        property("sonar.java.source", "14")
        property("sonar.cpd.cross_project", true)
        property("sonar.rootModuleName", "litecraft")
        //property("sonar.java.binaries", "build/libs/xyz-0.0.1-SNAPSHOT.jar")
        //property("sonar.java.coveragePlugin", "jacoco")
        //property("sonar.tests", "src/test")
        //property("sonar.java.test.binaries", "build/classes/java/test")
        //property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/codeCoverageReport/codeCoverageReport.xml")
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

// task to gather code coverage from multiple subprojects
// NOTE: the `JacocoReport` tasks do *not* depend on the `test` task by default. Meaning you have to ensure
// that `test` (or other tasks generating code coverage) run before generating the report.
// You can achieve this by calling the `test` lifecycle task manually
// $ ./gradlew test codeCoverageReport
/*
tasks.register<JacocoReport>("codeCoverageReport") {
    // If a subproject applies the 'jacoco' plugin, add the result it to the report
    subprojects {
        val subproject = this
        subproject.plugins.withType<JacocoPlugin>().configureEach {
            subproject.tasks.matching { it.extensions.findByType<JacocoTaskExtension>() != null }.configureEach {
                val testTask = this
                sourceSets(subproject.sourceSets.main.get())
                executionData(testTask)
            }

            // To automatically run `test` every time `./gradlew codeCoverageReport` is called,
            // you may want to set up a task dependency between them as shown below.
            // Note that this requires the `test` tasks to be resolved eagerly (see `forEach`) which
            // may have a negative effect on the configuration time of your build.
            subproject.tasks.matching { it.extensions.findByType<JacocoTaskExtension>() != null }.forEach {
                rootProject.tasks["codeCoverageReport"].dependsOn(it)
            }
        }
    }

    // enable the different report types (html, xml, csv)
    reports {
        // xml is usually used to integrate code coverage with
        // other tools like SonarQube, Coveralls or Codecov
        xml.isEnabled = true

        // HTML reports can be used to see code coverage
        // without any external tools
        html.isEnabled = true
    }
}
*/
