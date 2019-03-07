package com.kryptokrauts.aeternity.sdk.service.transaction.fee;

import java.math.BigInteger;

public interface FeeCalculationModel {

  /**
   * Calculates the fee based on the calculation model if the calculation needs informations from
   * the transaction object, they need to be passed via constructor of the fee calculation model
   * implementation class
   *
   * @param tx_byte_size transaction size in bytes
   * @param minimalGasPrice minimal gas price
   * @return the actual fee
   */
  BigInteger calculateFee(int tx_byte_size, long minimalGasPrice);
}
