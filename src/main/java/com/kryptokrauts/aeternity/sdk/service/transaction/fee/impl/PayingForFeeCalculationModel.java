package com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import java.math.BigDecimal;
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
    return BigDecimal.valueOf(
            (((BaseConstants.BASE_GAS / 5) + tx_byte_size) * BaseConstants.GAS_PER_BYTE)
                * minimalGasPrice)
        // multply by 10% of the calculated fee to cover inner tx byte size
        .multiply(new BigDecimal("1.1"))
        .toBigInteger();

    /*
     * We keep this logic block in case we decode the tx hashes back into our models
     *
     * if (transaction instanceof PayingForTransaction) { AbstractTransactionWithInnerTx<?> innerTx
     * = (AbstractTransactionWithInnerTx<?>) transaction; int inner_tx_byte_size =
     * innerTx.getInnerTxRLPEncodedList().bitLength() / 8; return BigInteger.valueOf(
     * (((BaseConstants.BASE_GAS / 5) + tx_byte_size - inner_tx_byte_size)
     * BaseConstants.GAS_PER_BYTE) minimalGasPrice); } else { throw new AException(
     * this.getClass().getName() + " fee calculation model is not applicable for any other than " +
     * AbstractTransactionWithInnerTx.class.getName()); }
     */
  }
}
