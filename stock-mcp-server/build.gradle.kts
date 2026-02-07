dependencies {
    implementation(project(":combine-inicator-analysis"))
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.ai.mcp.server.spring.boot.starter)
    // implementation("io.modelcontextprotocol.sdk:mcp-sdk:0.1.0")
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
