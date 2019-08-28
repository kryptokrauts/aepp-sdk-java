package com.kryptokrauts.aeternity.sdk.service.domain.name;

import com.kryptokrauts.aeternity.generated.model.NameEntry;
import com.kryptokrauts.aeternity.sdk.service.domain.GenericServiceResultObject;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public class NameIdResult extends GenericServiceResultObject<NameEntry, NameIdResult> {

  private String id;

  private BigInteger ttl;

  @Default private List<NamePointer> pointers = new LinkedList<NamePointer>();

  @Override
  protected NameIdResult map(NameEntry generatedResultObject) {
    if (generatedResultObject != null)
      return NameIdResult.builder()
          .id(generatedResultObject.getId())
          .ttl(generatedResultObject.getTtl())
          .pointers(
              generatedResultObject.getPointers().stream()
                  .map(
                      pointer ->
                          NamePointer.builder().id(pointer.getId()).key(pointer.getKey()).build())
                  .collect(Collectors.toList()))
          .build();
    else return NameIdResult.builder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return NameIdResult.class.getName();
  }
}
