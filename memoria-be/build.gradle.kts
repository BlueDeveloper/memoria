plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.brp"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // Swagger / OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Oracle JDBC + Security (Wallet)
    runtimeOnly("com.oracle.database.jdbc:ojdbc11:23.3.0.23.09")
    runtimeOnly("com.oracle.database.security:oraclepki:23.3.0.23.09")
    runtimeOnly("com.oracle.database.security:osdt_cert:19.3.0.0")
    runtimeOnly("com.oracle.database.security:osdt_core:19.3.0.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    jvmArgs = listOf(
        "-Doracle.net.tns_admin=C:/BLUE/Project/blue/OracleCloud/Wallet_BlueAutoDB",
        "-Doracle.net.wallet_location=(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=C:/BLUE/Project/blue/OracleCloud/Wallet_BlueAutoDB)))",
        "-Djavax.net.ssl.trustStore=C:/BLUE/Project/blue/OracleCloud/Wallet_BlueAutoDB/truststore.jks",
        "-Djavax.net.ssl.trustStorePassword=Qwer1234!",
        "-Djavax.net.ssl.keyStore=C:/BLUE/Project/blue/OracleCloud/Wallet_BlueAutoDB/keystore.jks",
        "-Djavax.net.ssl.keyStorePassword=Qwer1234!"
    )
}

// QueryDSL generated sources
val querydslDir = layout.buildDirectory.dir("generated/querydsl")

sourceSets {
    main {
        java {
            srcDir(querydslDir)
        }
    }
}

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(querydslDir)
}

tasks.named("clean") {
    doLast {
        querydslDir.get().asFile.deleteRecursively()
    }
}
