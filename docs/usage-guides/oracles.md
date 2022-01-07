# Oracles

## Introduction

This guide shows you how to perform all the operations that you need within the lifecycle of [oracles](https://aeternity.com/protocol/oracles) using the SDK.

## 1. Oracle: register
You register an oracle that responds with the temperature of the city that is included in the query.
This guide uses an example response of https://api.openweathermap.org.

```java
KeyPair oracleKeyPair = ...
BigInteger nonce = aeternityService
                            .accounts
                            .blockingGetNextNonce(oracleKeyPair.getAddress());

OracleRegisterTransactionModel oracleRegisterTx =
    OracleRegisterTransactionModel.builder()
        .accountId(oracleKeyPair.getAddress())
        .nonce(nonce)
        // lives for 5000 keyblocks if it isn't extended
        .oracleTtl(BigInteger.valueOf(5000))
        // using delta instead of fixed block height
        .oracleTtlType(OracleTTLType.DELTA)
        // fee in aettos a caller needs to pay
        .queryFee(BigInteger.valueOf(100))
        .queryFormat("string")
        .responseFormat("string")
        .build();

aeternityService.transactions.blockingPostTransaction(
    oracleRegisterTx, oracleKeyPair.getEncodedPrivateKey());
```

Note:

- By default the oracle will exist for the next 5000 KeyBlocks.
- If you intend to keep your oracle running longer you should increase the `oracleTtl` and/or set up a service that automatically extends the TTL before it expires.
- The `oracleId` will be similar to the address of the account that registered the Oracle.
   - The only difference is the prefix that will be `ok_` instead of `ak_`
   - This means that each account can only host 1 oracle. It's not possible to manage multiple oracles using the same account.

## 2. Some party: query an oracle and poll for response
### Query
After the oracle has been registered and as long as it isn't expired, everybody that knows the `oracleId` can query it.

```java
KeyPair callerKeyPair = ...
BigInteger nonce = aeternityService
                            .accounts
                            .blockingGetNextNonce(callerKeyPair.getAddress());
String oracleId = "ok_...";
String queryString = "{\"lat\":48.78,\"lon\":9.18}";

OracleQueryTransactionModel oracleQueryTx =
    OracleQueryTransactionModel.builder()
        .senderId(callerKeyPair.getAddress())
        .oracleId(oracleId)
        .nonce(nonce)
        .query(queryString)
        // fee needs to fit the fee defined by the oracle
        .queryFee(BigInteger.valueOf(100))
        // oracle can respond within the next 50 keyblocks
        .queryTtl(BigInteger.valueOf(50))
        .queryTtlType(OracleTTLType.DELTA)
        // response will be available 100 keyblocks
        // before being garbage collected together with the query
        .responseTtl(BigInteger.valueOf(100))
        .build();

aeternityService.transactions.blockingPostTransaction(
    oracleQueryTx, callerKeyPair.getEncodedPrivateKey());
```

Note:

- You should fetch (or know) the required fee for the query to the oracle.
- If you don't provide a sufficient fee the transaction is invalid.

### Poll for response
After broadcasting the OracleQueryTx you can poll for the response to that specific query like this:

```java
// using the same nonce and oracleId as like in the OracleQueryTx
String queryId = EncodingUtils.queryId(callerKeyPair.getAddress(), nonce, oracleId);
String response = null;
// wait for the response
while(response == null || response.isEmpty()) {
    OracleQueryResult oracleQueryResult =
        this.aeternityService.oracles.blockingGetOracleQuery(oracleId, queryId);
    response = oracleQueryResult.getResponse();
}
// do something with the response
```

Note:

- The `OracleQueryResult` will only contain the response property if the Oracle responded.
 
## 3. Oracle: poll for queries and respond

### Poll for queries & respond
Typically the oracle itself polls for its own queries and responds as soon as possible:

```java
// fetch oracle queries
OracleQueriesResult oracleQueriesResult =
    this.aeternityService.oracles.blockingGetOracleQueries(
        oracleKeyPair.getOracleAddress());

// typically the oracle would respond to all queries it didn't already respond to
// in this case it only responds to the first query in the list
OracleQueryResult oracleQueryResult =
    oracleQueriesResult.getQueryResults().get(0);

String responseString = "{\"coord\":{\"lon\":9.18,\"lat\":48.78},\"weather\":[{\"id\":310,\"main\":\"Drizzle\",\"description\":\"light intensity drizzle rain\",\"icon\":\"09n\"}],\"base\":\"stations\",\"main\":{\"temp\":282.56,\"pressure\":1021,\"humidity\":93,\"temp_min\":279.82,\"temp_max\":285.37},\"visibility\":7000,\"wind\":{\"speed\":4.1,\"deg\":330},\"clouds\":{\"all\":90},\"dt\":1572217099,\"sys\":{\"type\":1,\"id\":1274,\"country\":\"DE\",\"sunrise\":1572156074,\"sunset\":1572192774},\"timezone\":3600,\"id\":2825297,\"name\":\"Stuttgart\",\"cod\":200}";

BigInteger nonce = aeternityService
                            .accounts
                            .blockingGetNextNonce(oracleKeyPair.getAddress());
OracleRespondTransactionModel oracleRespondTx =
    OracleRespondTransactionModel.builder()
        .oracleId(oracleKeyPair.getOracleAddress())
        .queryId(oracleQueryResult.getId())
        .nonce(nonce)
        .response(responseString)
        .responseTtl(BigInteger.valueOf(100))
        .build();

aeternityService.transactions.blockingPostTransaction(
    oracleRespondTx, oracleKeyPair.getEncodedPrivateKey());
```

Note:

- The oracle itself would probably either use an API to get the current temperature for a certain city or ideally directly communicate with measuring devices located in that specific city.
- If the oracle responds in time it will automatically get the provided query fee credited to its account.
  
## 4. Oracle: extend
As mentioned above an Oracle has a certain TTL that can be specified when registering it. You might want to extend the TTL of the oracle before it expires. You can do that as follows:

```java
BigInteger nonce = aeternityService
                            .accounts
                            .blockingGetNextNonce(oracleKeyPair.getAddress());

OracleExtendTransactionModel oracleExtendTx =
    OracleExtendTransactionModel.builder()
        .nonce(nonce)
        .oracleId(oracleKeyPair.getOracleAddress())
        // extend the oracle ttl for another 100 keyblocks
        .relativeTtl(BigInteger.valueOf(100))
        .build();

aeternityService.transactions.blockingPostTransaction(
    oracleExtendTx, oracleKeyPair.getEncodedPrivateKey());
```

## Delegate signature to contract (Oracle interface)
It is possible to authorize a Sophia contract to manage an Oracle on behalf of your account. In order to achieve that you need to provide a delegation signature to the contract. The contract will then be able to use the [Oracle interface](https://aeternity.com/aesophia/latest/sophia_stdlib/#oracle) and perform Oracle related actions on behalf of your account.
This functionality could for example be used to build an AENS marketplace.

The [DelegationService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/delegation/DelegationService.java)
can be used to produce the signatures that can be used to delegate control for certain actions to a smart contract. 

Examples how to delegate signatures to a contract can be found in our [contract-maven-showcase](https://github.com/kryptokrauts/contraect-showcase-maven).