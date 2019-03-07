package com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import java.math.BigInteger;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OracleFeeCalculationModel implements FeeCalculationModel {

  private long relativeTTL;

  /**
   * the fee is calculated according to the following formula
   *
   * <p>ceiling(32000 * RelativeTTL / floor(60 * 24 * 365 / key_block_interval))+
   * byte_size(OracleRegisterTx) * GasPerByte
   */
  @Override
  public BigInteger calculateFee(int tx_byte_size, long minimalGasPrice) {
    long fee =
        (BaseConstants.BASE_GAS + tx_byte_size * BaseConstants.GAS_PER_BYTE) * minimalGasPrice;
    fee +=
        Math.ceil(
            32000 * relativeTTL / Math.floor(60 * 24 * 365 / BaseConstants.KEY_BLOCK_INTERVAL));
    return BigInteger.valueOf(fee);
  }
}
