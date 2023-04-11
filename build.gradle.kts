import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.codebootup.kotlin") version "1.0.0"
    id("org.jetbrains.dokka") version "1.8.10"
    signing
    `maven-publish`
    jacoco
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("com.diffplug.spotless") version "6.18.0"
}

repositories {
    mavenCentral()
}

group = "com.codebootup.compare-directories"
version = (project.properties["buildVersion"] ?: "1.0.0-SNAPSHOT")

dependencies {
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.assertj:assertj-core:3.24.2")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.main.get().kotlin)
}

val javadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Javadoc JAR"
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaHtml"))
}

signing {
    val signingKey = providers
        .environmentVariable("GPG_SIGNING_KEY")
    val signingPassphrase = providers
        .environmentVariable("GPG_SIGNING_PASSPHRASE")
    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
        val extension = extensions
            .getByName("publishing") as PublishingExtension
        sign(extension.publications)
    }
}
object Meta {
    const val desc = "Main use case is for test assertions where you want to compare two directories to identify any differences in tree structure or file content."
    const val license = "Apache-2.0"
    const val githubRepo = "codebootup/compare-directories"
    const val release = "https://s01.oss.sonatype.org/service/local/"
    const val snapshot = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set(project.name)
                description.set(Meta.desc)
                url.set("https://github.com/${Meta.githubRepo}")
                licenses {
                    license {
                        name.set(Meta.license)
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("codebootuplee")
                        name.set("Cooper, Lee")
                        organization.set("codebootup")
                        organizationUrl.set("https://github.com/codebootup")
                    }
                    developer {
                        id.set("codebootuphong")
                        name.set("Koh, Hong Da")
                        organization.set("codebootup")
                        organizationUrl.set("https://github.com/codebootup")
                    }
                }
                scm {
                    url.set(
                        "https://github.com/${Meta.githubRepo}.git",
                    )
                    connection.set(
                        "scm:git:git://github.com/${Meta.githubRepo}.git",
                    )
                    developerConnection.set(
                        "scm:git:git://github.com/${Meta.githubRepo}.git",
                    )
                }
                issueManagement {
                    url.set("https://github.com/${Meta.githubRepo}/issues")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri(Meta.release))
            snapshotRepositoryUrl.set(uri(Meta.snapshot))
            val ossrhUsername = providers
                .environmentVariable("OSSRH_USERNAME")
            val ossrhPassword = providers
                .environmentVariable("OSSRH_PASSWORD")
            if (ossrhUsername.isPresent && ossrhPassword.isPresent) {
                username.set(ossrhUsername.get())
                password.set(ossrhPassword.get())
            }
        }
    }
}

configure<SpotlessExtension> {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}

tasks.named("jacocoTestReport", JacocoReport::class.java) {
    reports {
        xml.required.set(true)
    }
}
