plugins {
    jacoco
}

dependencies {
    api(project(":api:jigsaw:kotlin-stdlib"))
}

tasks.withType<JacocoReport> {
    reports {
        html.apply {
            isEnabled = true
            destination = file("$buildDir/reports/jacoco")
        }
        xml.isEnabled = true
        csv.isEnabled = false

        sourceDirectories.setFrom(files("${project.projectDir}/src/main/kotlin"))
        classDirectories.setFrom(fileTree("${buildDir}/classes/kotlin/main"){ exclude("**/model/**") })
        executionData.setFrom(files("${buildDir}/jacoco/test.exec"))
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it).apply {
                exclude("io/company/common/aws/test/support/**")
                exclude("io/company/common/system/**")
            }
        }))
    }
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

//
//jacoco {
//    toolVersion = "0.7.9"
//    reportsDir = file("$buildDir/reports")
//}
//
//tasks.register<JacocoReport>("jacocoTestReport22") {
//    group = "Reporting"
//    description = "Generate Jacoco coverage reports for Debug build"
//
//    reports {
//        xml.isEnabled = true
//        html.isEnabled = true
//    }
//
//    // what to exclude from coverage report
//    // UI, "noise", generated classes, platform classes, etc. 
//    val excludes = listOf(
//            "**/R.class",
//            "**/R$*.class",
//            "**/*\$ViewInjector*.*",
//            "**/BuildConfig.*",
//            "**/Manifest*.*",
//            "**/*Test*.*",
//            "android/**/*.*",
//            "**/*Fragment.*",
//            "**/*Activity.*"
//    )
//    // generated classes
//    classDirectories.setFrom(
//        fileTree("$buildDir/intermediates/classes/debug"){ exclude(excludes) }, 
//        fileTree("$buildDir/tmp/kotlin-classes/debug"){ exclude(excludes) }
//    )
//
//    // sources
//    sourceDirectories.setFrom(files(listOf(
//        sourceSets["main"].java.srcDirs,
//        "src/main/kotlin"
//    )))
//    executionData.setFrom(files("$buildDir/jacoco/testDebugUnitTest.exec"))
//}
