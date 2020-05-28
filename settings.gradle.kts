rootProject.name = "litecraft"

include(
    ":api", ":api:core", ":api:plugin", ":api:scheduler", ":api:world", ":api:jigsaw",
    ":api:jigsaw:kotlin-stdlib",
    ":server"
)
