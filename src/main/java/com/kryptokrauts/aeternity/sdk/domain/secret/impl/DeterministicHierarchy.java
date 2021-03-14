package com.kryptokrauts.aeternity.sdk.domain.secret.impl;

import java.util.HashMap;
import java.util.Map;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import lombok.Getter;

@Getter
public class DeterministicHierarchy {

  private static final Integer DEPTH_MASTER = 0;

  public static final Integer ADDRESS_INDEX_DEFAULT = 0;

  private Map<Integer, DeterministicHierarchyEntry> deterministicHierarchy;

  public DeterministicHierarchy(RawKeyPair master) {
    this.deterministicHierarchy = new HashMap<>();
    this.deterministicHierarchy.put(DEPTH_MASTER,
        new DeterministicHierarchyEntry(DEPTH_MASTER, master));
  }

  public void addAccount(RawKeyPair accountKeypair) {
    this.getMaster().addChild(BaseConstants.HD_CHAIN_PURPOSE, accountKeypair);
  }

  public void addChain(RawKeyPair chainKeypair) {
    this.getAccount().addChild(BaseConstants.HD_CHAIN_CODE_AETERNITY, chainKeypair);
  }

  /**
   * 
   * @param miKeypair child index keypair
   * @param mi0Keypair this childs internal chain keypair
   * @param mi00Keypair this childs actual address keypiar
   */
  public void addNextAddress(RawKeyPair miKeypair, RawKeyPair mi0Keypair, RawKeyPair mi00Keypair) {
    this.getChain().addChild(this.getChain().getHighestChildIndex(), miKeypair)
        .addChild(ADDRESS_INDEX_DEFAULT, mi0Keypair).addChild(ADDRESS_INDEX_DEFAULT, mi00Keypair);
  }

  public RawKeyPair getChildAt(Integer index) {
    return this.getChain().getChildren().get(index).getChildren().get(ADDRESS_INDEX_DEFAULT)
        .getChildren().get(ADDRESS_INDEX_DEFAULT).getRawKeyPair();
  }

  public Integer getNextChildIndex() {
    return this.getChain().getHighestChildIndex();
  }

  public RawKeyPair getMasterKey() {
    return this.getMaster().getRawKeyPair();
  }

  public RawKeyPair getAccountKeypair() {
    return this.getAccount().getRawKeyPair();
  }

  public RawKeyPair getChainKeypair() {
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
