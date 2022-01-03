# Contracts

## Introduction
The smart contract language of the æternity blockchain is [Sophia](https://aeternity.com/aesophia). It is a functional language in the ML family, strongly typed and has restricted mutable state.

Before interacting with contracts using the SDK you should get familiar with Sophia itself first. Have a look into [aepp-sophia-examples](https://github.com/aeternity/aepp-sophia-examples) and start rapid prototyping using [AEstudio](https://studio.aepps.com).

The SDK needs to interact with following components in order to enable smart contract interactions on the æternity blockchain:

- [æternity](https://github.com/aeternity/aeternity) (host your own one or use the public testnet node at `https://testnet.aeternity.io`)
- [aesophia_http](https://github.com/aeternity/aesophia_http) (host your own one or use the public compiler at `https://compiler.aepps.com`)

Note:

- For production deployments you should ***always*** host these services by yourself.
- We highly recommend the usage of the [contraect-maven-plugin](#contraect-maven-plugin) for smart contract interaction.

## Contract interaction

For the example interaction following contract is being used:

```sophia
@compiler >= 6

include "String.aes"

contract ChatBot =

    record state = { last_name: string }

    datatype event = Greeting(string)

    entrypoint init() = { last_name = "" }

    entrypoint greet(name: string) : string =
        Chain.event(Greeting(name))
        String.concat("Hello, ", name)

    stateful entrypoint greet_and_remember(name: string) : string =
        Chain.event(Greeting(name))
        put(state{last_name=name})
        String.concat("Hello, ", name)
```

### Compile a contract
By using the [CompilerService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/compiler/CompilerService.java)
you can easily interact with the hosted Sophia http compiler and get the bytecode in return.

```java
String sourceCode = "..."; // use source code of ChatBot contract
String bytecode = aeternityService.compiler.blockingCompile(sourceCode, null)
                                           .getResult();
```

#### Handle includes
In case your contract contains custom includes (contracts/interfaces placed in other files) you need to provide a map in the `fileSystem` param with the name of the include as key and the source code of the include as value.

### Deploy a contract
When you have the bytecode you are basically ready to deploy the contract. This example contract doesn't expect any param in the `init` method.
In this case you don't have to use the `CompilerService` to encode the calldata. You can simply use a default constant for the empty calldata.

#### Convenient way

```java
ContractTxResult contractTxResult = aeternityService.transactions.blockingContractCreate(sourceCode);
String contractId = contractTxResult.getCallResult().getContractId();
```

#### Explicit way

```java
// build the tx model with all the required attributes
ContractCreateTransactionModel contractCreate =
  ContractCreateTransactionModel.builder()
      // in case the init entrypoint doesn't require a param you can simply use this constant value
      .callData(BaseConstants.CONTRACT_EMPTY_INIT_CALLDATA)
      .contractByteCode(byteCode)
      .nonce(aeternityService.accounts.blockingGetNextNonce())
      .ownerId(aeternityService.keyPairAddress)
      .build();

// by default this action will wait until the tx is included in a block)
PostTransactionResult createTxResult =
  aeternityService.transactions.blockingPostTransaction(contractCreate);

// after the tx is included you can fetch the tx-info to determine the contractId
TransactionInfoResult createTxInfoResult =
  aeternityService.info.blockingGetTransactionInfoByHash(createTxResult.getTxHash());
String contractId = createTxInfoResult.getCallInfo().getContractId();
```

### Call a contract (read-only)

#### Convenient way

```java
AeternityService readOnlyService = new AeternityServiceFactory().getService();
Object decodedResult = readOnlyService.transactions.blockingReadOnlyContractCall(
                                  contractId,
                                  "greet",
                                  sourceCode,
                                  ContractTxOptions.builder()
                                      .params(List.of(new SophiaString("kryptokrauts")))
                                      .build());
log.info(decodedResult.toString()); // "Hello, kryptokrauts"
```

#### Explicit way

```java
AeternityService readOnlyService = new AeternityServiceFactory().getService();
String callData =
    readOnlyService
        .compiler
        .blockingEncodeCalldata(
            sourceCode, "greet", Arrays.asList("\"kryptokrauts\""), null)
        .getResult();
ContractCallTransactionModel contractCall =
    ContractCallTransactionModel.builder()
        .contractId(contractId)
        .callData(callData)
        .build();

DryRunTransactionResult dryRunResult =
    readOnlyService.transactions.blockingDryRunContractCall(contractCall, true);

ObjectResultWrapper resultWrapper =
    readOnlyService.compiler.blockingDecodeCallResult(
        sourceCode,
        "greet",
        dryRunResult.getContractCallObject().getReturnType(),
        dryRunResult.getContractCallObject().getReturnValue(),
        null);

log.info(resultWrapper.getResult().toString()); // "Hello, kryptokrauts"
```

### Call a contract (stateful)

#### Convenient way
When using the convenient way by calling `blockingStatefulContractCall`
you will receive an object `ContractTxResult` which includes all important information (e.g. tx-hash, gas used, ...) about the stateful contract call.

```java
ContractTxResult contractTxResult = aeternityService.transactions
          .blockingStatefulContractCall(contractId, "greet",
              sourceCode, ContractTxOptions.builder()
                  .params(List.of(new SophiaString("kryptokrauts")))
                  .build());
```

#### Explicit way

```java
// TODO
```

### Additional topics

#### Gas estimation via dry-run
TODO

#### ContractTxOptions
TODO

#### Sophia type-mapping
TODO

## Plugins

### contraect-maven-plugin
To provide an even more convenient way to interact with smart contracts on the æternity blockchain
we developed a plugin that uses the [ACI](https://aeternity.com/aesophia/latest/aeso_aci) of contracts
written in Sophia as input to generate Java classes.
The generated classes make use of the aepp-sdk-java and provide methods to deploy contracts and call the respective entrypoint functions.

**Links**:

- https://github.com/kryptokrauts/contraect-maven-plugin
- https://github.com/kryptokrauts/contraect-showcase-maven