plugins {
	id("org.jetbrains.kotlin.jvm")
}

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