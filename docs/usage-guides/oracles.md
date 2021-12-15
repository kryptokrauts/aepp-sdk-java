# Oracles

## Introduction

This guide shows you how to perform all the operations that you need within the lifecycle of [oracles](https://aeternity.com/protocol/oracles) using the SDK.

## 1. Oracle: register
You register an oracle that responds with the temperature of the city that is included in the query.

```java
// TODO
```

Note:

- By default the oracle will exist for the next 500 KeyBlocks.
- If you intend to keep your oracle running longer you should increase the `oracleTtl` and/or set up a service that automatically extends the TTL before it expires.
- The `oracleId` will be similar to the address of the account that registered the Oracle.
   - The only difference is the prefix that will be `ok_` instead of `ak_`
   - This means that each account can only host 1 oracle. It's not possible to manage multiple oracles using the same account.

## 2. Some party: query an oracle and poll for response
### Query
After the oracle has been registered and as long as it isn't expired, everybody that knows the `oracleId` can query it.

```java
// TODO
```

Note:

- You should fetch (or know) the required fee for the query to the oracle.
- If you don't provide a sufficient fee the transaction is invalid.

### Poll for response
Now you have access to the query object and can poll for the response to that specific query:

```java
// TODO
```

## 3. Oracle: poll for queries and respond

### Poll for queries & respond
Typically the oracle itself polls for its own queries and responds as soon as possible:

```java
// TODO
```

Note:

- The oracle itself would probably either use an API to get the current temperature for a certain city or ideally directly communicate with measuring devices located in that specific city.
- If the oracle responds in time it will automatically get the provided query fee credited to its account.
  
## 4. Oracle: extend
As mentioned above an Oracle has a certain TTL that can be specified when registering it. You might want to extend the TTL of the oracle before it expires. You can do that as follows:

```java
// TODO
```

## Delegate signature to contract (Oracle interface)
It is possible to authorize a Sophia contract to manage an Oracle on behalf of your account. In order to achieve that you need to provide a delegation signature to the contract. The contract will then be able to use the [Oracle interface](https://aeternity.com/aesophia/latest/sophia_stdlib/#oracle) and perform Oracle related actions on behalf of your account.
This functionality could for example be used to build an AENS marketplace.

The [DelegationService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/delegation/DelegationService.java)
can be used to produce the signatures that can be used to delegate control for certain actions to a smart contract. 

Examples how to delegate signatures to a contract can be found in our [contract-maven-showcase](https://github.com/kryptokrauts/contraect-showcase-maven).