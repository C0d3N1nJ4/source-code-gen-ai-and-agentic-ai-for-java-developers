plugins { id("acme.spring-ai-conventions") }
dependencies {
    implementation(libs.webmvc)
    implementation(libs.validation)
    implementation(libs.actuator)
    implementation(libs.ollama)
    testImplementation(libs.webmvc.test)
}
