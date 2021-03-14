package com.kryptokrauts.aeternity.sdk.domain.secret.impl;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeterministicHierarchyEntry {

  private int depth;

  private RawKeyPair rawKeyPair;

  private Map<Integer, DeterministicHierarchyEntry> children;

  // we will only support hardened keys
  private boolean hardened = true;

  public DeterministicHierarchyEntry(int depth, RawKeyPair keypair) {
    this.depth = depth;
    this.rawKeyPair = keypair;
    this.children = new HashMap<>();
  }

  public DeterministicHierarchyEntry addChild(Integer index, RawKeyPair keypair) {
    this.children.put(index, new DeterministicHierarchyEntry(this.depth + 1, keypair));
    return this.children.get(index);
  }

  public Integer getHighestChildIndex() {
    if (this.children.keySet().size() > 0) {
      return this.children.keySet().stream().max(Integer::compareTo).get();
    }
    return 0;
  }
}
