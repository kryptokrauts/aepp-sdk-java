package com.kryptokrauts.aeternity.sdk.service.name.domain;

import com.kryptokrauts.aeternity.generated.model.NameEntry;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
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
    return pointers.values().stream()
        .filter(p -> p.startsWith(ApiIdentifiers.ACCOUNT_PUBKEY))
        .findFirst();
  }

  public Optional<String> getChannelPointer() {
    return pointers.values().stream().filter(p -> p.startsWith(ApiIdentifiers.CHANNEL)).findFirst();
  }

  public Optional<String> getContractPointer() {
    return pointers.values().stream()
        .filter(p -> p.startsWith(ApiIdentifiers.CONTRACT_PUBKEY))
        .findFirst();
  }

  public Optional<String> getOraclePointer() {
    return pointers.values().stream()
        .filter(p -> p.startsWith(ApiIdentifiers.ORACLE_PUBKEY))
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
