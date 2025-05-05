pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Librarian"
include(":app")
include(":common")
include(":core")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":core:database")
include(":core:model")
include(":feature")
include(":feature:dashboard")
include(":feature:profile")
include(":feature:add-books")
include(":feature:search")
include(":feature:notification")
include(":feature:onboarding")
