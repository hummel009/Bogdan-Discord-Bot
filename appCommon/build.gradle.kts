plugins {
	id("org.jetbrains.kotlin.jvm")
}

dependencies {
	implementation("net.lingala.zip4j:zip4j:latest.release")
	implementation("com.google.code.gson:gson:latest.release")
	implementation("org.apache.httpcomponents.client5:httpclient5:latest.release")
	implementation("org.javacord:javacord:latest.release") {
		exclude(group = "org.bouncycastle")
	}
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}