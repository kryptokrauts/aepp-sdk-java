package com.kryptokrauts.aeternity.sdk.domain.secret;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Data structure to hold the deterministic key hierarchy tree, based on the given master keypair
 * {@link HdKeyPair}
 */
@Getter
public class DeterministicHierarchy {

  private static final Integer DEPTH_MASTER = 0;

  public static final Integer ADDRESS_INDEX_DEFAULT = 0;

  private Map<Integer, DeterministicHierarchyEntry> dhTree;

  public DeterministicHierarchy(HdKeyPair master) {
    this.dhTree = new HashMap<>();
    this.dhTree.put(DEPTH_MASTER, new DeterministicHierarchyEntry(DEPTH_MASTER, master));
  }

  public void addAccount(HdKeyPair accountKeypair) {
    this.getMaster().addChild(BaseConstants.HD_CHAIN_PURPOSE, accountKeypair);
  }

  public void addChain(HdKeyPair chainKeypair) {
    this.getAccount().addChild(BaseConstants.HD_CHAIN_CODE_AETERNITY, chainKeypair);
  }

  /**
   * @param miKeypair child index keypair
   * @param mi0Keypair this childs internal chain keypair
   * @param mi00Keypair this childs actual address keypiar
   */
  public void addNextAddress(HdKeyPair miKeypair, HdKeyPair mi0Keypair, HdKeyPair mi00Keypair) {
    this.getChain()
        .addChild(this.getChain().getNextChildIndex(), miKeypair)
        .addChild(ADDRESS_INDEX_DEFAULT, mi0Keypair)
        .addChild(ADDRESS_INDEX_DEFAULT, mi00Keypair);
  }

  public HdKeyPair getChildAt(Integer index) {
    if (this.getChain().getChildren().get(index) == null) {
      throw new AException(
          "Cannot retrieve child at index "
              + index
              + " - no child keypair was generated for this index. Max child index available: "
              + (this.getChain().getChildren().size() - 1));
    }
    HdKeyPair keyPair =
        this.getChain()
            .getChildren()
            .get(index)
            .getChildren()
            .get(ADDRESS_INDEX_DEFAULT)
            .getChildren()
            .get(ADDRESS_INDEX_DEFAULT)
            .getKeyPair();
    keyPair.setIndex(index);
    return keyPair;
  }

  public List<HdKeyPair> getChildKeyPairs() {
    return this.getChain().getChildren().entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(
            value -> {
              HdKeyPair returnKeyPair =
                  value
                      .getValue()
                      .getChildren()
                      .get(ADDRESS_INDEX_DEFAULT)
                      .getChildren()
                      .get(ADDRESS_INDEX_DEFAULT)
                      .getKeyPair();
              returnKeyPair.setIndex(value.getValue().getKeyPair().getIndex());
              return returnKeyPair;
            })
        .collect(Collectors.toList());
  }

  public Map<Integer, HdKeyPair> getChildKeysIndexMap() {
    return this.getChain().getChildren().entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e ->
                    e.getValue()
                        .getChildren()
                        .get(ADDRESS_INDEX_DEFAULT)
                        .getChildren()
                        .get(ADDRESS_INDEX_DEFAULT)
                        .getKeyPair()));
  }

  public Integer getNextChildIndex() {
    return this.getChain().getNextChildIndex();
  }

  public HdKeyPair getLastChildKeyPair() {
    return this.getChildAt(this.getChain().getHighestChildIndex());
  }

  public HdKeyPair getMasterKeyPair() {
    return this.getMaster().getKeyPair();
  }

  public HdKeyPair getAccountKeyPair() {
    return this.getAccount().getKeyPair();
  }

  public HdKeyPair getChainKeyPair() {
    return this.getChain().getKeyPair();
  }

  private DeterministicHierarchyEntry getMaster() {
    this.checkHasMaster();
    return this.dhTree.get(DEPTH_MASTER);
  }

  private DeterministicHierarchyEntry getAccount() {
    return getMaster().getChildren().get(BaseConstants.HD_CHAIN_PURPOSE);
  }

  private DeterministicHierarchyEntry getChain() {
    return getAccount().getChildren().get(BaseConstants.HD_CHAIN_CODE_AETERNITY);
  }

  private void checkHasMaster() {
    if (this.dhTree.get(DEPTH_MASTER) == null) {
      throw new IllegalAccessError(
          "Cannot construct deterministic hierarchy without master keypair");
    }
  }
}
