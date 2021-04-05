package com.kryptokrauts.aeternity.sdk.domain.secret;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class DeterministicHierarchy {

  private static final Integer DEPTH_MASTER = 0;

  public static final Integer ADDRESS_INDEX_DEFAULT = 0;

  private Map<Integer, DeterministicHierarchyEntry> dhTree;

  public DeterministicHierarchy(HDKeyPair master) {
    this.dhTree = new HashMap<>();
    this.dhTree.put(DEPTH_MASTER, new DeterministicHierarchyEntry(DEPTH_MASTER, master));
  }

  public void addAccount(HDKeyPair accountKeypair) {
    this.getMaster().addChild(BaseConstants.HD_CHAIN_PURPOSE, accountKeypair);
  }

  public void addChain(HDKeyPair chainKeypair) {
    this.getAccount().addChild(BaseConstants.HD_CHAIN_CODE_AETERNITY, chainKeypair);
  }

  /**
   * @param miKeypair child index keypair
   * @param mi0Keypair this childs internal chain keypair
   * @param mi00Keypair this childs actual address keypiar
   */
  public void addNextAddress(HDKeyPair miKeypair, HDKeyPair mi0Keypair, HDKeyPair mi00Keypair) {
    this.getChain()
        .addChild(this.getChain().getNextChildIndex(), miKeypair)
        .addChild(ADDRESS_INDEX_DEFAULT, mi0Keypair)
        .addChild(ADDRESS_INDEX_DEFAULT, mi00Keypair);
  }

  public HDKeyPair getChildAt(Integer index) {
    if (this.getChain().getChildren().get(index) == null) {
      throw new AException(
          "Cannot retrieve child at index "
              + index
              + " - no child keypair was generated for this index. Max child index available: "
              + (this.getChain().getChildren().size() - 1));
    }
    return this.getChain()
        .getChildren()
        .get(index)
        .getChildren()
        .get(ADDRESS_INDEX_DEFAULT)
        .getChildren()
        .get(ADDRESS_INDEX_DEFAULT)
        .getKeyPair();
  }

  public List<HDKeyPair> getChildKeyPairs() {
    return this.getChain().getChildren().entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(
            value -> {
              HDKeyPair returnKeyPair =
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

  public Map<Integer, HDKeyPair> getChildKeysIndexMap() {
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

  public HDKeyPair getLastChildKeyPair() {
    return this.getChildAt(this.getChain().getHighestChildIndex());
  }

  public HDKeyPair getMasterKeyPair() {
    return this.getMaster().getKeyPair();
  }

  public HDKeyPair getAccountKeyPair() {
    return this.getAccount().getKeyPair();
  }

  public HDKeyPair getChainKeyPair() {
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
