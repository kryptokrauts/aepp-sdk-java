package com.kryptokrauts.aeternity.sdk.service.mdw.domain;

public enum SortDirection {
  FORWARD("forward"),
  BACKWARD("backward");

  private final String value;

  SortDirection(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
