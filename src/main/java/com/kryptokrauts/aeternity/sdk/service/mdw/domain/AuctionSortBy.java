package com.kryptokrauts.aeternity.sdk.service.mdw.domain;

public enum AuctionSortBy {
  NAME("name"),
  EXPIRATION("expiration");

  private final String value;

  AuctionSortBy(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
