import org.beryx.jlink.PrepareMergedJarsDirTask
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

val isDebug: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.beryx.jlink") version "3.0.1"
    id("org.barfuin.gradle.taskinfo") version "2.1.0"
}

group = "com.gmail.tatsukimatsumo"
version = "1.0.0"

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.4.1")
    }
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

tasks.register<proguard.gradle.ProGuardTask>("applyProguard") {
    group = "proguard"

    injars(tasks.jar)
    outjars("${layout.buildDirectory.get()}/proguard/${project.archivesName.get()}-$version.jar")

    val javaHome = System.getProperty("java.home")
    // Automatically handle the Java version of this build.
    if (System.getProperty("java.version").startsWith("1.")) {
        // Before Java 9, the runtime classes were packaged in a single jar file.
        libraryjars("$javaHome/lib/rt.jar")
    } else {
        // As of Java 9, the runtime classes are packaged in modular jmod files.
        libraryjars(
            // filters must be specified first, as a map
            mapOf(
                "jarfilter" to "!**.jar",
                "filter" to "!module-info.class"
            ),
            "$javaHome/jmods/java.base.jmod"
        )
    }

    libraryjars(configurations.compileClasspath)

    allowaccessmodification()
    repackageclasses("a") // in module, the empty package is NG.

    printmapping("${layout.buildDirectory.get()}/proguard-mapping.txt")

    keep(
        """public final class com.gmail.tatsukimatsumo.MainKt {
             public static void main(java.lang.String[]);
        }
    """
    )
    keep("class module-info")

    doLast {
        project.sync {
            from("${layout.buildDirectory.get()}/proguard")
            into("${layout.buildDirectory.get()}/libs")
        }
    }
}

tasks.withType<PrepareMergedJarsDirTask> {
    dependsOn(tasks.named("applyProguard"))
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
