package com.kryptokrauts.aeternity.sdk.constants;

public interface BaseConstants {

    String PREFIX_ZERO_X = "0x";

    // https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#gas
    long BASE_GAS = 15000;

    // https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#common-fields-for-transactions
        long ON_CHAIN_FEE_MULTIPLIER = 1000000;

    String AETERNITY_MESSAGE_PREFIX = "æternity Signed Message:\n";

    int MAX_MESSAGE_LENGTH = 0xFD;

    // vertx base_path parameter
    String VERTX_BASE_PATH = "basePath";

    // the default testnet url
    String DEFAULT_TESTNET_URL = "https://sdk-testnet.aepps.com/v2";

}
