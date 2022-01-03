package com.kryptokrauts.aeternity.sdk.domain.sophia;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class SophiaAENSName extends SophiaType {

  private String owner;
  private SophiaChainTTL chainTTL;
  private Map<String, SophiaAENSPointee> pointers;

  public SophiaAENSName(
      String owner, SophiaChainTTL chainTTL, Map<String, SophiaAENSPointee> pointers) {
    this.owner = owner;
    this.chainTTL = chainTTL;
    if (pointers == null) {
      this.pointers = new HashMap<>();
    } else {
      this.pointers = pointers;
    }
  }

  public String getOwner() {
    return this.owner;
  }

  public Map<String, SophiaAENSPointee> getPointers() {
    return this.pointers;
  }

  public SophiaChainTTL getChainTTL() {
    return this.chainTTL;
  }

  public void addPointer(String key, SophiaAENSPointee pointee) {
    pointers.put(key, pointee);
  }

  @Override
  public String getCompilerValue() {
    return "AENS.Name("
        + owner
        + ","
        + chainTTL.getCompilerValue()
        + ",{"
        + pointers.keySet().stream()
            .map(r -> "[\"" + r + "\"]=" + pointers.get(r).getCompilerValue())
            .collect(Collectors.joining(","))
        + "})";
  }
}
