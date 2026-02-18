rootProject.name = "yalla-maps"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

val localBomDir = file("../yalla-bom")
if (localBomDir.exists()) {
    includeBuild(localBomDir) {
        dependencySubstitution {
            substitute(module("uz.yalla:bom")).using(project(":"))
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()

        val githubUser = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
        val githubToken = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")

        listOf(
            "Bom" to "yalla-bom",
            "Core" to "yalla-core",
            "Components" to "yalla-components",
            "Design" to "yalla-design",
            "Platform" to "yalla-platform",
            "Resources" to "yalla-resources",
            "Firebase" to "yalla-firebase",
            "Media" to "yalla-media",
            "Maps" to "yalla-maps"
        ).forEach { (nameSuffix, repositoryName) ->
            maven {
                name = "GitHubPackages$nameSuffix"
                url = uri("https://maven.pkg.github.com/RoyalTaxi/$repositoryName")
                credentials {
                    username = githubUser
                    password = githubToken
                }
            }
        }
    }
}
