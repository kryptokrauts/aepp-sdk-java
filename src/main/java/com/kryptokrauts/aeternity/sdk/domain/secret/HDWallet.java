package com.kryptokrauts.aeternity.sdk.domain.secret;

import java.util.List;
import lombok.Getter;

/**
 * this class wrapps a mnemonic additionally to the master rawKeyPair it contains the list of
 * mnemonic seed words, the generated {@link KeyPair} and the {@link DeterministicHierarchy} which
 * build the base for generating a hierarchical deterministic wallet.
 *
 * <p>The deterministicHierarchy object can either be created from the root (master) or a derived
 * key according to the tree structure stated in <a
 * href=https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Master_key_generation>BIP32</a>
 */
@Getter
public class HDWallet {

  private List<String> mnemonicSeedWords;

  private DeterministicHierarchy deterministicHierarchy;

  public HDWallet(HDKeyPair masterKeyPair, List<String> mnemonicSeedWords) {
    this.mnemonicSeedWords = mnemonicSeedWords;
    this.deterministicHierarchy = new DeterministicHierarchy(masterKeyPair);
  }

  public HDKeyPair getMasterKeyPair() {
    return this.getDeterministicHierarchy().getMasterKeyPair();
  }

  public List<HDKeyPair> getChildKeyPairs() {
    return this.getDeterministicHierarchy().getChildKeyPairs();
  }

  public HDKeyPair getLastChildKeyPair() {
    return this.getDeterministicHierarchy().getLastChildKeyPair();
  }
}
