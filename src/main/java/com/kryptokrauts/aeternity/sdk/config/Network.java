package com.kryptokrauts.aeternity.sdk.config;

/**
 *  needed for transaction-signature
 *  https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#transaction-signature
 */
public enum Network {

    TESTNET("ae_devnet"),
    MAINNET("ae_mainnet");

    private String id;

    Network(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
