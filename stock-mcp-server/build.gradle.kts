dependencies {
    implementation(project(":combine-inicator-analysis"))
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.mcp.java.sdk)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = false
}
