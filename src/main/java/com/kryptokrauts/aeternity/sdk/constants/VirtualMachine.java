package com.kryptokrauts.aeternity.sdk.constants;

import com.kryptokrauts.sophia.compiler.generated.model.CompileOpts;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * aeternity supports multiple virtual machines. depending on the selected VM contract related
 * transactions need to specify the respective abiVersion and vmVersion
 */
@AllArgsConstructor
public enum VirtualMachine {
  AEVM(BigInteger.ONE, BigInteger.valueOf(6), CompileOpts.BackendEnum.AEVM),
  FATE(BigInteger.valueOf(3), BigInteger.valueOf(7), CompileOpts.BackendEnum.FATE);

  @Getter private BigInteger abiVersion;
  @Getter private BigInteger vmVersion;
  @Getter private CompileOpts.BackendEnum backendEnum;

  public static VirtualMachine getVirtualMachine(BigInteger abiVersion) {
    if (AEVM.getAbiVersion().equals(abiVersion)) {
      return AEVM;
    }
    return FATE;
  }
}
