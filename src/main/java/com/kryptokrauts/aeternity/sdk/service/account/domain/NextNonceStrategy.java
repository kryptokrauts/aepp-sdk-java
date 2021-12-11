package com.kryptokrauts.aeternity.sdk.service.account.domain;

public enum NextNonceStrategy {
  /**
   * The greatest nonce seen in the account or currently in the transaction pool is incremented with
   * 1.
   */
  MAX("max"),
  /**
   * Transactions in the mempool are checked if there are gaps. Use it to request missing nonces
   * that prevent transactions with greater nonces to get included.
   */
  CONTINUITY("continuity");

  private final String value;

  NextNonceStrategy(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
