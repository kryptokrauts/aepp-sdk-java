package com.kryptokrauts.aeternity.sdk.domain.sophia;

import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;

public class SophiaBytes extends SophiaType {
  private String bytes;

  public SophiaBytes(String bytes, int size) {
    int length = size * 2;

    if (!bytes.startsWith("#")) {
      bytes = String.join("", "#", bytes);
    }
    if (!bytes.matches("#[0-9A-Fa-f]+(_[0-9A-Fa-f]+)*")) {
      throw new InvalidParameterException(
          String.format(
              "Given bytes value %s is invalid - only characters that match pattern %s are allowed",
              bytes, "#[0-9A-Fa-f]+(_[0-9A-Fa-f]+)*"));
    }
    bytes = bytes.replaceAll("_", "");
    if (bytes.length() <= length - 1) {
      throw new InvalidParameterException(
          String.format(
              "Bytes value %s must have at least a length of %d (%d characters)",
              bytes, (length / 2) - 1, length - 1));
    }
    if (bytes.length() > length + 1) {
      throw new InvalidParameterException(
          String.format(
              "Given bytes value %s exceeds maximum length of %d (%d characters)",
              bytes, length / 2, length));
    }
    this.bytes = bytes;
  }

  @Override
  public String getSophiaValue() {
    return bytes;
  }
}
