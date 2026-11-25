import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "dev.nickl0s"
version = providers.gradleProperty("version").get()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdea("2026.3")
        bundledPlugin("com.intellij.platform.vcs")
        bundledModule("intellij.platform.vcs.core")
        bundledModule("intellij.platform.vcs.shared")
        bundledModule("intellij.platform.vcs.impl.shared")
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "263"
        }
        changeNotes = """
            <ul>
              <li>Rename the plugin to Coding Agent Toolkit.</li>
              <li>Remove the coding harness launcher and its settings.</li>
              <li>Add a customizable shortcut to toggle whole-file inclusion from the Commit diff.</li>
            </ul>
        """.trimIndent()
    }
}

tasks {
    withType<JavaCompile> {
        options.release = 25
    }
}
