## Introduction
The central access point to all services is made available through the [AeternityService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/aeternity/impl/AeternityService.java) which can be obtained via the [AeternityServiceFactory](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/aeternity/AeternityServiceFactory.java).
Necessary parameters to f.e. identify the network to use or the endpoints to act against, are defined via the [AeternityServiceConfiguration](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/aeternity/AeternityServiceConfiguration.java).

## Initialization
The following code-snippet is an example how to initialize the `AeternityService` with a fresh generated KeyPair:

```java
KeyPairService keyPairService = new KeyPairServiceFactory().getService();
KeyPair keyPair = keyPairService.generateKeyPair();

AeternityService aeternityServiceNative =
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

## Configuration
Within the `AeternityServiceConfiguration` class, different parameters can be set:

| **Paramenter**                                    | **Description**                                                                                                           | **Default** |
| -----------                                       | -----------                                                                                                               | ----------- |
| `baseUrl`                                         | endpoint of the aeternity node                                                                                            | `https://testnet.aeternity.io` |
| `debugBaseUrl`                                    | debug endpoint of the aeternity node                                                                                      | `https://testnet.aeternity.io` |
| `compilerBaseUrl`                                 | endpoint of the Sophia http compiler                                                                                      | `https://compiler.aeternity.io` |
| `mdwBaseUrl`                                      | endpoint the aeternity middleware                                                                                         | `https://testnet.aeternity.io/mdw` |
| `network`                                         | network to run against - should be aligned with endpoint of the node                                                      | [Network](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/constants/Network.java) . `TESTNET` |
| `keyPair`                                         | the KeyPair to use for signing transactions if no KeyPair is provided explicitely                                         | - |
| `targetVM`                                        | the VM to target, since Iris only FATE is supported for new contracts                                                     | [VirtualMachine](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/constants/VirtualMachine.java) . `FATE` |
| `nativeMode`                                      | native mode builds transaction model with SDK. set to false to build via API call to node                                 | `true` |
| `debugDryRun`                                     | use debug dry-run endpoint instead of protected dry-run endpoint                                                          | `false` |
| `minimalGasPrice`                                 | default gas price to be used in contract related transactions                                                             | `1000000000` |
| `waitForTxIncludedInBlockEnabled`                 | wait for tx to be included in a block (only relevant in synchronous functions)                                            | `true` |
| `numTrialsToWaitForTxIncludedInBlock`             | number of trials to wait for a tx to be included in a block before throwing a `TransactionWaitTimeoutExpiredException`    | `60` |
| `millisBetweenTrialsToWaitForTxIncludedInBlock`   | milliseconds to wait between trials for checking a tx to be included in a block                                           | `1000` |
| `numOfConfirmations`                              | number of confirmations to wait for accepting a tx                                                                        | `10` |
| `millisBetweenTrailsToWaitForConfirmation`        | milliseconds to wait between trials for checking the confirmation                                                         | `10000` |
| `useZeroAddressAccountForDryRun`                  | use zero-address-account for dry-run                                                                                      | `true` |