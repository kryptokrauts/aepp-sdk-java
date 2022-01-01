package com.kryptokrauts.aeternity.sdk.domain.sophia;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SophiaString extends SophiaType {
  private String rawValue;

  @Override
  public String getSophiaValue() {
    return "\"" + rawValue + "\"";
  }
}
