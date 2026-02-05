dependencies {
    implementation(project(":combine-inicator-analysis"))
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.ai.mcp.server.spring.boot.starter)
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
