plugins { java; alias(libs.plugins.spring.boot) }
dependencies {
    implementation(platform(libs.boot.bom))
    implementation(platform(libs.ai.bom))
    implementation(libs.webmvc)
    implementation(libs.validation)
    implementation(libs.actuator)
    implementation(libs.ollama)
    testImplementation(libs.boot.test)
    testImplementation(libs.webmvc.test)
    testRuntimeOnly(libs.junit.launcher)
}
