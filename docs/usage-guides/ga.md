# Generalized Accounts

## Introduction
[Generalized Accounts](https://aeternity.com/protocol/generalized_accounts/index.html) (GAs) are mainly a way to give more flexibility when it comes to transaction integrity, in particular when it comes to signing. This is done by moving both the nonce handling and signature checking to a smart contract that is attached to the account.

## Example contract
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

  function to_sign(h : hash, n : int) : hash =
    Crypto.blake2b((h, n))
```

## Attach the Generalized Account
Following code snippet shows how to attach the contract above to your account and make it a GA:

```java
// TODO
```

## Perform a meta transaction for the GA
Once the GA is attached you can perform a `GaMetaTx`.
In order to perform a successful transaction you need to provide the correct nonce and signature for the transaction:

```java
// TODO
```