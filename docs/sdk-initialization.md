## Introduction
The central access point to all services is made available through the [AeternityService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/aeternity/impl/AeternityService.java) which can be obtained via the [AeternityServiceFactory](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/aeternity/AeternityServiceFactory.java).
Necessary parameters to for example identify the network to use or the endpoints to act against, are defined via the [AeternityServiceConfiguration](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/aeternity/AeternityServiceConfiguration.java).

Of course it is also possible to initialize each Service (such as [AccountService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/account/AccountService.java) or [TransactionService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/transaction/TransactionService.java)) independently by providing the required `AeternityServiceConfiguration`.

## Initialize `AeternityService`
The following code-snippet is an example how to initialize the `AeternityService` with a KeyPair recovered from a known private key:

```java
KeyPairService keyPairService = new KeyPairServiceFactory().getService();
KeyPair keyPair = keyPairService.recoverKeyPair(<privateKey>);

AeternityService aeternityService =
        new AeternityServiceFactory()
            .getService(
                AeternityServiceConfiguration.configure()
                    .baseUrl(<aeternityBaseUrl>)
                    .compilerBaseUrl(<compilerBaseUrl>)
                    .mdwBaseUrl(<mdwBaseUrl>)
                    .network(Network.<network>)
                    .keyPair(keyPair)
                    .compile());
```

In many transaction models that you will build for the different transaction types that æternity provides you will need
to define the id of the sender or the account that performs the transaction.
You can access the address of the KeyPair configured for the AeternityService easily by calling `aeternityService.keyPairAddress`.

## Service Configuration
Within the `AeternityServiceConfiguration` class, different parameters can be set:

| **Paramenter**                                    | **Description**                                                                                                                   | **Default** |
| -----------                                       | -----------                                                                                                                       | ----------- |
| `baseUrl`                                         | endpoint of the aeternity node                                                                                                    | `https://testnet.aeternity.io` |
| `compilerBaseUrl`                                 | endpoint of the Sophia http compiler                                                                                              | `https://compiler.aeternity.io` |
| `debugBaseUrl`                                    | debug endpoint of the aeternity node                                                                                              | `https://testnet.aeternity.io` |
| `debugDryRun`                                     | use debug dry-run endpoint instead of protected dry-run endpoint                                                                  | `false` |
| `defaultGasPrice`                                 | default gas price to be used in contract related transactions                                                                     | `1000000000` |
| `dryRunGasReserveMargin`                          | the reserve margin to use for gasLimit in stateful contract calls (only relevant in the convenience method if dry-run is active)  | `1.25f` |
| `dryRunStatefulCalls`                             | perform a dry-run for stateful contract calls by default (only relevant in the convenience method)                                | `true` |
| `keyPair`                                         | the KeyPair to use for signing transactions if no KeyPair is provided explicitly                                                  | - |
| `mdwBaseUrl`                                      | endpoint the aeternity middleware                                                                                                 | `https://testnet.aeternity.io/mdw` |
| `millisBetweenTrailsToWaitForConfirmation`        | milliseconds to wait between trials for checking the confirmation                                                                 | `10000L` |
| `millisBetweenTrialsToWaitForTxIncludedInBlock`   | milliseconds to wait between trials for checking a tx to be included in a block                                                   | `1000L` |
| `nativeMode`                                      | native mode builds transaction models with SDK. set to false to build via API call to node                                        | `true` |
| `network`                                         | network to run against - should be aligned with endpoint of the node                                                              | [Network](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/constants/Network.java) . `TESTNET` |
| `numOfConfirmations`                              | number of confirmations to wait for accepting a tx (relevance dependent on `waitForTxIncludedInBlockEnabled`)                     | `10` |
| `numTrialsToWaitForTxIncludedInBlock`             | number of trials to wait for a tx to be included in a block (relevance dependent on `waitForTxIncludedInBlockEnabled`)            | `60` |
| `targetVM`                                        | the VM to target, since Iris only FATE is supported for new contracts                                                             | [VirtualMachine](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/constants/VirtualMachine.java) . `FATE` |
| `useZeroAddressAccountForDryRun`                  | use zero-address-account for dry-run                                                                                              | `true` |
| `waitForTxIncludedInBlockEnabled`                 | wait for tx to be included in a block (only relevant in synchronous functions)                                                    | `true` |