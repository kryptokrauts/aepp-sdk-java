# PayingForTx

## Introduction
This guide explains you how to perform a `PayingForTx` (also known as meta-transaction) using the SDK.

It is a very powerful and efficient solution that is crucial for onboarding new users into you ecosystem. By making use of the `PayingForTx` you will be able to cover the fees of your users.

## How it works
Typically somebody that you want to pay the transaction for (e.g. a new user of your decentralized aepp) signs the inner transaction (e.g. of type `ContractCallTx`) with a specific signature that is used for inner transactions.

You can then collect the signed inner transaction, wrap it into a `PayingForTx` and broadcast it to the network.

## Usage examples

TODO