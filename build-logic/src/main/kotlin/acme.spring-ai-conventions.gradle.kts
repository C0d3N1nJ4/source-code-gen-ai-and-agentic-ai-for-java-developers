import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("acme.java-conventions")
    id("org.springframework.boot")
}
val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    implementation(platform(catalog.findLibrary("boot-bom").get()))
    implementation(platform(catalog.findLibrary("ai-bom").get()))
    testImplementation(catalog.findLibrary("boot-test").get())
    testRuntimeOnly(catalog.findLibrary("junit-launcher").get())
}
