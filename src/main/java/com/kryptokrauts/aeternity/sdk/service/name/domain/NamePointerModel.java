package com.kryptokrauts.aeternity.sdk.service.name.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NamePointerModel {
  private String key;

  private String id;
}
