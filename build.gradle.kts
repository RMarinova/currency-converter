plugins {
    id ("org.jetbrains.kotlin.jvm") version "1.9.21"
    id ("application")
    kotlin("plugin.serialization") version "1.4.21"
//    id ("org.jetbrains.kotlin.scripting") version "1.9.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.ktor:ktor-client-core:2.1.0")
    implementation("io.ktor:ktor-client-cio:2.1.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.1.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("CurrencyConversionKt")
}

//tasks.withType<Jar> {
//    manifest {
//        attributes["Main-Class"] = "CurrencyConversionKt"
//    }
//}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "CurrencyConversionKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
