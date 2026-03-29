pluginManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
}

rootProject.name = "technical-analysis"

include("stock-db-operations")
include("stock-analysis")
include("combine-inicator-analysis")
include("stock-mcp-server")