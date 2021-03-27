package com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import java.math.BigInteger;

/**
 * the fee is calculated according to the following formula
 *
 * <p>(BASE_GAS / 5 + byte_size(PayingForTx) - byte_size(InnerTx)) * GasPerByte
 */
public class PayingForFeeCalculationModel implements FeeCalculationModel {

  @Override
  public BigInteger calculateFee(
      int tx_byte_size, long minimalGasPrice, AbstractTransaction<?> transaction) {
    // TODO replace with correct implementation, need to subtract the byte_size of the inner tx
    return BigInteger.valueOf(
        (BaseConstants.BASE_GAS / 5 + tx_byte_size * BaseConstants.GAS_PER_BYTE) * minimalGasPrice);
  }
}
