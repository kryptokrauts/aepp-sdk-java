package com.kryptokrauts.aeternity.sdk.domain.sophia;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SophiaString extends SophiaType {

  private String value;

  @Override
  public String getCompilerValue() {
    return "\"" + value + "\"";
  }
}
