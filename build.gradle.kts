import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
	id("jacoco")
}

group = "ar.edu.utn.frba.tacs.tp.api"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("com.google.code.gson", "gson", "2.8.6")

	implementation("com.squareup.okhttp3:okhttp:4.9.1")
	implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

	testImplementation("org.junit.jupiter", "junit-jupiter-api","5.7.0")
	testImplementation("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")
	testImplementation("org.junit.platform", "junit-platform-launcher")
	testImplementation("org.mockito", "mockito-junit-jupiter", "3.7.0")
	testImplementation("org.junit.platform", "junit-platform-runner", "1.7.0")

	testCompileOnly("junit", "junit")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.isEnabled = false
		csv.isEnabled = false
		html.destination = layout.buildDirectory.dir("jacocoHtml").get().asFile
	}
}

tasks.withType<Test> {
		environment("ACCESS_TOKEN","10226310284967175")
}