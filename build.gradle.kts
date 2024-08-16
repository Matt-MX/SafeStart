import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    id("java")
    alias(libs.plugins.runPaper)
    alias(libs.plugins.paperweight) apply true
}

runPaper.folia.registerTask()

val id = findProperty("id").toString()
val pluginName = findProperty("plugin_name")

dependencies {
    paperweight.paperDevBundle(libs.versions.paperApi.get())
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    withType<ProcessResources> {
        val props = mapOf(
            "name" to pluginName,
            "main" to "${findProperty("group_name")}.${id}.${findProperty("plugin_main_class_name")}",
            "author" to findProperty("plugin_author"),
            "version" to if (findProperty("include_commit_hash")
                    .toString().toBoolean()
            ) "${rootProject.version}-commit-${getCurrentCommitHash()}" else rootProject.version.toString()
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    assemble {
        dependsOn("reobfJar")
    }

    test {
        useJUnitPlatform()
    }

    runServer {
        val mcVersion = libs.versions.paperApi.get().split("-")[0]
        minecraftVersion(mcVersion)

        downloadPlugins {
            hangar("ViaVersion", "5.0.1")
            hangar("ViaBackwards", "5.0.1")
        }
    }
}

sourceSets["main"].resources.srcDir("src/resources/")

fun getCurrentCommitHash(): String {
    val process = ProcessBuilder("git", "rev-parse", "HEAD").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val commitHash = reader.readLine()
    reader.close()
    process.waitFor()
    if (process.exitValue() == 0) {
        return commitHash?.substring(0, 7) ?: ""
    } else {
        throw IllegalStateException("Failed to retrieve the commit hash.")
    }
}