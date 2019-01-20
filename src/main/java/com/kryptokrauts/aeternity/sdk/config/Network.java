package com.kryptokrauts.aeternity.sdk.config;

/**
 *  needed for transaction-signature
 *  https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#transaction-signature
 */
public enum Network {

    NETWORK_ID_TESTNET("ae_devnet"),
    NETWORK_ID_MAINNET("ae_mainnet");

    private String id;

    Network(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
