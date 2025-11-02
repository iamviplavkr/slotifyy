// Root-level build.gradle.kts

plugins {
    // Define versions here ONCE for the whole project
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
