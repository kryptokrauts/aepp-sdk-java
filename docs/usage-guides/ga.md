# Generalized Accounts

## Introduction
[Generalized Accounts](https://aeternity.com/protocol/generalized_accounts/index.html) (GAs) are mainly a way to give more flexibility when it comes to transaction integrity, in particular when it comes to signing. This is done by moving both the nonce handling and signature checking to a smart contract that is attached to the account.

## Example contract
The following contract can be used in order to make the æternity account a GA that expects transactions
to be signed with the Elliptic Curve Digital Signature Algorithm known from Ethereum. This way you can for example use
your Ethereum private key in order to sign transactions on æternity.

```sophia
contract ECDSAAuth =
  record state = { nonce : int, owner : bytes(20) }

  entrypoint init(owner' : bytes(20)) = { nonce = 1, owner = owner' }

  stateful entrypoint authorize(n : int, s : bytes(65)) : bool =
    require(n >= state.nonce, "Nonce too low")
    require(n =< state.nonce, "Nonce too high")
    put(state{ nonce = n + 1 })
    switch(Auth.tx_hash)
      None          => abort("Not in Auth context")
      Some(tx_hash) => Crypto.ecverify_secp256k1(to_sign(tx_hash, n), state.owner, s)

  entrypoint to_sign(h : hash, n : int) : hash =
    Crypto.blake2b((h, n))

  entrypoint get_nonce() : int =
    state.nonce

  entrypoint get_owner() : bytes(20) =
    state.owner
```

## Attach the Generalized Account
Before actually being able to use the Ethereum private key for signing you have to first attach the contract to the account
which makes it a GA.

Following code snippet shows how to achieve this:

```java
String ecdsaAuthSource = "...";
String ethereumAddress = "...";

AccountResult gaTestAccount =
  aeternityService.accounts.blockingGetAccount(gaAccountKeyPair.getAddress());
log.info(gaTestAccount.toString()); // AccountResult(publicKey=ak_2V4Sgh4FRBaAGox9pkFgYBKFGjf4kKxyMumiT9RcvCCeSFiTpS, balance=10000000000000000000, nonce=0, payable=true, kind=basic, gaContractId=null, gaAuthenticationFunction=null)

// Ethereum address as bytes(20) in Sophia
String expectedEthereumAddress = ethereumAddress.replace("0x", "#");

// encode the required calldata via http compiler
StringResultWrapper resultWrapper =
  aeternityService.compiler.blockingEncodeCalldata(
      ecdsaAuthSource, "init", SophiaTypeTransformer
          .toCompilerInput(List.of(new SophiaBytes(expectedEthereumAddress, 20))), null);
String callData = resultWrapper.getResult();

// get bytecode via http compiler
resultWrapper =
  aeternityService.compiler.blockingCompile(ecdsaAuthSource, null);
String code = resultWrapper.getResult();

// build the GAAttachTx
GeneralizedAccountsAttachTransactionModel gaAttachTx =
  GeneralizedAccountsAttachTransactionModel.builder()
      .authFun(EncodingUtils.generateAuthFunHash("authorize"))
      .callData(callData)
      .code(code)
      .nonce(gaTestAccount.getNonce().add(ONE))
      .ownerId(gaTestAccount.getPublicKey())
      .build();

// broadcast the tx to attach the GA
aeternityService.transactions.blockingPostTransaction(
      gaAttachTx, gaAccountKeyPair.getEncodedPrivateKey());

gaTestAccount =
  aeternityService.accounts.blockingGetAccount(gaAccountKeyPair.getAddress());
log.info(gaTestAccount.toString()); // AccountResult(publicKey=ak_2V4Sgh4FRBaAGox9pkFgYBKFGjf4kKxyMumiT9RcvCCeSFiTpS, balance=9999915897000000000, nonce=1, payable=true, kind=generalized, gaContractId=ct_2m5omoYZCRMf4as85V6FW3LDENoBM4JnHgMoPKk6Xz41tb6617, gaAuthenticationFunction=authorize)
```

Congratulations, you now have a GA and can sign transactions with your Ethereum private key!

Attention:

- Be aware, there is no way back if you made your account a GA!

## Perform a meta transaction for the GA
Once the GA is attached you can perform a `GaMetaTx`.
The `GaMetaTx` can include any type of tx supported by the æternity protocol.
In this specific example we perform a simple `SpendTx`.
The important part is how to produce the right hash of the tx in combination with the nonce which needs to be signed. 
For this specific example a Sophia encoded tuple of a hash and an integer, which is also hashed, needs to be signed.
As [this is not trivial](https://aeternity.com/protocol/generalized_accounts/ga_explained.html#caveat-producing-the-right-hash) we use the dry-run functionality of the node to get the correct hash from the contract by providing the tx-hash and the nonce in order to achieve that.

Note:

- To simplify this you could also just hash a concatenated String of tx-hash and nonce ;-)

```java
// send 1 AE
UnitConversionService unitConversionService = new DefaultUnitConversionServiceImpl();
BigInteger amountToSend = unitConversionService.toSmallestUnit("1");

// create a new KeyPair for the recipient
KeyPair otherRecipient = keyPairService.generateKeyPair();

// build the inner SpendTx to be included in the GaMetaTx
SpendTransactionModel gaInnerSpendTx =
  SpendTransactionModel.builder()
      .sender(gaAccountKeyPair.getAddress())
      .recipient(otherRecipient.getAddress())
      .amount(amountToSend)
      .payload("spent using a generalized account with Ethereum signature =)")
      .nonce(ZERO) // GA inner tx requires 0 as nonce
      .build();

// compute the correct hash for the inner tx
String txHash = aeternityService.transactions.computeGAInnerTxHash(gaInnerSpendTx);

// call the "to_sign" entrypoint to get the correct hash to sign from the contract
Object toSignResult = aeternityService.transactions
            .blockingReadOnlyContractCall(gaTestAccount.getGaContractId(), "to_sign",
                ecdsaAuthSource, ContractTxOptions.builder().params(List.of(
                    new SophiaHash(txHash), 1))
                    .build());

// remove the Sophia prefix "#" from the hash and decode it 
byte[] toSign = Hex.decode(toSignResult.toString().substring(1));
// sign the hash e.g. using web3j
byte[] signedTxHashWithNonce = web3jSignMessage(toSign, credentials.getEcKeyPair());

// encode the require authData using the http compiler
String authData = this.aeternityService.compiler
    .blockingEncodeCalldata(ecdsaAuthSource, "authorize", SophiaTypeTransformer
        .toCompilerInput(
            List.of(1, new SophiaBytes(Hex.toHexString(signedTxHashWithNonce), 65))), null)
    .getResult();

// build the GaMetaTx
GeneralizedAccountsMetaTransactionModel gaMetaTx =
    GeneralizedAccountsMetaTransactionModel.builder()
        .gaId(gaAccountKeyPair.getAddress())
        .authData(authData)
        .innerTxModel(gaInnerSpendTx)
        .build();

// broadcast the tx
aeternityService.transactions.blockingPostTransaction(gaMetaTx);
```