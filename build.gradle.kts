import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("application")
	id("org.jetbrains.kotlin.jvm") version "latest.release"
}

group = "io.github.hummel009"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

dependencies {
	implementation("net.lingala.zip4j:zip4j:latest.release")
	implementation("com.google.code.gson:gson:latest.release")
	implementation("org.apache.httpcomponents.client5:httpclient5:latest.release")
	implementation("net.dv8tion:JDA:latest.release")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

application {
	mainClass = "io.github.hummel009.discord.bogdan.MainKt"
}

tasks {
	named<JavaExec>("run") {
		standardInput = System.`in`
	}
	jar {
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "io.github.hummel009.discord.bogdan.MainKt"
				)
			)
		}
		from(configurations.runtimeClasspath.get().map {
			if (it.isDirectory) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}