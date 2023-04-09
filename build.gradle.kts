plugins{
    id("com.codebootup.kotlin") version "1.0.0"
}

repositories{
    mavenCentral()
}

group "com.codebootup"
version = (project.properties["buildVersion"] ?: "1.0.0-SNAPSHOT")

dependencies{
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.assertj:assertj-core:3.24.2")
}