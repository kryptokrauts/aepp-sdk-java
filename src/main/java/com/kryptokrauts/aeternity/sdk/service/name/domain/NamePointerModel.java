package com.kryptokrauts.aeternity.sdk.service.name.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class NamePointerModel {
  private String key;

  private String id;
}
