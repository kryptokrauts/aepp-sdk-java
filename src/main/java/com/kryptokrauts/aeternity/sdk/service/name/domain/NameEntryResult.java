package com.kryptokrauts.aeternity.sdk.service.name.domain;

import com.kryptokrauts.aeternity.generated.model.NameEntry;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.sdk.constants.AENS;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NameEntryResult extends GenericResultObject<NameEntry, NameEntryResult> {

  private String id;
  private String owner;
  private BigInteger ttl;
  private Map<String, String> pointers;

  public Optional<String> getAccountPointer() {
    return pointers.entrySet().stream()
        .filter(e -> e.getKey().equals(AENS.POINTER_KEY_ACCOUNT))
        .map(e -> e.getValue())
        .findFirst();
  }

  public Optional<String> getChannelPointer() {
    return pointers.entrySet().stream()
        .filter(e -> e.getKey().equals(AENS.POINTER_KEY_CHANNEL))
        .map(e -> e.getValue())
        .findFirst();
  }

  public Optional<String> getContractPointer() {
    return pointers.entrySet().stream()
        .filter(e -> e.getKey().equals(AENS.POINTER_KEY_CONTRACT))
        .map(e -> e.getValue())
        .findFirst();
  }

  public Optional<String> getOraclePointer() {
    return pointers.entrySet().stream()
        .filter(e -> e.getKey().equals(AENS.POINTER_KEY_ORACLE))
        .map(e -> e.getValue())
        .findFirst();
  }

  @Override
  protected NameEntryResult map(NameEntry generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .id(generatedResultObject.getId())
          .owner(generatedResultObject.getOwner())
          .ttl(generatedResultObject.getTtl())
          .pointers(getPointers(generatedResultObject.getPointers()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return NameEntryResult.class.getName();
  }

  private Map<String, String> getPointers(final List<NamePointer> namePointers) {
    if (namePointers == null) {
      return Collections.emptyMap();
    }
    return namePointers.stream().collect(Collectors.toMap(p -> p.getKey(), p -> p.getId()));
  }
}
