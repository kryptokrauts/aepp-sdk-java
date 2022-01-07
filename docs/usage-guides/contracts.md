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
    readOnlyService.transactions.blockingDryRunContractTx(contractCall, true);

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
          .blockingStatefulContractCall(contractId, "greet_and_remember",
              sourceCode, ContractTxOptions.builder()
                  .params(List.of(new SophiaString("kryptokrauts")))
                  .build());
```

#### Explicit way

```java
// obtain the calldata by calling the http compiler
String callData =
  aeternityService
      .compiler
      .blockingEncodeCalldata(
          chatBotSource, "greet_and_remember", Arrays.asList("\"kryptokrauts\""), null)
      .getResult();

// build the contract call tx model
ContractCallTransactionModel contractCall =
  ContractCallTransactionModel.builder()
      .callerId(aeternityService.keyPairAddress)
      .contractId(contractId)
      .callData(callData)
      .nonce(aeternityService.accounts.blockingGetNextNonce())
      .build();

/**
* optional: if you know that the default of 25000 is sufficient you don't need a dry-run at all
*/
DryRunTransactionResult dryRunResult =
  aeternityService.transactions.blockingDryRunContractTx(contractCall, false);
/**
* determine gasUsed via dry-run and add a margin to make sure the tx gets mined.
* ideally you implement this as a one-time action and monitor gas usage over time.
* the margin is not required but recommended. if the provided gasLimit is insufficient
* the tx will fail and consumed gas will be payed anyway. so you can lose funds
*/
BigInteger gasLimitWithMargin = new BigDecimal(
  dryRunResult.getContractCallObject().getGasUsed())
  .multiply(new BigDecimal(1.5f))
  .toBigInteger();
// set the gasLimitWithMargin before broadcasting the transaction
contractCall = contractCall.toBuilder().gasLimit(gasLimitWithMargin).build();

// broadcast the tx
PostTransactionResult txResult = aeternityService.transactions
  .blockingPostTransaction(contractCall);

// obtain the tx-info
TransactionInfoResult infoResult = aeternityService.info
  .blockingGetTransactionInfoByHash(txResult.getTxHash());

// decode the return value by calling the http compiler
ObjectResultWrapper resultWrapper =
  aeternityService.compiler.blockingDecodeCallResult(
      chatBotSource,
      "greet_and_remember",
      infoResult.getCallInfo().getReturnType(),
      infoResult.getCallInfo().getReturnValue(),
      null);

_logger.info(resultWrapper.getResult().toString()); // "Hello, kryptokrauts"
```

### Additional topics

#### Gas estimation via dry-run

- It is reasonable to estimate the gas consumption for a contract call using the dry-run feature of the node at least once
    and provide a specific offset (e.g. multiplied by 1.25 or 2) as default to ensure that contract calls are mined.
    Depending on the logic of the contract the gas consumption of a specific contract call can vary
    and therefore you should monitor the gas consumption and increase the default for the respective contract call
    accordingly over time.
- The default gas value of `25000` should cover all trivial stateful contract calls.
    In case transactions start running out of gas you should proceed the way described above
    and estimate the required gas using the dry-run feature.

#### ContractTxOptions
In the convenience methods you can always provide tx-options and define some or all of following attributes:

- `params` (the list of params to be passed, default: `null`)
    - check the Sophia type-mapping table below
- `amount` (the amount in ættos to be passed, default `ZERO`)
- `gasLimit` (the custom gasLimit, default: `25000`)
- `gasPrice` (the custom gasPrice to be used in the tx instead of the default `1000000000`)
- `nonce` (the custom nonce to be used in the tx, default: automatically determined by the sdk)
- `ttl` (the custom ttl, default: `ZERO`)
- `filesystem` (the includes-map for the contract, default: `null`)
    - key = include-name
    - value = source code of the include

#### Sophia type-mapping

The following mapping table indicates what Java types have to be passed for the respective Sophia entrypoint parameters.

If you use the convenience methods you only need to make sure the params reflect the required types in Sophia.
The convenience methods use the `toCompilerInput` method of [SophiaTypeTransformer](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/domain/sophia/SophiaTypeTransformer.java)
to automatically transform the Java type into the representation the Sophia compiler expects it to be.

The reverse mapping not fully covered by the SDK. You need to analyze the result object and map/handle it accordingly.
For some types there is also a reverse mapping implemented. To make use of this you have to call the `getMappedResult` method
of the SophiaTypeTransformer and provide the expected type explicitely.

If want full type support, please refer to the [contraect-maven-plugin](https://github.com/kryptokrauts/contraect-maven-plugin) which will generate a class out of your contract
with all types and methods under the hood to easily interact with your contract.

| **Sophia type** | **Java type** | **Sophia example value** |
| ----------- | ----------- | ----------- |
| address | `String` | `ak_2gx9MEFxKvY9vMG5YnqnXWv1hCsX7rgnfvBLJS4aQurustR1rt` |
| bool | `Boolean` | `true`, `false` |
| bytes(8) | `SophiaBytes` | `#fedcba9876543210` |
| Chain.ttl	| `SophiaChainTTL` | `FixedTTL(1050)`, `RelativeTTL(50)` |
| contract | `String` | `ct_Ez6MyeTMm17YnTnDdHTSrzMEBKmy7Uz2sXu347bTDPgVH2ifJ` |
| hash | `SophiaHash` | `#000102030405060708090a0b0c0d0e0f000102030405060708090a0b0c0d0e0f` |
| int | `Integer` / `Long` / `BigInteger` | `1337`, `1337` |
| list(string) | `List<SophiaString>` | `["a", "b", "c"]` |
| map(string, string) | `Map<SophiaString, SophiaString>` | `{["foo"] = "bar", ["x"] = "yz"}` |
| option(string) | `Optional<SophiaString>` | `Some("kryptokrauts")`, `None` |
| oracle('a, 'b) | `String` | `ok_2YNyxd6TRJPNrTcEDCe9ra59SVUdp9FR9qWC5msKZWYD9bP9z5` |
| oracle_query('a, 'b) | `String` | `oq_2oRvyowJuJnEkxy58Ckkw77XfWJrmRgmGaLzhdqb67SKEL1gPY` |
| signature | `SophiaSignature` | `#000102030405060708090a0b0c0d0e0f000102030405060708090a0b0c0d0e0f000102030405060708090a0b0c0d0e0f000102030405060708090a0b0c0d0e0f` |
| string | `SophiaString` | `"This is a string"` |
| tuple | `SophiaTuple` | `(42, "Foo", true)` |

## Plugins

### contraect-maven-plugin
To provide an even more convenient way to interact with smart contracts on the æternity blockchain
we developed a plugin that uses the [ACI](https://aeternity.com/aesophia/latest/aeso_aci) of contracts
written in Sophia as input to generate Java classes.
The generated classes make use of the aepp-sdk-java and provide methods to deploy contracts and call the respective entrypoint functions.

In contrast to plain SDK usage the plugin provides you type-safe param and return values.

**Links**:

- https://github.com/kryptokrauts/contraect-maven-plugin
- https://github.com/kryptokrauts/contraect-showcase-maven