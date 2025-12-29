plugins {
    alias(libs.plugins.jib)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.mariadb.java.client)
    implementation(libs.commons.csv)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
}
