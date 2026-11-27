plugins { id("acme.spring-ai-conventions") }
dependencies {
    implementation(libs.webmvc)
    implementation(libs.validation)
    implementation(libs.actuator)
    implementation(libs.ollama)
    implementation(libs.openai)
    testImplementation(libs.webmvc.test)
    testImplementation(libs.reactor.test)
}
tasks.test {
    systemProperty("acme.live", providers.gradleProperty("liveModel").orElse("false").get())
}
