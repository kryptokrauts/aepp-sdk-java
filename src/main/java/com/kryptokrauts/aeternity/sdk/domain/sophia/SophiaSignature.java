package com.kryptokrauts.aeternity.sdk.domain.sophia;

import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;

public class SophiaSignature extends SophiaType {
  private String signature;
  int length = 128;

  public SophiaSignature(String signature) {
    if (!signature.startsWith("#")) {
      signature = String.join("", "#", signature);
    }
    if (!signature.matches("#[0-9A-Fa-f]+(_[0-9A-Fa-f]+)*")) {
      throw new InvalidParameterException(
          String.format(
              "Given bytes value %s is invalid - only characters that match pattern %s are allowed",
              signature, "#[0-9A-Fa-f]+(_[0-9A-Fa-f]+)*"));
    }
    signature = signature.replaceAll("_", "");
    if (signature.length() <= length - 1) {
      throw new InvalidParameterException(
          String.format(
              "Bytes value %s must have at least a length of %d (%d characters)",
              signature, (length / 2) - 1, length - 1));
    }
    if (signature.length() > length + 1) {
      throw new InvalidParameterException(
          String.format(
              "Given bytes value %s exceeds maximum length of %d (%d characters)",
              signature, length / 2, length));
    }
    this.signature = signature;
  }

  @Override
  public String getSophiaValue() {
    return signature;
  }
}
