plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.gmail.tatsukimatsumo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.8.0-RC2"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.withType<JavaCompile> {
    // これがないと「れさぴょん for Java」の中にあるコメント部分でコンパイルエラーになる。
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
