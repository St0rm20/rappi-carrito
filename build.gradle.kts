plugins {
    java
    // Spring Boot and Dependency Management
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"

    // 💡 NUEVO: Plugin de JavaFX para compilar y ejecutar
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.uniquindio"
version = "0.0.1-SNAPSHOT"
description = "rappi-carrito"

// 💡 NUEVO: Configuración de la versión de JavaFX
javafx {
    version = "21" // Debe coincidir con tu Java 21
    modules = listOf("javafx.controls", "javafx.fxml") // Módulos necesarios
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
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
    // 1. Spring Boot Core
    implementation("org.springframework.boot:spring-boot-starter")

    // 💡 NUEVO: Persistencia (JPA) para Productos y Carrito
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 💡 NUEVO: Validación de Modelos (Opcional, pero buena práctica)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // 2. JavaFX (Ya cubierto por el plugin, pero buena práctica listarlos)
    // implementation("org.openjfx:javafx-controls:${javafx.version}")
    // implementation("org.openjfx:javafx-fxml:${javafx.version}")

    // 3. Utilidades y Development
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Base de Datos
    runtimeOnly("com.mysql:mysql-connector-j")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}