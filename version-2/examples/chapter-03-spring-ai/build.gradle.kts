plugins { java; alias(libs.plugins.spring.boot) }
dependencies {
    implementation(platform(libs.boot.bom))
    implementation(platform(libs.ai.bom))
    implementation(libs.ollama)
    testImplementation(libs.boot.test)
    testRuntimeOnly(libs.junit.launcher)
}
