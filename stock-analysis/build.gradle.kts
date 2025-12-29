plugins {
    alias(libs.plugins.jib)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.webflux) // For WebClient
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.ta.lib)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}
