package com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleExtendTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleQueryTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRegisterTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRespondTransaction;
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
    // TODO seems like this isn't always the relative TTL and calculate fee might be higher than
    // expected
    if (transaction instanceof OracleRegisterTransaction) {
      relativeTtl = ((OracleRegisterTransaction) transaction).getModel().getOracleTtl().longValue();
    } else if (transaction instanceof OracleExtendTransaction) {
      relativeTtl = ((OracleExtendTransaction) transaction).getModel().getRelativeTtl().longValue();
    } else if (transaction instanceof OracleQueryTransaction) {
      relativeTtl = ((OracleQueryTransaction) transaction).getModel().getQueryTtl().longValue();
    } else if (transaction instanceof OracleRespondTransaction) {
      relativeTtl =
          ((OracleRespondTransaction) transaction).getModel().getResponseTtl().longValue();
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
