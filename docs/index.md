# Getting started

The aepp-sdk-java is a community developed Java SDK to interact with the [æternity blockchain](https://aeternity.com) powered by [kryptokrauts.com](https://kryptokrauts.com)!

## System requirements

- Java 15
- Sodium library
    - Mac
        - `brew install libsodium`
    - Linux
        - CentOS
            - [Enable EPEL](https://fedoraproject.org/wiki/EPEL)
            - `run yum install libsodium`
        - Ubuntu / Debian
            - `apt-get install libsodium18`
    - Windows
        - get latest release [libsodium](https://download.libsodium.org/libsodium/releases/)
        - download the latest pre-built binary (e.g. `libsodium-1.0.18-msvc.zip`)
        - extract `libsodium.dll` to `C:\Windows\System32`

## Include dependency

### Release
The latest release always reflects the state of the [master](https://github.com/kryptokrauts/aepp-sdk-java/tree/master) branch.

#### Maven
```xml
<dependency>
    <groupId>com.kryptokrauts</groupId>
    <artifactId>aepp-sdk-java</artifactId>
    <version>3.0.1</version>
</dependency>
```

#### Gradle
```gradle
compile "com.kryptokrauts:aepp-sdk-java:3.0.1"
```

### Snapshot
The latest snapshot always reflects the state of the [3.x](https://github.com/kryptokrauts/aepp-sdk-java/tree/3.x) branch.
It is published with the version defined in `gradle.properties`.

#### Maven
```xml
<repositories>
    <repository>
        <id>maven-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.kryptokrauts</groupId>
    <artifactId>aepp-sdk-java</artifactId>
    <version>3.0.2-SNAPSHOT</version>
  </dependency>
</dependencies>
```

#### Gradle
```gradle
repositories {
  jcenter()
  maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

compile "com.kryptokrauts:aepp-sdk-java:3.0.2-SNAPSHOT"
```