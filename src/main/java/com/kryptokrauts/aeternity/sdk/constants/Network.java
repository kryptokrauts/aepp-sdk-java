package com.kryptokrauts.aeternity.sdk.constants;

/**
 * List of available networks - needed for transaction-signature <a href=
 * https://github.com/aeternity/protocol/blob/master/consensus/consensus.md#transaction-signature>transaction-signature</a>
 */
public enum Network {
  DEVNET("ae_devnet"),
  TESTNET("ae_uat"),
  MAINNET("ae_mainnet");

  private String id;

  Network(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public Network withId(String networkId) {
    this.id = networkId;
    return this;
  }
}
