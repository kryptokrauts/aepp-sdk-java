package com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import java.math.BigInteger;

public class GaMetaFeeCalculationModel implements FeeCalculationModel {

  /**
   * the fee is calculated according to the following formula
   *
   * <p>(5 * BASE_GAS + byte_size(GAMetaTx) - byte_size(InnerTx)) * GasPerByte
   */
  @Override
  public BigInteger calculateFee(
      int tx_byte_size, long minimalGasPrice, AbstractTransaction<?> transaction) {
    // TODO replace with correct implementation, currently using calculation of contract create
    return BigInteger.valueOf(
        (5 * BaseConstants.BASE_GAS + tx_byte_size * BaseConstants.GAS_PER_BYTE) * minimalGasPrice);
  }
}
