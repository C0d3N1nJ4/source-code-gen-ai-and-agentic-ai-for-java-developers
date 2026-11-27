pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { mavenCentral() } }
rootProject.name = "gen-ai-version-2"
include(":examples:chapter-03-spring-ai", ":acme-enterprise-assistant")
