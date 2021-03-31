package com.kryptokrauts.aeternity.sdk.domain.secret;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeterministicHierarchyEntry {

  private int depth;

  private KeyPair rawKeyPair;

  private byte[] chaincode;

  private byte[] privateKey;

  private Map<Integer, DeterministicHierarchyEntry> children;

  // we will only support hardened keys
  private boolean hardened = true;

  public DeterministicHierarchyEntry(int depth, KeyPair keypair) {
    this.depth = depth;
    this.rawKeyPair = keypair;
    this.children = new HashMap<>();
  }

  public DeterministicHierarchyEntry addChild(Integer index, KeyPair keypair) {
    this.children.put(index, new DeterministicHierarchyEntry(this.depth + 1, keypair));
    return this.children.get(index);
  }

  public Integer getHighestChildIndex() {
    if (this.children.keySet().size() > 0) {
      return this.children.keySet().stream().max(Integer::compareTo).get();
    }
    return 0;
  }

  public Integer getNextChildIndex() {
    Integer highestIndex = this.getHighestChildIndex();
    // special case due to index starts with 0
    if (highestIndex == 0) {
      if (this.children.keySet().size() > 0) {
        return 1;
      } else {
        return 0;
      }
    }
    return highestIndex + 1;
  }
}
