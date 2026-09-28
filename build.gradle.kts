import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "dev.nickl0s"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdea("2026.2.3")
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "262"
        }
        changeNotes = """
            <ul>
              <li>Initial release: copy a selected editor range as an OpenCode reference.</li>
            </ul>
        """.trimIndent()
    }
}

tasks {
    withType<JavaCompile> {
        options.release = 25
    }
}
