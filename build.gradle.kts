
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.dokka") version "1.9.20"
    `maven-publish`
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "com.dennisschroeder"
version = "0.1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    google()
    mavenCentral()
}

val ktorVersion: String by project
val ktlintVersion: String by project
val koinVersion: String by project
val mockkVersion: String by project
val jupiterVersion: String by project
val assertKVersion: String by project
val dataBobVersion: String by project
val jsonAssertVersion: String by project
val kotlinLoggingVersion: String by project
val slf4jVersion: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-gson-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion") {
        exclude(group = "org.mockito")
        exclude(group = "junit")
    }
    testImplementation("io.mockk:mockk:$mockkVersion")
    implementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertKVersion")
    testImplementation("org.skyscreamer:jsonassert:$jsonAssertVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks {
    val dokkaHtml by getting(DokkaTask::class)

    dokkaHtml {
        outputDirectory.set(rootDir.resolve("docs"))
    }
}

defaultTasks("dokkaHtml")

publishing {
    publications {
        register("mavenJava", MavenPublication::class.java) {
            from(components["java"])
        }
    }
}

tasks.withType<Test> {
    environment["HOST"] = "home-assistant.local"
    environment["PORT"] = 8321
    environment["ACCESS_TOKEN"] = "dsq7zht54899dhz43kbv4dgr56a8we234h>!sg?x"
    environment["SECURE"] = true
    environment["START_STATE_STREAM"] = false
    useJUnitPlatform()
}

tasks {
    check {
        dependsOn(test)
    }
}

detekt {
    input = files("$projectDir/src/main/kotlin")
    config = files("$projectDir/config/detekt-config.yml")
}

ktlint {
    version.set(ktlintVersion)
    ignoreFailures.set(false)
}
