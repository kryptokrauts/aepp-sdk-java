# Changelog

## [v2.2.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.2.0)

This release ships some fixes and enhancements.

### Fixes
- [#91](../../../issues/91) show exception details on compiler and aeternal errors
- [#95](../../../issues/95) fix default values in BaseConstants

### Enhancements
- [#96](../../../issues/96) add ResultWrapper for standard java types returned from service calls to omit exceptions being ignored
- [#97](../../../issues/97) introduce configurable way to wait for tx being included in a block
  - this is currently covered by the following properties and only relevant in `blockingPostTransaction` calls
    - `waitForTxIncludedInBlockEnabled` (default=true)
    - `numTrialsToWaitForTxIncludedInBlock` (default=60)
    - `millisBetweenTrialsToWaitForTxIncludedInBlock` (default=1000)
  - there is an [open issue](../../../issues/107) that will cover the functionality to wait an amount of confirmations (keyblocks) to consider a transaction being mined
- [#98](../../../issues/98) provide a (more) user-friendly way to handle unit conversions
  - we introduced a `UnitConversionService`-Interface with a `DefaultUnitConversionService`-Implementation that makes it a bit easier to handle conversions from `AE` to `aettos` or custom tokens that may have less decimals
- [#99](../../../issues/99) prevent exceptions for TxModel classes

## [v2.1.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.1.0)

This release ships some fixes and enhancements. Additionally we renamed some attributes and model-classes. If you already used [v2.0.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.0.0) it might be needed to fix these changes.

### Refactoring
- [#86](../../../issues/86) add "payable" attribute to AccountResult-model

### Fixes
- [#85](../../../issues/85) AeternalService: fix case-sensitive comparison of domains
- [#88](../../../issues/88) replace Optional.orElse with Optional.orElseGet in the AccountServiceImpl

### Enhancements
- [#84](../../../issues/84) add missing ÆNS related AeternalService functionalities
   - we added the possibility to query for active names
   and search for a name (which allows to receive e.g. the owner of a name)
- [#87](../../../issues/87) add support to receive byteCode for given contractId

## [v2.0.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.0.0)

### Breaking changes and new features
- [#77](../../../issues/77) integrate middleware `aeternal` and allow to query ÆNS auction related information
- [#72](../../../issues/72) add method to post transactions with a custom private key 
- [#70](../../../issues/70) adapt TLD changes (`.chain` instead of `.aet`)
- [#68](../../../issues/68) refactoring: rename contractBaseUrl to compilerBaseUrl
- [#65](../../../issues/65) refactoring: allow posting of signed transactions (string)
- [#64](../../../issues/64) ÆNS: auction-related functionalities
  - calculate the next minimum fee for a running auction
- [#63](../../../issues/63) refactoring: dryRun actions
- [#56](../../../issues/56) Lima related changes:
  - update to new node version `v5.x.x`
  - ÆNS auctions:
     - TLD: `.aet` instead of `.test`
     - `nameFee` calculation
     - adapt changes in generation of `commitmentId` and `nameId`
     - update version of `NameClaimTx`
  - update to new compiler version `v4.x.x`:
     - adapt sophia contracts to compile with new version
  - FATE-VM:
     - make the compiler-backend configurable (`AEVM` / `FATE`)
     - introduce `VirtualMachine` enum and add it to the `ServiceConfiguration` so that the services in the SDK use the correct `vmVersion` and `abiVersion` combination and the correct `backend` in the compiler
- [#44](../../../issues/44) major refactoring:
  - introduced model classes to be independent from the swagger-generated classes
  - discarded the `TransactionFactory` -> for each transaction-type a model class was introduced which can be created following the builder-pattern
  - introduced the `AeternityService` which serves as entrypoint to access other services and needs to be instantiated through the `AeternityServiceFactory` by passing the `AeternityServiceConfiguration` 

### General changes
- [#60](../../../issues/60) update tuweni to stable release version
- [#57](../../../issues/57) support contracts and oracles as ÆNS pointers
- [#49](../../../issues/49) provide guidelines for contributors
- [#45](../../../issues/45) update of node (`v4.1.0`) and compiler (`v3.2.0`)

## [v1.2.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.2.0)

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
- [#36](../../../issues/36) upgraded SDK setup to make use of æternity release 3.3.0 (`Fortuna`)  

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
- [#40](../../../issues/40) added service method to generate ACI for Smart Contracts

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