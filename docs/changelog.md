# Changelog

## [v1.2.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.2.0) (DRAFT, not yet released)

### General changes
- [#5](../../../issues/5) included a specific `com.google.guava` version (`27.0.1-jre`)
  - maven users shouldn't need to that in their own projects anymore
- [#24](../../../issues/24) replaced `net.consensys.cava` dependency by `org.apache.tuweni`
  - the project is now being maintained by the apache software foundation
- [#25](../../../issues/25) upgraded SDK setup to make use of æternity release 3.0.1 (`Fortuna`)
- [#27](../../../issues/27) added goggles to docker-compose setup in order to enable easy tx verification in our local setup
- [#31](../../../issues/31) we included our `PaymentSplitter.aes` contract into the test-resources and wrote an integration test to make sure our contract functionalities work
- [#32](../../../issues/32) we made some changes regarding our release-notes
  - in future you will find all information in this changelog-file
  - there won't be a specific file for a certain release anymore
- [#35](../../../issues/35) in future you will find the SDK documentation on gitbook:
  - https://kryptokrauts.gitbook.io/aepp-sdk-java/
- [#36](../../../issues/35) upgraded SDK setup to make use of æternity release 3.3.0 (`Fortuna`)
  

### New features
- [#7](../../../issues/7) AENs support
  - from now on it is possible to make use of the æternity naming system
- [#10](../../../issues/10) contract support
  - from now on it is possible to create and interact with æternity smart contracts
  - while developing the contract support we identified some problems with the RLP encoding when trying to encode a `BigInteger.ZERO`
     - in the past it wasn't possible to use a `TTL` with value `0` in any transaction type
     - this is now solved :-)
- [#28](../../../issues/28) added support for sophia compiler
  - we now provide a `CompilerServiceFactory` that allows to get an Instance of `SophiaCompilerServiceImpl`
  - this service was needed to enable creation of smart contracts with the SDK

## [v1.1.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.1.0)

### Breaking changes
- Transactions are now created through a central `TransactionFactory`
  - this refactoring breaks implementations that used older versions of the SDK

### General changes
- [#13](../../../issues/13) upgrade to new æternity release 2.0.0 (`Minerva`)

## New features
- [#6](../../../issues/6) HD wallet support (BIP44, BIP32 + BIP39)
  - it is now possible to create and recover HD wallets
- [#11](../../../issues/11) Fees (gas cost) calculation
  - æternity release 2.0.0 (`Minerva`) introduced a new fee structure
  - the SDK now provides an automated fee calculation if the user doesn't provide a fee on his/her own

## [v1.0.2](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.0.2)

### Fixes
- [#4](../../../issues/4) create a transaction on testnet
  - we used `ae_devnet` instead of `ae_uat` before
  - now we support 3 networks:
     - DEVNET (`ae_devnet`)
     - TESTNET (`ae_uat`)
     - MAINNET (`ae_mainnet`)

## [v1.0.1](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.0.1)

### General changes
- return Single instead of Observable for responses that return only one object
- switch from bouncycastle 1.61-beta to the release version (no more need to manually add it)

## [v1.0.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.0.0)

Initial release of our Java SDK to interact with the æternity blockchain.

### Initial functionalities
- generate and recover KeyPair
- generate and recover Keystore-JSON
- create and sign spendTx
- various utils (crypto, encoding, signing)