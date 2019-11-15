plugins {
    java
}

group = "me.geso.modernfit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-webflux:5.2.1.RELEASE")
    implementation("io.projectreactor.netty:reactor-netty:0.9.1.RELEASE")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.1.0")
    testImplementation("org.assertj:assertj-core:3.14.0")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.25.1")
    testImplementation("org.slf4j:slf4j-simple:1.7.29")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Test> {
    useJUnitPlatform()
}
