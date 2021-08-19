package com.kryptokrauts.aeternity.sdk.constants;

public interface BaseConstants {

  String PREFIX_ZERO_X = "0x";

  // https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
  int HD_CHAIN_PURPOSE = 44;

  int HD_CHAIN_CODE_AETERNITY = 457;

  // https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#gas
  long BASE_GAS = 15000;

  long GAS_PER_BYTE = 20;

  // https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#common-fields-for-transactions
  long MINIMAL_GAS_PRICE = 1000000000;

  // average time between key-blocks in minutes
  long KEY_BLOCK_INTERVAL = 3;

  String AETERNITY_MESSAGE_PREFIX = "aeternity Signed Message:\n";

  int MAX_MESSAGE_LENGTH = 0xFD;

  // vertx base_path parameter
  String VERTX_BASE_PATH = "basePath";

  // the default testnet url
  String DEFAULT_TESTNET_URL = "https://testnet.aeternity.io";

  // the default testnet compiler url
  String DEFAULT_TESTNET_COMPILER_URL = "https://compiler.aeternity.io";

  // the default testnet mdw url
  String DEFAULT_TESTNET_MDW_URL = "https://testnet.aeternity.io/mdw";
}
