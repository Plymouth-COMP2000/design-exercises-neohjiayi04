pluginManagement {
    repositories {
<<<<<<< HEAD
        google()
=======
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        mavenCentral()
        gradlePluginPortal()
    }
}
<<<<<<< HEAD

=======
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

<<<<<<< HEAD
rootProject.name = "DINEO2"
include(":app")
=======
rootProject.name = "Dineo"
include(":app")
 
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
