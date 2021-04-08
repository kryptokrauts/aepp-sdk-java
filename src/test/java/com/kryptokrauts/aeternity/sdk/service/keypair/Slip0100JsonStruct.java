package com.kryptokrauts.aeternity.sdk.service.keypair;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Slip0100JsonStruct {
  private String mnemonic;

  private String password;

  private List<DerivedKeyEntry> accounts;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DerivedKeyEntry {

    private int index;

    private String address;

    private String pk;
  }
}
