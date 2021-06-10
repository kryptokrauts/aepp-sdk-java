package com.kryptokrauts.aeternity.sdk.constants;

import com.kryptokrauts.sophia.compiler.generated.model.CompileOpts;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * aeternity supports multiple virtual machines. depending on the selected VM contract related
 * transactions need to specify the respective abiVersion and vmVersion.
 *
 * <p>since protocol version 5 (Iris) new contracts can only be created using the FATE VM, see
 * https://github.com/aeternity/protocol/blob/master/contracts/contract_vms.md#virtual-machines-on-the-%C3%A6ternity-blockchain
 *
 * <p>it's still valid to use AEVM for contract calls of old contracts and as developer you can
 * always define a custom vmVersion to use for contract related transactions for the respective VM
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

  public VirtualMachine withVmVersion(BigInteger vmVersion) {
    this.vmVersion = vmVersion;
    return this;
  }
}
