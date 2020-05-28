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
        
        val moduleName = "br.com.gamemods.litecraft"+path.replace(':', '.')

        tasks.withType<JavaCompile>().configureEach {
            options.javaModuleVersion.set(provider { project.version as String })
            
            // this is needed because we have a separate compile step in this example with the 'module-info.java' is in 'main/java' and the Kotlin code is in 'main/kotlin'
            options.compilerArgs = listOf("--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}")
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions.jvmTarget = "13"
            kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.contracts.ExperimentalContracts"
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
            api(kotlin("stdlib"))
            api(kotlin("stdlib-jdk7"))
            api(kotlin("stdlib-jdk8"))

            attributesSchema.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE).compatibilityRules.add(ModularJarCompatibilityRule::class)

            components { withModule<ModularKotlinRule>(kotlin("stdlib")) }
            components { withModule<ModularKotlinRule>(kotlin("stdlib-jdk7")) }
            components { withModule<ModularKotlinRule>(kotlin("stdlib-jdk8")) }
        }

        configurations.all {
            val n = name.toLowerCase()
            if (n.endsWith("compileclasspath") || n.endsWith("runtimeclasspath"))
                attributes.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("modular-jar"))
            if (n.endsWith("compile") || n.endsWith("runtime"))
                isCanBeConsumed = false
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

abstract class ModularJarCompatibilityRule : AttributeCompatibilityRule<LibraryElements> {
    override fun execute(details: CompatibilityCheckDetails<LibraryElements>): Unit = details.run {
        if (producerValue?.name == LibraryElements.JAR && consumerValue?.name == "modular-jar")
            compatible()
    }
}

//https://stackoverflow.com/questions/61638287/gradle-6-4-kotlin-and-jpms/61766500?noredirect=1#comment109264242_61766500
abstract class ModularKotlinRule : ComponentMetadataRule {

    @javax.inject.Inject
    abstract fun getObjects(): ObjectFactory

    override fun execute(ctx: ComponentMetadataContext) {
        val id = ctx.details.id
        listOf("compile", "runtime").forEach { baseVariant ->
            ctx.details.addVariant("${baseVariant}Modular", baseVariant) {
                attributes {
                    attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, getObjects().named("modular-jar"))
                }
                withFiles {
                    removeAllFiles()
                    addFile("${id.name}-${id.version}-modular.jar")
                }
                withDependencies {
                    clear() // 'kotlin-stdlib-common' and  'annotations' are not modules and are also not needed
                }
            }
        }
    }
}
