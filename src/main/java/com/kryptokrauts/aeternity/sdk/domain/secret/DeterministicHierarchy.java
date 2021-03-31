package com.kryptokrauts.aeternity.sdk.domain.secret;

import java.util.HashMap;
import java.util.Map;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import lombok.Getter;

@Getter
public class DeterministicHierarchy {

  private static final Integer DEPTH_MASTER = 0;

  public static final Integer ADDRESS_INDEX_DEFAULT = 0;

  private Map<Integer, DeterministicHierarchyEntry> deterministicHierarchy;

  public DeterministicHierarchy(KeyPair master) {
    this.deterministicHierarchy = new HashMap<>();
    this.deterministicHierarchy.put(DEPTH_MASTER,
        new DeterministicHierarchyEntry(DEPTH_MASTER, master));
  }

  public void addAccount(KeyPair accountKeypair) {
    this.getMaster().addChild(BaseConstants.HD_CHAIN_PURPOSE, accountKeypair);
  }

  public void addChain(KeyPair chainKeypair) {
    this.getAccount().addChild(BaseConstants.HD_CHAIN_CODE_AETERNITY, chainKeypair);
  }

  /**
   * @param miKeypair child index keypair
   * @param mi0Keypair this childs internal chain keypair
   * @param mi00Keypair this childs actual address keypiar
   */
  public void addNextAddress(KeyPair miKeypair, KeyPair mi0Keypair, KeyPair mi00Keypair) {
    this.getChain().addChild(this.getChain().getNextChildIndex(), miKeypair)
        .addChild(ADDRESS_INDEX_DEFAULT, mi0Keypair).addChild(ADDRESS_INDEX_DEFAULT, mi00Keypair);
  }

  public KeyPair getChildAt(Integer index) {
    if (this.getChain().getChildren().get(index) == null) {
      throw new AException("Cannot retrieve child at index " + index
          + " - no child keypair was generated for this index. Max child index available: "
          + (this.getChain().getChildren().size() - 1));
    }
    return this.getChain().getChildren().get(index).getChildren().get(ADDRESS_INDEX_DEFAULT)
        .getChildren().get(ADDRESS_INDEX_DEFAULT).getRawKeyPair();
  }

  public Integer getNextChildIndex() {
    return this.getChain().getNextChildIndex();
  }

  public KeyPair getLastChild() {
    return this.getChildAt(this.getChain().getHighestChildIndex());
  }

  public KeyPair getMasterKeyPair() {
    return this.getMaster().getRawKeyPair();
  }

  public KeyPair getAccountKeyPair() {
    return this.getAccount().getRawKeyPair();
  }

  public KeyPair getChainKeyPair() {
    return this.getChain().getRawKeyPair();
  }

  private DeterministicHierarchyEntry getMaster() {
    this.checkHasMaster();
    return this.deterministicHierarchy.get(DEPTH_MASTER);
  }

  private DeterministicHierarchyEntry getAccount() {
    return getMaster().getChildren().get(BaseConstants.HD_CHAIN_PURPOSE);
  }

  private DeterministicHierarchyEntry getChain() {
    return getAccount().getChildren().get(BaseConstants.HD_CHAIN_CODE_AETERNITY);
  }

  private void checkHasMaster() {
    if (this.deterministicHierarchy.get(DEPTH_MASTER) == null) {
      throw new IllegalAccessError(
          "Cannot construct deterministic hierarchy without master keypair");
    }
  }
}
