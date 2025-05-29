import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("application")
	id("org.jetbrains.kotlin.jvm")
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

val embed: Configuration by configurations.creating

dependencies {
	implementation(project(":appCommon"))

	embed(project(":appCommon"))
	embed("org.jetbrains.kotlin:kotlin-stdlib:latest.release")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

application {
	mainClass = "com.github.hummel.union.windows.MainKt"
}

tasks {
	named<JavaExec>("run") {
		standardInput = System.`in`
	}
	jar {
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "com.github.hummel.union.windows.MainKt"
				)
			)
		}
		from(embed.map {
			if (it.isDirectory) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}
