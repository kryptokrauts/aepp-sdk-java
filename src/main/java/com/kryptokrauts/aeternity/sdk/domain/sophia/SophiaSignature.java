package com.kryptokrauts.aeternity.sdk.domain.sophia;

public class SophiaSignature extends SophiaBytes {

  public SophiaSignature(String signature) {
    super(signature, 64);
  }
}
