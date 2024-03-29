import com.avast.gradle.dockercompose.ComposeSettings

plugins {
    kotlin("jvm") version "1.6.10"

    application
    `maven-publish`

    id("com.avast.gradle.docker-compose") version "0.14.9"
    id("uk.co.lukestevens.plugins.release-helper") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "uk.co.lucystevens"
version = "0.0.3"

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val ktormVersion = "3.5.0"
val koinVersion= "3.3.2"
val dockerJavaVersion = "3.2.13"
val jacksonVersion = "2.14.0"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.insert-koin:koin-core:$koinVersion")

    // ktorm for database connections
    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation("org.ktorm:ktorm-support-postgresql:$ktormVersion")
    implementation("org.postgresql:postgresql:42.5.1")

    // javalin + jackson for API server
    implementation("io.javalin:javalin:5.3.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // okhttp for API requests
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // logback for logging
    implementation("ch.qos.logback:logback-classic:1.4.5")

    // testing
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.2")
}

application {
    mainClass.set("uk.co.lucystevens.LauncherKt")
}

/**
 *  Tasks
 */
configure<ComposeSettings> {
    startedServices.set(listOf("local-db"))
    forceRecreate.set(true)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}

val integrationTest = task<Test>("integrationTest") {
    useJUnitPlatform()
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    outputs.upToDateWhen { false }
    mustRunAfter(tasks.composeUp)
}

dockerCompose.isRequiredBy(integrationTest)
