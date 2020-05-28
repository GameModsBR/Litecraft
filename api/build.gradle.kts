plugins {
    kotlin("jvm")
}

subprojects {
    if (!path.startsWith(":api:jigsaw")) {
        apply(plugin = "kotlin")

        if (path != ":api:core") {
            dependencies {
                api(project(":api:core"))
            }
        }
    }
}

dependencies {
//    api(project(":api:core"))
    subprojects.forEach {
        if (!it.path.startsWith(":api:jigsaw")) {
            this.api(project(it.path))
        }
    }
}
