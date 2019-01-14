# aepp-sdk-java for Java/Kotlin/Scala
A community developed Java SDK to interact with the Æternity blockchain

## Requirements

- Java 8

## Latest stable release (*not released yet*)

### Download

 [ ![Download](https://api.bintray.com/packages/kryptokrauts/maven/aepp-sdk-java/images/download.svg) ](https://bintray.com/kryptokrauts/maven/aepp-sdk-java/_latestVersion)

### Maven

```xml
...
<repositories>
  <repository>
    <id>jcenter</id>
    <url>https://jcenter.bintray.com/</url>
  </repository>
</repositories>

<dependency>
    <groupId>com.kryptokrauts</groupId>
    <artifactId>aepp-sdk-java</artifactId>
    <version>X.X.X</version>
</dependency>
...
```

### Gradle

```groovy
repositories {
  jcenter()
}

compile "com.kryptokrauts:aepp-sdk-java:X.X.X"
```

### Snapshots

You can access the latest snapshot by adding "-SNAPSHOT" to the version number and
adding the repository `https://oss.jfrog.org/artifactory/oss-snapshot-local`
to your build.

You can also reference a specific snapshot.
Here's the [list of snapshot versions](https://oss.jfrog.org/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/com/kryptokrauts/aepp-sdk-java).

#### Maven
```xml
...
<repositories>
  <repository>
    <id>oss-snapshot-local</id>
    <url>https://oss.jfrog.org/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/com/kryptokrauts/aepp-sdk-java</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.kryptokrauts</groupId>
    <artifactId>aepp-sdk-java</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
</dependencies>
...
```

#### Gradle
```groovy
repositories {
  maven { url "https://oss.jfrog.org/artifactory/oss-snapshot-local" }
}

compile "com.kryptokrauts:aepp-sdk-java:0.0.1-SNAPSHOT"
```

## Documentation

*TODO*

## Release notes

*no release published*

## License

Licensed under the [ISC License](LICENSE)