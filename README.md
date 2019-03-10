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

## Latest stable release

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
    <version>1.1.0</version>
</dependency>
...
```

### Gradle

```groovy
repositories {
  jcenter()
}

compile "com.kryptokrauts:aepp-sdk-java:1.1.0"
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
    <id>jcenter</id>
    <url>https://jcenter.bintray.com/</url>
  </repository>
  <repository>
    <id>oss-snapshot-local</id>
    <url>https://oss.jfrog.org/artifactory/oss-snapshot-local</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.kryptokrauts</groupId>
    <artifactId>aepp-sdk-java</artifactId>
    <version>1.1.1-SNAPSHOT</version>
  </dependency>
</dependencies>
...
```

#### Gradle
```groovy
repositories {
  jcenter()
  maven { url "https://oss.jfrog.org/artifactory/oss-snapshot-local" }
}

compile "com.kryptokrauts:aepp-sdk-java:1.1.1-SNAPSHOT"
```

### Release notes

- [v1.1.0](docs/release-notes/RELEASE-NOTES-1.1.0.md)
- [v1.0.2](docs/release-notes/RELEASE-NOTES-1.0.2.md)
- [v1.0.1](docs/release-notes/RELEASE-NOTES-1.0.1.md)
- [v1.0.0](docs/release-notes/RELEASE-NOTES-1.0.0.md)

## Documentation

The services of the SDK can be accessed using the factory pattern. Every service has its own factory which allows to get the service either with default config (`ae_uat` using `https://sdk-testnet.aepps.com/v2`) or using a XServiceConfiguration builder pattern configuration object:

```java
new ChainServiceFactory().getService(); // get default configured service

new TransactionServiceFactory().getService( TransactionServiceConfiguration.configure().baseUrl( "http://localhost/v2").compile() ); //set the baseUrl to localhost
```

Transactions of any type are created using a factory residing in the transaction service. This provides a uniform way of creating and abstracts the setting of some necessary parameters, f.e. like the fee calculation model, which must be transparent. A new transaction is created in two steps:

```java
/* 
 * Step 1: create transaction object of desired type 
 */
transactionService = ... 				// resolve TxService like above
AbstractTransaction<?> spendTx =			// abstract supertype of tx
        transactionService						
            .getTransactionFactory()			// get the factory and create desired tx type
            .createSpendTransaction(sender, recipient, amount, payload, null, ttl, nonce);

/* 
 * Step 2: create unsigned transaction object 
 * especially a this point, the automated fee calculation will take place, 
 * depending on the actual transaction type 
 */
UnsignedTx unsignedTx =
        transactionService.createUnsignedTransaction(spendTx).toFuture().get();
```

### Example code to generate and post a transaction
```java
// secret needed to recover KeyPair and sign tx
final String testSecret = "<your_private_key>";

// the KeyPairService doesn't need specific configuration parameters
final KeyPairService keyPairService = new KeyPairServiceFactory().getService();
BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(testSecret);

final String baseUrl = "https://sdk-testnet.aepps.com/v2"; // default: https://sdk-testnet.aepps.com/v2
final Network testnet = Network.TESTNET; // default: TESTNET -> ae_uat

// get services with required configuration
// you can also call getService() which will load the default settings (see above)
ServiceConfiguration serviceConf = ServiceConfiguration.configure().baseUrl(baseUrl).compile();
final AccountService accountService = new AccountServiceFactory().getService(serviceConf);
final ChainService chainService = new ChainServiceFactory().getService(serviceConf);
// the TransactionService needs to know the network because the signature of tx is handled differently
final TransactionService transactionService = new TransactionServiceFactory().getService(TransactionServiceConfiguration.configure().baseUrl(baseUrl).network(testnet).compile());

final String toAddress = "<recipient_address>";

// get block to determine current height for calculation of TTL
KeyBlock block = chainService.getCurrentKeyBlock().blockingGet();
// get the current account to determine nonce which has to be increased
Account account = accountService.getAccount( keyPair.getPublicKey() ).blockingGet();

// amount to send -> (in future we will provide utils to calculate æternity units)
BigInteger amount = BigInteger.valueOf( 1 );
// some payload included within tx
String payload = "works =)";
// self defined fee is optional. if you provide null as fee our implementation will automatically calculate the fee
BigInteger fee = BigInteger.valueOf( <SELF_DEFINED_FEE> );
// tx will be valid for the next ten blocks
BigInteger ttl = block.getHeight().add(BigInteger.TEN);
// we need to increase the current account nonce by one
BigInteger nonce = account.getNonce().add( BigInteger.ONE );

// create the tx (with self defined fee)
AbstractTransaction<?> spendTxWithSelfDefinedFee =
        transactionServiceNative
                .getTransactionFactory()
                .createSpendTransaction(
                        keyPair.getPublicKey(), recipient, amount, payload, fee, ttl, nonce);
// create the tx (with calculated fee)
AbstractTransaction<?> spendTxWithCalculatedFee =
        transactionServiceNative
                .getTransactionFactory()
                .createSpendTransaction(
                        keyPair.getPublicKey(), recipient, amount, payload, null, ttl, nonce);

// choose one of the spendTx above to create the UnsignedTx-object
UnsignedTx unsignedTx =
        transactionServiceNative.createUnsignedTransaction(spendTxWithCalculatedFee).toFuture().get();

// sign the tx
Tx signedTx =
        transactionServiceNative.signTransaction(unsignedTx, keyPair.getPrivateKey());

// hopefully you receive a successful txResponse
PostTxResponse txResponse = transactionService.postTransaction( signedTx ).blockingGet();
```
### Example code to generate a HD wallet
The implementation of HD wallets is based on [bitcoinj](https://github.com/bitcoinj/bitcoinj)

Although possible, it's not recommended to create the HD wallet based on a user choosen list of mnemonic words, because this will lack randomicity. Additionally it's strongly recommended to set password, which additionally salts the mnemonic phrase and increases security.
All derived keys should be created with the hardened flag. Otherwise it is possible to reconstruct all descendent private and public keys from a known private key and all descendent public keys from a known public key. 

```java
final KeyPairService keyPairService = new KeyPairServiceFactory().getService();

// create the master
MnemonicKeyPair generatedKeyPair =
                    keyPairService.generateMasterMnemonicKeyPair("superSafeRandomSaltPassword");
// get the mnemonics
master.getMnemonicSeedWords()

// derive a key                    
BaseKeyPair generatedDerivedKey =
                            EncodingUtils.createBaseKeyPair(
                                keyPairService.generateDerivedKey(master, true).toRawKeyPair());
```

## License

Licensed under the [ISC License](LICENSE)

## Support us

If you like this project we would appreciate your support.

- [ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV](https://explorer.aepps.com/#/account/ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV)

![ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV](donations.png)

(QR-code generated with https://cwaqrgen.com/aeternity)