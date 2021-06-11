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
    return getPointer(AENS.POINTER_KEY_ACCOUNT);
  }

  public Optional<String> getChannelPointer() {
    return getPointer(AENS.POINTER_KEY_CHANNEL);
  }

  public Optional<String> getContractPointer() {
    return getPointer(AENS.POINTER_KEY_CONTRACT);
  }

  public Optional<String> getOraclePointer() {
    return getPointer(AENS.POINTER_KEY_ORACLE);
  }

  private Optional<String> getPointer(String type) {
    if (pointers != null && pointers.entrySet() != null) {
      return pointers.entrySet().stream()
          .filter(e -> e.getKey().equals(type))
          .map(e -> e.getValue())
          .findFirst();
    }
    return Optional.empty();
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
