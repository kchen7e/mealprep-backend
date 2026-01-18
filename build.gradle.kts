plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2"
}

group = "com.mealprep"
version = "0.7.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    enabled = false
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("io.minio:minio:8.5.7")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-aspects")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.build {                                                                                                                                                                                                  
      dependsOn(tasks.clean)                                                                                                                                                                                     
  }
  
 tasks.jar {                                                                                                                                                                                                    
      enabled = false                                                                                                                                                                                            
  } 

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
