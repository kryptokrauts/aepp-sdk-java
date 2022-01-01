package com.kryptokrauts.aeternity.sdk.constants;

import java.math.BigInteger;

public interface BaseConstants {

  String PREFIX_ZERO_X = "0x";

  // https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
  int HD_CHAIN_PURPOSE = 44;

  int HD_CHAIN_CODE_AETERNITY = 457;

  // https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#gas
  long BASE_GAS = 15000;

  long GAS_PER_BYTE = 20;

  // https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#common-fields-for-transactions
  BigInteger MINIMAL_GAS_PRICE = BigInteger.valueOf(1000000000);

  BigInteger CONTRACT_DEFAULT_GAS_LIMIT = BigInteger.valueOf(25000);

  // default calldata for empty init function
  String CONTRACT_EMPTY_INIT_CALLDATA = "cb_KxFE1kQfP4oEp9E=";

  String CONTRACT_EMPTY_RETURN_DATA = "cb_Xfbg4g==";

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

  // the zero address account used for dry run
  String ZERO_ADDRESS_ACCOUNT = "ak_11111111111111111111111111111111273Yts";

  // the amount to set for a dry run using the zero address account
  String ZERO_ADDRESS_ACCOUNT_AMOUNT = "100000000000000000000000000000000000";

  // the zero address account default nonce
  BigInteger ZERO_ADDRESS_ACCOUNT_DEFAULT_NONCE = new BigInteger("2");

  // the zero address account devnet nonce
  BigInteger ZERO_ADDRESS_ACCOUNT_DEVNET_NONCE = BigInteger.ONE;
}
