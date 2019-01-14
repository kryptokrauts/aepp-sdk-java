package com.kryptokrauts.aeternity.sdk.constants;

public interface Signing {

    // needed for transaction-signature
    // https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#transaction-signature
    String NETWORK_ID_TESTNET = "ae_devnet";
    String NETWORK_ID_MAINNET = "ae_mainnet";
}
