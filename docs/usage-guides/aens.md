# æternity naming system

## Introduction
This guide shows you how to perform all the operations that you need within the lifecycle of [æternity naming system (AENS)](https://aeternity.com/protocol/AENS.html) using the SDK. The examples require you to have an initialized instance of `AeternityService`, see [SDK initialization](../sdk-initialization.md).

If you successfully claimed a name it will expire after 180000 keyblocks (~375 days). You will need to update your name before it expires!

## 1. Claim a name
Claiming an AENS name requires you (at least) 2 transactions:

- First you need to perform a `pre-claim` by providing a `commitmentId` (hash).
    - The `commitmentId` is calculated with a random `salt` and the provided name. The SDK automatically generates the random `salt`, calculates the `commitmentId` and includes it into the `NamePreclaimTx`.
- After the `NamePreclaimTx` transaction has been mined you will be able to perform the actual `claim` of the name. When performing the actual `claim` via a `NameClaimTx` you will, depending on the length of the name:
    - immediately become owner of that name
    - initiate an auction
    
### Pre-claim

```java
BigInteger salt = CryptoUtils.generateNamespaceSalt();

NamePreclaimTransactionModel preClaimTx =
  NamePreclaimTransactionModel.builder()
      .accountId(aeternityService.keyPairAddress)
      .name("userguide.chain")
      .salt(salt)
      .nonce(aeternityService.accounts.blockingGetNextNonce())
      .build();

// the tx will be automatically signed and broadcasted using the configured KeyPair
PostTransactionResult preClaimTxResult = aeternityService
                                            .transactions
                                            .blockingPostTransaction(preClaimTx);
```

Note:

- After transaction is included, you have `300` keyblocks to broadcast `claim` transaction with
the same `salt` and it should be signed with the same private key as `pre-claim`.
    - As the `pre-claim` is required to avoid front running it is recommended to wait with the actual `claim` until at least 1 keyblock has been mined so that nobody knows which name you aim to claim.
    - The corresponding `claim` cannot be included in the same keyblock anyway. The protocol doesn't allow that.
- You should check if the name is still available before performing a `pre-claim`. The protocol itself doesn't reject a `pre-claim` transaction if the name isn't available anymore.
- As you can see above in the logs the result (`preClaimTx`) of the `aensPreclaim` has bound a `claim` function that you can make use of to perform the actual claim.
    - In case you want to perform the actual claim at a later point you should remember the `salt` that has been used for the `pre-claim`

### Claim

```java
// re-use the salt from the pre-claim here
NameClaimTransactionModel nameClaimTx =
    NameClaimTransactionModel.builder()
        .accountId(aeternityService.keyPairAddress)
        .name("userguide.chain")
        .nameSalt(salt)
        .nonce(aeternityService.accounts.blockingGetNextNonce())
        .build();

// the tx will be automatically signed and broadcasted using the configured KeyPair
PostTransactionResult nameClaimTxResult = aeternityService
                                            .transactions
                                            .blockingPostTransaction(nameClaimTx);
```

Note:

- The `nameFee` that is required will be correctly calculated automatically for the initial claim.
    - You can also set it manually but this is only required in running auctions (see below)
- In case the `claim` triggers an auction the required `nameFee` is locked by the protocol.
    - If you win the auction the `nameFee` is permanently deducted from your accounts balance and effectively *burned*.
        - It will be credited to `ak_11111111111111111111111111111111273Yts` which nobody can access. This reduces the total supply of AE over time.
    - If somebody else outbids you the provided `nameFee` is immediately released and returned to your account.
    
### Bid during an auction
In case there is an auction running for a name you want to claim you need to place a bid.

**Get the info about a running auction via middleware**
```java
// get auction information
NameAuctionResult nameAuctionResult = aeternityService
                                              .mdw
                                              .blockingGetNameAuction("auction.chain");

// calculate the minimum required fee for the next bid
BigInteger minimumNextFee = AENS.getNextNameFee(
                                        nameAuctionResult.getCurrentBid().getNameFee());

// build, sign & broadcast the bid (NameClaimTx)
NameClaimTransactionModel nameClaimTx =
    NameClaimTransactionModel.builder()
        .accountId(aeternityService.keyPairAddress)
        .name("userguide.chain")
        .nameSalt(BigInteger.ZERO) // for bids this value needs to be set to ZERO
        .nonce(aeternityService.accounts.blockingGetNextNonce())
        .build();

// the tx will be automatically signed and broadcasted using the configured KeyPair
PostTransactionResult nameClaimTxResult = aeternityService
                                            .transactions
                                            .blockingPostTransaction(nameClaimTx);
```

## 2. Update a name
Now that you own your AENS name you might want to update it in order to:

- Set pointers to `accounts`, `oracles`, `contracts` or `channels`.
- Extend the TTL before it expires.
    - By default a name will have a TTL of 180000 keyblocks (~375 days). It cannot be extended longer than 180000 keyblocks.

```java
// fake other allowed pointers for some KeyPair
KeyPair someKeyPair = keyPairService.generateKeyPair();
String contractPointer = keyPair.getContractAddress();
String channelPointer = keyPair.getAddress().replace("ak_", "ch_");
String oraclePointer = keyPair.getOracleAddress();
// generate another KeyPair for other pointers
KeyPair anotherKeyPair = keyPairService.generateKeyPair();
// build the NameUpdateTransactionModel
NameUpdateTransactionModel nameUpdateTx =
    NameUpdateTransactionModel.builder()
        .accountId(aeternityService.keyPairAddress) // this account must be owner of the name
        .nameId(AENS.getNameId("userguide.chain")) // get the correct nameId for a name
        .pointers(
            new HashMap<String, String>() {
              {
                put(AENS.POINTER_KEY_ACCOUNT, accountPointer); // default pointer-key for accounts
                put(AENS.POINTER_KEY_CHANNEL, channelPointer); // default pointer-key for channels
                put(AENS.POINTER_KEY_CONTRACT, contractPointer); // default pointer-key for contracts
                put(AENS.POINTER_KEY_ORACLE, oraclePointer); // default pointer-key for oracles
                put("arbitrary-account-pointer-key", anotherKeyPair.getAddress());
                put(
                    "arbitrary-channel-pointer-key",
                    // workaround to set a valid channel id
                    anotherKeyPair.getAddress().replace("ak_", "ch_"));
                put(
                    "arbitrary-contract-pointer-key",
                    anotherKeyPair.getContractAddress());
                put("arbitrary-oracle-pointer-key", anotherKeyPair.getOracleAddress());
              }
            })
        .nonce(aeternityService.accounts.blockingGetNextNonce())
        .build();

PostTransactionResult nameUpdateTxResult = aeternityService
                                                   .transactions
                                                   .blockingPostTransaction(nameUpdateTx);
```

Note:

- You can set up to 32 pointers in total for each name.
- The name will be extended for `AENS.MAX_TTL` (180000) by default.

## 3. Transfer ownership of a name
In some cases you might want to transfer the ownership of a name to another account. Of course this is also possible and you can do that as follows:

```java
// we select a random new owner
KeyPair newOwnerKeyPair = keyPairService.generateKeyPair();
NameTransferTransactionModel nameTransferTx =
    NameTransferTransactionModel.builder()
        .accountId(aeternityService.keyPairAddress) // this account must be owner of the name
        .nameId(AENS.getNameId("userguide.chain")) // get the correct nameId for a name
        .recipientId(newOwnerKeyPair.getAddress())
        .nonce(aeternityService.accounts.blockingGetNextNonce())
        .build();

PostTransactionResult nameTransferTxResult = aeternityService
                                                   .transactions
                                                   .blockingPostTransaction(nameTransferTx);
```

## 4. Revoke a name
In case you want to revoke a name prior to its expiration for whatever reason you can do that as follows:

```java
NameRevokeTransactionModel nameRevokeTx =
                NameRevokeTransactionModel.builder()
                    .accountId(aeternityService.keyPairAddress) // this account must be owner of the name
                    .nameId(AENS.getNameId("userguide.chain")) // get the correct nameId for a name
                    .nonce(aeternityService.accounts.blockingGetNextNonce())
                    .build();

PostTransactionResult nameRevokeTxResult = aeternityService
                                                   .transactions
                                                   .blockingPostTransaction(nameRevokeTx);
```

Note:

- On revocation the name enters in a `revoked` state.
- After a timeout of `2016` keyblocks the name will be available for claiming again.

## Delegate signature to contract (AENS interface)
It is possible to authorize a Sophia contract to manage an AENS name on behalf of your account. In order to achieve that you need to provide a delegation signature to the contract. The contract will then be able to use the [AENS interface](https://aeternity.com/aesophia/latest/sophia_features/#aens-interface) and perform AENS related actions on behalf of your account.
This functionality could for example be used to build an AENS marketplace.

The [DelegationService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/delegation/DelegationService.java)
can be used to produce the signatures that can be used to delegate control for certain actions to a smart contract. 

Examples how to delegate signatures to a contract can be found in our [contract-maven-showcase](https://github.com/kryptokrauts/contraect-showcase-maven).