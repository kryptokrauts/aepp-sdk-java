package com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleExtendTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleQueryTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRegisterTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleResponseTransaction;
import java.math.BigInteger;

public class OracleFeeCalculationModel implements FeeCalculationModel {

  /**
   * the fee is calculated according to the following formula
   *
   * <p>ceiling(32000 * RelativeTTL / floor(60 * 24 * 365 / key_block_interval))+
   * byte_size(OracleRegisterTx) * GasPerByte
   */
  @Override
  public BigInteger calculateFee(
      int tx_byte_size, long minimalGasPrice, AbstractTransaction<?> transaction) {
    long relativeTtl;
    if (transaction instanceof OracleRegisterTransaction) {
      relativeTtl = ((OracleRegisterTransaction) transaction).getOracleTtl().getValue().longValue();
    } else if (transaction instanceof OracleExtendTransaction) {
      relativeTtl =
          ((OracleExtendTransaction) transaction).getOracleRelativeTtl().getValue().longValue();
    } else if (transaction instanceof OracleQueryTransaction) {
      relativeTtl = ((OracleQueryTransaction) transaction).getQueryTtl().getValue().longValue();
    } else if (transaction instanceof OracleResponseTransaction) {
      relativeTtl =
          ((OracleResponseTransaction) transaction).getResponseTtl().getValue().longValue();
    } else {
      throw new UnsupportedOperationException(
          String.format(
              "Fee calculation not supported for class %s", transaction.getClass().getName()));
    }
    long fee =
        ((BaseConstants.BASE_GAS + tx_byte_size * BaseConstants.GAS_PER_BYTE)
                + (long)
                    Math.ceil(
                        32000
                            * relativeTtl
                            / Math.floor(60 * 24 * 365 / BaseConstants.KEY_BLOCK_INTERVAL)))
            * minimalGasPrice;
    return BigInteger.valueOf(fee);
  }
}
