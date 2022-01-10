# Changelog

## [v3.0.1](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v3.0.1)

This is a minor release to address potential vulnerabilities in third party dependencies. It's highly recommended to update to this version.

## [v3.0.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v3.0.0)

This is a major release that underwent a huge refactoring. It supports the most important latest changes of aeternity. It is fully compatible with the latest Iris hardfork.

### General changes
- [#196](https://github.com/kryptokrauts/aepp-sdk-java/pull/196)
, [#218](https://github.com/kryptokrauts/aepp-sdk-java/pull/218)
, [#219](https://github.com/kryptokrauts/aepp-sdk-java/pull/219)
, [#220](https://github.com/kryptokrauts/aepp-sdk-java/pull/220)
, [#221](https://github.com/kryptokrauts/aepp-sdk-java/pull/221)
, [#222](https://github.com/kryptokrauts/aepp-sdk-java/pull/222)
, [#223](https://github.com/kryptokrauts/aepp-sdk-java/pull/223)
 SDK documentation via MkDocs including versioning with Mike
- [#217](https://github.com/kryptokrauts/aepp-sdk-java/pull/217) Improved Javadocs
- Updated dependencies

### Breaking changes
- [#144](https://github.com/kryptokrauts/aepp-sdk-java/pull/144) Revisited handling of keypairs:
    - Introduced `KeyPair` class, dropped `BaseKeyPair` class
    - Introduced `HdWallet` and `HdKeyPair`. These changes include a fix for a bug in the key derivation that derived wrong keypairs.
- [#177](https://github.com/kryptokrauts/aepp-sdk-java/pull/177) Discarded Java 8 in favor of Java 15+
- [#183](https://github.com/kryptokrauts/aepp-sdk-java/pull/183) Middleware: dropped `indaex` (formerly `aeternal`) in favor of `mdw`
- [#206](https://github.com/kryptokrauts/aepp-sdk-java/pull/206) Renamed `gas` to `gasLimit` in all transaction model classes

### New features
- [#155](https://github.com/kryptokrauts/aepp-sdk-java/pull/155) New transaction type `PayingForTx`
- [#157](https://github.com/kryptokrauts/aepp-sdk-java/pull/157) Arbitrary pointer-keys for AENS pointers
- [#159](https://github.com/kryptokrauts/aepp-sdk-java/pull/159) External (protected) dry-run endpoint
- [#171](https://github.com/kryptokrauts/aepp-sdk-java/pull/171) Convenient methods to create delegation signatures
- [#184](https://github.com/kryptokrauts/aepp-sdk-java/pull/184) Retrieve next nonce with new API endpoint that provides different strategies
- [#188](https://github.com/kryptokrauts/aepp-sdk-java/pull/188) Force `deposit` to be `ZERO` in ContractCreateTx
- [#192](https://github.com/kryptokrauts/aepp-sdk-java/pull/192) New method `computeGAInnerTxHash` for usage of Generalized Accounts
- [#200](https://github.com/kryptokrauts/aepp-sdk-java/pull/200) Limit max amount of pointers to 32. Introduce constant `AENS.MAX_TTL`.
- [#201](https://github.com/kryptokrauts/aepp-sdk-java/pull/201) Support for `NameTransferTx`
- [#211](https://github.com/kryptokrauts/aepp-sdk-java/pull/211) Convenient methods for contract related transaction types to easily:
    - deploy a contract
    - perform a stateful contract call
    - perform a read-only dry-run call
    
### Refactoring
- [#176](https://github.com/kryptokrauts/aepp-sdk-java/pull/176) OpenAPI (OAS3) API (`/v3`) in favor of old swagger API (`/v2`)
- Introduced default values in the model classes of each tx-type

## [v2.2.1](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.2.1)

This release ships an enhancement.

### Enhancements
- [#116](https://github.com/kryptokrauts/aepp-sdk-java/issues/116) support includes for compiling, deploying and calling contracts
    - we identified this was missing when developing the [contraect-maven-plugin](https://github.com/kryptokrauts/contraect-maven-plugin)

## [v2.2.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.2.0)

This release ships some fixes and enhancements.

### Fixes
- [#91](https://github.com/kryptokrauts/aepp-sdk-java/issues/91) show exception details on compiler and aeternal errors
- [#95](https://github.com/kryptokrauts/aepp-sdk-java/issues/95) fix default values in BaseConstants

### Enhancements
- [#96](https://github.com/kryptokrauts/aepp-sdk-java/issues/96) add ResultWrapper for standard java types returned from service calls to omit exceptions being ignored
- [#97](https://github.com/kryptokrauts/aepp-sdk-java/issues/97) introduce configurable way to wait for tx being included in a block
    - this is currently covered by the following properties and only relevant in `blockingPostTransaction` calls
        - `waitForTxIncludedInBlockEnabled` (default=true)
        - `numTrialsToWaitForTxIncludedInBlock` (default=60)
        - `millisBetweenTrialsToWaitForTxIncludedInBlock` (default=1000)
- [#98](https://github.com/kryptokrauts/aepp-sdk-java/issues/98) provide a (more) user-friendly way to handle unit conversions
    - we introduced a `UnitConversionService`-Interface with a `DefaultUnitConversionService`-Implementation that makes it a bit easier to handle conversions from `AE` to `aettos` or custom tokens that may have less decimals
- [#99](https://github.com/kryptokrauts/aepp-sdk-java/issues/99) prevent exceptions for TxModel classes
- [#107](https://github.com/kryptokrauts/aepp-sdk-java/issues/107) wait for confirmation of transaction
    - now it is possible to wait for a transaction to be confirmed
    - this is an asynchronous operation and can be configured through the following properties:
        - `numOfConfirmations` (default=10)
            - the number of confirmations (KeyBlocks) until a transaction is considered confirmed
            - this value can also be explicitly set as method parameter
        - `millisBetweenTrailsToWaitForConfirmation` (default=10000)
- [#111](https://github.com/kryptokrauts/aepp-sdk-java/issues/111) add "payloadDecoded" attribute to SpendTransactionModel
    - now the payload for a SpendTx is automatically decoded

## [v2.1.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.1.0)

This release ships some fixes and enhancements. Additionally we renamed some attributes and model-classes. If you already used [v2.0.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.0.0) it might be needed to fix these changes.

### Refactoring
- [#86](https://github.com/kryptokrauts/aepp-sdk-java/issues/86) add "payable" attribute to AccountResult-model

### Fixes
- [#85](https://github.com/kryptokrauts/aepp-sdk-java/issues/85) AeternalService: fix case-sensitive comparison of domains
- [#88](https://github.com/kryptokrauts/aepp-sdk-java/issues/88) replace Optional.orElse with Optional.orElseGet in the AccountServiceImpl

### Enhancements
- [#84](https://github.com/kryptokrauts/aepp-sdk-java/issues/84) add missing ÆNS related AeternalService functionalities
    - we added the possibility to query for active names
   and search for a name (which allows to receive e.g. the owner of a name)
- [#87](https://github.com/kryptokrauts/aepp-sdk-java/issues/87) add support to receive byteCode for given contractId

## [v2.0.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v2.0.0)

### Breaking changes and new features
- [#77](https://github.com/kryptokrauts/aepp-sdk-java/issues/77) integrate middleware `aeternal` and allow to query ÆNS auction related information
- [#72](https://github.com/kryptokrauts/aepp-sdk-java/issues/72) add method to post transactions with a custom private key 
- [#70](https://github.com/kryptokrauts/aepp-sdk-java/issues/70) adapt TLD changes (`.chain` instead of `.aet`)
- [#68](https://github.com/kryptokrauts/aepp-sdk-java/issues/68) refactoring: rename contractBaseUrl to compilerBaseUrl
- [#65](https://github.com/kryptokrauts/aepp-sdk-java/issues/65) refactoring: allow posting of signed transactions (string)
- [#64](https://github.com/kryptokrauts/aepp-sdk-java/issues/64) ÆNS: auction-related functionalities
    - calculate the next minimum fee for a running auction
- [#63](https://github.com/kryptokrauts/aepp-sdk-java/issues/63) refactoring: dryRun actions
- [#56](https://github.com/kryptokrauts/aepp-sdk-java/issues/56) Lima related changes:
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
- [#44](https://github.com/kryptokrauts/aepp-sdk-java/issues/44) major refactoring:
    - introduced model classes to be independent from the swagger-generated classes
    - discarded the `TransactionFactory` -> for each transaction-type a model class was introduced which can be created following the builder-pattern
    - introduced the `AeternityService` which serves as entrypoint to access other services and needs to be instantiated through the `AeternityServiceFactory` by passing the `AeternityServiceConfiguration` 

### General changes
- [#60](https://github.com/kryptokrauts/aepp-sdk-java/issues/60) update tuweni to stable release version
- [#57](https://github.com/kryptokrauts/aepp-sdk-java/issues/57) support contracts and oracles as ÆNS pointers
- [#49](https://github.com/kryptokrauts/aepp-sdk-java/issues/49) provide guidelines for contributors
- [#45](https://github.com/kryptokrauts/aepp-sdk-java/issues/45) update of node (`v4.1.0`) and compiler (`v3.2.0`)

## [v1.2.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.2.0)

### General changes
- [#5](https://github.com/kryptokrauts/aepp-sdk-java/issues/5) included a specific `com.google.guava` version (`27.0.1-jre`)
    - maven users shouldn't need to that in their own projects anymore
- [#24](https://github.com/kryptokrauts/aepp-sdk-java/issues/24) replaced `net.consensys.cava` dependency by `org.apache.tuweni`
    - the project is now being maintained by the apache software foundation
- [#25](https://github.com/kryptokrauts/aepp-sdk-java/issues/25) upgraded SDK setup to make use of æternity release 3.0.1 (`Fortuna`)
- [#27](https://github.com/kryptokrauts/aepp-sdk-java/issues/27) added goggles to docker-compose setup in order to enable easy tx verification in our local setup
- [#31](https://github.com/kryptokrauts/aepp-sdk-java/issues/31) we included our `PaymentSplitter.aes` contract into the test-resources and wrote an integration test to make sure our contract functionalities work
- [#32](https://github.com/kryptokrauts/aepp-sdk-java/issues/32) we made some changes regarding our release-notes
    - in future you will find all information in this changelog-file
    - there won't be a specific file for a certain release anymore
- [#35](https://github.com/kryptokrauts/aepp-sdk-java/issues/35) in future you will find the SDK documentation on gitbook:
    - https://kryptokrauts.gitbook.io/aepp-sdk-java/
- [#36](https://github.com/kryptokrauts/aepp-sdk-java/issues/36) upgraded SDK setup to make use of æternity release 3.3.0 (`Fortuna`)  

### New features
- [#7](https://github.com/kryptokrauts/aepp-sdk-java/issues/7) AENs support
    - from now on it is possible to make use of the æternity naming system
- [#10](https://github.com/kryptokrauts/aepp-sdk-java/issues/10) contract support
    - from now on it is possible to create and interact with æternity smart contracts
    - while developing the contract support we identified some problems with the RLP encoding when trying to encode a `BigInteger.ZERO`
        - in the past it wasn't possible to use a `TTL` with value `0` in any transaction type
        - this is now solved :-)
- [#28](https://github.com/kryptokrauts/aepp-sdk-java/issues/28) added support for sophia compiler
    - we now provide a `CompilerServiceFactory` that allows to get an Instance of `SophiaCompilerServiceImpl`
    - this service was needed to enable creation of smart contracts with the SDK
- [#40](https://github.com/kryptokrauts/aepp-sdk-java/issues/40) added service method to generate ACI for Smart Contracts

## [v1.1.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.1.0)

### Breaking changes
- Transactions are now created through a central `TransactionFactory`
    - this refactoring breaks implementations that used older versions of the SDK

### General changes
- [#13](https://github.com/kryptokrauts/aepp-sdk-java/issues/13) upgrade to new æternity release 2.0.0 (`Minerva`)

## New features
- [#6](https://github.com/kryptokrauts/aepp-sdk-java/issues/6) HD wallet support (BIP44, BIP32 + BIP39)
    - it is now possible to create and recover HD wallets
- [#11](https://github.com/kryptokrauts/aepp-sdk-java/issues/11) Fees (gas cost) calculation
    - æternity release 2.0.0 (`Minerva`) introduced a new fee structure
    - the SDK now provides an automated fee calculation if the user doesn't provide a fee on his/her own

## [v1.0.2](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.0.2)

### Fixes
- [#4](https://github.com/kryptokrauts/aepp-sdk-java/issues/4) create a transaction on testnet
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