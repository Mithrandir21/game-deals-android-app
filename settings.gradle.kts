

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Game Deals"

include(":app")
include(":base")
include(":common")
include(":common:ui")
include(":logging")
include(":testing")
include(":remote")
include(":remote:gamerpower")
include(":remote:cheapshark")
include(":domain")

include(":feature:store")
include(":feature:deal")
include(":feature:game")
include(":feature:search")
include(":feature:home")
include(":feature:webview")
include(":feature:giveaways")
