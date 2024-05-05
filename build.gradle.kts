plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.21"
  id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.vanjor"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

kotlin {
  jvmToolchain(17)
}

intellij {
  version.set("2023.3.5")
  type.set("IC")
  //plugins.set(listOf("android"))
  plugins = listOf("org.jetbrains.kotlin")
}

tasks {
  // Set the JVM compatibility versions
  compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  compileKotlin  {
    kotlinOptions.jvmTarget = "17"
  }

  runIde {
    ideDir.set(file("C:/Program Files/Android/Android Studio/"))
  }

  patchPluginXml {
    sinceBuild.set("231")
    untilBuild.set("241.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
