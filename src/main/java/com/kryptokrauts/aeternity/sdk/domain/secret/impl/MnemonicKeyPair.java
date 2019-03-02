package com.kryptokrauts.aeternity.sdk.domain.secret.impl;

import java.util.List;
import lombok.Data;

@Data
public class MnemonicKeyPair extends RawKeyPair {

  private List<String> mnemonicSeedWords;

  public MnemonicKeyPair(RawKeyPair rawKeyPair, List<String> mnemonicSeedWords) {
    super(rawKeyPair.getPublicKey(), rawKeyPair.getPrivateKey());
    this.mnemonicSeedWords = mnemonicSeedWords;
  }
}
