package com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.AbstractTransactionWithInnerTx;
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
    if (transaction instanceof AbstractTransactionWithInnerTx<?>) {
      AbstractTransactionWithInnerTx<?> innerTx = (AbstractTransactionWithInnerTx<?>) transaction;
      int inner_tx_byte_size = innerTx.getInnerTxRLPEncodedList().bitLength() / 8;
      return BigInteger.valueOf(
          ((5 * BaseConstants.BASE_GAS + tx_byte_size - inner_tx_byte_size)
                  * BaseConstants.GAS_PER_BYTE)
              * minimalGasPrice);
    } else {
      throw new AException(
          this.getClass().getName()
              + " fee calculation model is not applicable for any other than "
              + AbstractTransactionWithInnerTx.class.getName());
    }
  }
}
