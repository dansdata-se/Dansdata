import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jetbrains.kotlinx.dataframe") version "0.14.1"
    id("com.ncorti.ktfmt.gradle") version "0.20.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
}

group = "se.dansdata"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:dataframe-jdbc:0.14.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.3")
    testImplementation("org.postgresql:postgresql:42.7.4")
    testImplementation("io.strikt:strikt-core:0.34.0")
    testImplementation("org.testcontainers:testcontainers:1.20.3")
    testImplementation("org.testcontainers:junit-jupiter:1.20.3")
    testImplementation("org.testcontainers:jdbc:1.20.3")
}

tasks.test { useJUnitPlatform() }

kotlin { jvmToolchain(21) }

tasks.register<KtfmtFormatTask>("ktfmtPrecommit") {
    source = project.fileTree(rootDir)
    include("**/*.kt")
}

detekt {
    buildUponDefaultConfig = true
    config.from("detekt.yml")
}

ktfmt {
    kotlinLangStyle()
    removeUnusedImports.set(true)
    manageTrailingCommas.set(true)
}

tasks.withType<Detekt>().configureEach { jvmTarget = "21" }

tasks.withType<DetektCreateBaselineTask>().configureEach { jvmTarget = "21" }
