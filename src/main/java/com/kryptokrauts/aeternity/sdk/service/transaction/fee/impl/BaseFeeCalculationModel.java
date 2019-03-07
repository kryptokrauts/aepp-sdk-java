package com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import java.math.BigInteger;

public class BaseFeeCalculationModel implements FeeCalculationModel {

  /**
   * the fee is calculated according to the following formula
   *
   * <p>(BASE_GASE + (byte_size * GAS_PER_BYTE)) * MINIMAL_GAS_PRICE
   */
  @Override
  public BigInteger calculateFee(int tx_byte_size, long minimalGasPrice) {
    return BigInteger.valueOf(
        (BaseConstants.BASE_GAS + tx_byte_size * BaseConstants.GAS_PER_BYTE) * minimalGasPrice);
  }
}
