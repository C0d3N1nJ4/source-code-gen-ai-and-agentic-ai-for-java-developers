plugins {
    alias(libs.plugins.spring.boot) apply false
}
subprojects {
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        }
        tasks.withType<Test>().configureEach { useJUnitPlatform() }
    }
}
