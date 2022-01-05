package com.kryptokrauts.aeternity.sdk.domain.secret;

import java.util.List;
import lombok.Getter;

/**
 * This class wrapps a mnemonic additionally to the master rawKeyPair it contains the list of
 * mnemonic seed words, the generated {@link KeyPair} and the {@link DeterministicHierarchy} which
 * build the base for generating a hierarchical deterministic wallet.
 *
 * <p>The {@link DeterministicHierarchy} object can either be created from the root (master) or a
 * derived key according to the tree structure stated in <a
 * href=https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Master_key_generation>BIP32</a>
 */
@Getter
public class HdWallet {

  private List<String> mnemonicSeedWords;

  private DeterministicHierarchy deterministicHierarchy;

  public HdWallet(HdKeyPair masterKeyPair, List<String> mnemonicSeedWords) {
    this.mnemonicSeedWords = mnemonicSeedWords;
    this.deterministicHierarchy = new DeterministicHierarchy(masterKeyPair);
  }

  public HdKeyPair getMasterKeyPair() {
    return this.getDeterministicHierarchy().getMasterKeyPair();
  }

  public List<HdKeyPair> getChildKeyPairs() {
    return this.getDeterministicHierarchy().getChildKeyPairs();
  }

  public HdKeyPair getLastChildKeyPair() {
    return this.getDeterministicHierarchy().getLastChildKeyPair();
  }
}
