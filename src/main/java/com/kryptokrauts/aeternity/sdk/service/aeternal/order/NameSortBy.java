package com.kryptokrauts.aeternity.sdk.service.aeternal.order;

public enum NameSortBy {
  NAME("name"),
  EXPIRATION("expiration"),
  WINNING_BID("max_bid");

  private final String value;

  NameSortBy(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
