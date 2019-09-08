package com.kryptokrauts.aeternity.sdk.service.name.domain;

import com.kryptokrauts.aeternity.generated.model.NameEntry;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NameIdResult extends GenericResultObject<NameEntry, NameIdResult> {

  private String id;

  private BigInteger ttl;

  @Default private List<NamePointerModel> pointers = new LinkedList<NamePointerModel>();

  @Override
  protected NameIdResult map(NameEntry generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .id(generatedResultObject.getId())
          .ttl(generatedResultObject.getTtl())
          .pointers(
              generatedResultObject.getPointers().stream()
                  .map(
                      pointer ->
                          NamePointerModel.builder()
                              .id(pointer.getId())
                              .key(pointer.getKey())
                              .build())
                  .collect(Collectors.toList()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return NameIdResult.class.getName();
  }
}
