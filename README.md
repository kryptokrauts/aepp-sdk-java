# aepp-sdk-java
A community developed Java SDK to interact with the Æternity blockchain.

## Requirements

- Java 8
- Sodium library for encryption, decryption, ...
    - Mac
        - `brew install libsodium`
    - Linux
        - CentOS
            - [Enable EPEL](https://fedoraproject.org/wiki/EPEL)
            - run `yum install libsodium`
        - Ubuntu / Debian
            - `apt-get install libsodium18`
    - Windows
        - get latest release [libsodium](https://download.libsodium.org/libsodium/releases/)
        - download the latest pre-built binary (e.g. `libsodium-1.0.16-msvc.zip`)
        - extract `libsodium.dll` to `C:\Windows\System32`
- bouncycastle version 161b20 (beta and currently not available in a public maven repository)
    - copy `bcprov-jdk15on-161b20.jar` into your projects `lib` folder
    - Maven
      ```xml
     	<dependency>
          <groupId>org.bouncycastle</groupId>
          <artifactId>bcprov-jdk15on</artifactId>
          <version>161b20</version>
          <scope>system</scope>
          <systemPath>${basedir}/lib/bcprov-jdk15on-161b20.jar</systemPath>
      </dependency>
      ```
    - Gradle
      ```xml
      compile fileTree(include: ["*.jar"], dir: "lib")
      ```

## Latest stable release

- [v1.0.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.0.0)

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
    <url>https://oss.jfrog.org/artifactory/oss-snapshot-local</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.kryptokrauts</groupId>
    <artifactId>aepp-sdk-java</artifactId>
    <version>1.0.1-SNAPSHOT</version>
  </dependency>
</dependencies>
...
```

#### Gradle
```groovy
repositories {
  maven { url "https://oss.jfrog.org/artifactory/oss-snapshot-local" }
}

compile "com.kryptokrauts:aepp-sdk-java:1.0.1-SNAPSHOT"
```

## Documentation

The services of the SDK can be accessed using the factory pattern. Every service has its own factory which allows to get the service either with default config (`ae_devnet` using `https://sdk-testnet.aepps.com/v2`) or using a XServiceConfiguration builder pattern configuration object:

```java
new ChainServiceFactory().getService(); // get default configured service

new TransactionServiceFactory().getService( TransactionServiceConfiguration.configure().baseUrl( "http://localhost/v2").compile() ); //set the baseUrl to localhost
```

## Release notes

- [v1.0.0](docs/release-notes/RELEASE-NOTES-1.0.0.md)

## License

Licensed under the [ISC License](LICENSE)

## Support us

If you like this project we would appreciate your support.

- [ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV](https://explorer.aepps.com/#/account/ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV)

![ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV](donations.png)

(QR-code generated with https://cwaqrgen.com/aeternity)