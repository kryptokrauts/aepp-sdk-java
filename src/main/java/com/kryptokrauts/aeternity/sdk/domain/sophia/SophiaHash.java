package com.kryptokrauts.aeternity.sdk.domain.sophia;

import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;

public class SophiaHash extends SophiaType {

  private String hash;
  int length = 64;

  public SophiaHash(String hash) {
    if (!hash.startsWith("#")) {
      hash = String.join("", "#", hash);
    }
    if (!hash.matches("#[0-9A-Fa-f]+(_[0-9A-Fa-f]+)*")) {
      throw new InvalidParameterException(
          String.format(
              "Given bytes value %s is invalid - only characters that match pattern %s are allowed",
              hash, "#[0-9A-Fa-f]+(_[0-9A-Fa-f]+)*"));
    }
    hash = hash.replaceAll("_", "");
    if (hash.length() <= length - 1) {
      throw new InvalidParameterException(
          String.format(
              "Bytes value %s must have at least a length of %d (%d characters)",
              hash, (length / 2) - 1, length - 1));
    }
    if (hash.length() > length + 1) {
      throw new InvalidParameterException(
          String.format(
              "Given bytes value %s exceeds maximum length of %d (%d characters)",
              hash, length / 2, length));
    }
    this.hash = hash;
  }

  @Override
  public String getCompilerValue() {
    return hash;
  }
}
