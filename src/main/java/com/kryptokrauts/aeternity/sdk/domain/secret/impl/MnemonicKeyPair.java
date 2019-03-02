package com.kryptokrauts.aeternity.sdk.domain.secret.impl;

import java.util.List;

import org.bitcoinj.crypto.DeterministicHierarchy;

import lombok.Getter;

/**
 * @author Michel Meier 
 *     this class wrapps a mnemonic additionally to the rawKeyPair it contains the
 *     list of mnemonic seed words, the generated {@link RawKeyPair} and the {@link
 *     DeterministicHierarchy} which build the base for generating a hierarchical deterministic
 *     wallet.
 *     <p>The deterministicHierarchy object can either be created from the root (master) or a
 *     derived key according to the tree structure stated in <a
 *     href=https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Master_key_generation>BIP32</a>
 */
@Getter
public class MnemonicKeyPair extends RawKeyPair {

  private List<String> mnemonicSeedWords;

  private DeterministicHierarchy deterministicHierarchy;

  public MnemonicKeyPair(
      RawKeyPair rawKeyPair,
      List<String> mnemonicSeedWords,
      DeterministicHierarchy deterministicHierarchy) {
    super(rawKeyPair.getPublicKey(), rawKeyPair.getPrivateKey());
    this.mnemonicSeedWords = mnemonicSeedWords;
    this.deterministicHierarchy = deterministicHierarchy;
  }

  public RawKeyPair toRawKeyPair() {
    return new RawKeyPair(getPublicKey(), getPrivateKey());
  }
}
