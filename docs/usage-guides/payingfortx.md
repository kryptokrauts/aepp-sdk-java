# PayingForTx

## Introduction
This guide explains you how to perform a `PayingForTx` (also known as meta-transaction) using this SDK.

It is a very powerful and efficient solution that is for example crucial for onboarding new users into the æternity ecosystem. By making use of the `PayingForTx` you will be able to cover the fees of your users.

## How it works
Typically somebody that you want to pay the transaction for (e.g. a new user of your decentralized aepp) signs the inner transaction (e.g. of type `ContractCallTx`) with a specific signature that is used for inner transactions.

You can then collect the signed inner transaction, wrap it into a `PayingForTx` and broadcast it to the network.

## SpendTx in PayingForTx
This is an example how one can cover the transaction fee for another account that
simply wants to send some AE:

```java
KeyPair senderWhoDelegates = ...
KeyPair recipient = ...
KeyPair payerDelegate = ...

BigInteger senderNonce = aeternityService
                              .accounts
                              .blockingGetNextNonce(senderWhoDelegates.getAddress());

SpendTransactionModel spendTx = 
    SpendTransactionModel.builder()
              .sender(senderWhoDelegates.getAddress())
              .recipient(anotherKeypair.getAddress())
              .amount(amount)
              .payload("yeah, somebody else payed the tx fee for this transaction =)")
              .nonce(senderNonce)
              .build();

// the inner tx of a PayingForTx needs to be signed with a specific prefix
// the sdk provides the respective method to do this
String signedInnerTx = aeternityService.transactions.signPayingForInnerTransaction(
                                     spendTx, senderWhoDelegates.getEncodedPrivateKey());

BigInteger payerNonce = aeternityService
                              .accounts
                              .blockingGetNextNonce(payerDelegate.getAddress());

PayingForTransactionModel payingForTx =
  PayingForTransactionModel.builder()
      .payerId(payerDelegate)
      .nonce(payerNonce)
      .innerTx(signedInnerTx)
      .build();

aeternityService.transactions.blockingPostTransaction(payingForTx);
```

Note:

- This can be done for any tx-type!
- In the example we assume that the wrapped inner tx is built and signed by a user that interacts with an aepp.
    - The aepp sends the signed transaction to the Java backend which wraps it into `PayingForTx` and pays the required fee.
- You can even [combine the usage of a Generalized Account with the PayingForTx](https://aeternity.com/protocol/generalized_accounts/ga_explained.html#payingfor-example) which provides lots of possiblities!

## Contract calls in PayingForTx
If you need to onboard users that don't have any funds you can pay the fee of their contract calls using the `PayingForTx`.
This way your aepp can hide some complexity of using the underlying blockchain and your users don't have to buy AE in order to use your aepp! 

## Use cases

- Game developers that want to quickly onboard new users.
- Governance aepps that want people to vote on important proposals without having them to pay anything.
- Custodians that want to offer an additional services to cover the transaction fees of their clients.
- ... many more!