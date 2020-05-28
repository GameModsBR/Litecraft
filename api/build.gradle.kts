plugins {
    kotlin("jvm")
}

subprojects {
    apply(plugin = "kotlin")
    
    if (path != ":api:core") {
        dependencies {
            api(project(":api:core"))
        }
    }
}

dependencies {
    subprojects.forEach {
        api(project(it.path))
    }
}
