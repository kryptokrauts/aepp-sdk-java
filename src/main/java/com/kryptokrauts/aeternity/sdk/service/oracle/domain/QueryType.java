package com.kryptokrauts.aeternity.sdk.service.oracle.domain;

public enum QueryType {
  OPEN("open"),
  CLOSED("closed"),
  ALL("all");

  private final String value;

  QueryType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
