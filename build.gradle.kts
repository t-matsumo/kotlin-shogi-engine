import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

val isDebug: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.beryx.jlink") version "3.0.1"
}

group = "com.gmail.tatsukimatsumo"
version = "1.0.0"

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

    if (isDebug != "true") {
        options.compilerArgs.add("-g:none")
        options.compilerArgs.add("-Xpkginfo:nonempty")
        options.isDebug = false
    }
}

tasks.withType<JavaExec> {
    // IntelliJ IDEAでmain関数から実行するとjavaコマンドに--patch-moduleを良い感じに付与できず、
    // パッケージが見つからないと怒られるのでGradle runで実行する。
    // その際、標準入力の設定をしないと例外が投げられる。
    standardInput = System.`in`
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        progressiveMode = true
        languageVersion = KotlinVersion.KOTLIN_1_9
        allWarningsAsErrors = true
    }
}

application {
    // jlinkで起動バッチのMainクラスを指定するのに必要
    mainClass = "com.gmail.tatsukimatsumo.MainKt"
    mainModule = "shogiai.main"
}

jlink {
    addOptions(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages"
    )
}
