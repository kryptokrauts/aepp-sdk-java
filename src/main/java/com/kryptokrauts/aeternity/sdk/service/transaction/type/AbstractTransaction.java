package com.kryptokrauts.aeternity.sdk.service.transaction.type;

import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import lombok.experimental.SuperBuilder;
import net.consensys.cava.bytes.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this abstract class is the base of all transactions and wraps the calculation of transaction fees
 * by using a default fee, generating the RLP and gaining the byte size of the transaction
 *
 * @param <TxModel>
 */
@SuperBuilder
public abstract class AbstractTransaction<TxModel> {

  private static final Logger _logger = LoggerFactory.getLogger(AbstractTransaction.class);

  protected BigInteger fee;

  /** fee calculation model for this transaction type, one of {@link FeeCalculationModel} */
  protected FeeCalculationModel feeCalculationModel;

  /**
   * generates a Bytes object from the attributes. this is necessary for calculating the fee based
   * on RLP encoding
   *
   * @return {@link Bytes}
   */
  protected abstract Bytes createRLPEncodedList();

  /**
   * this method needs to be implemented for testing purposes (non native mode)
   *
   * @return a single-wrapped unsignedTx object
   */
  protected abstract <T extends UnsignedTx> Single<T> createInternal();

  /**
   * this method needs to be implemented for testing purposes (non native mode) and returns the
   * generated tx model from the transaction fields
   *
   * @return one of {@link com.kryptokrauts.aeternity.generated.model}
   */
  protected abstract TxModel toModel();

  /**
   * this method creates an unsigned transaction whether in native or debug mode if no fee is
   * defined (null), the fee will be calculated based on the transactions byte size and maybe other
   * transaction attributes using the fee calculation model
   *
   * @param nativeMode native or debug mode
   * @param minimalGasPrice the minimal gas price, which the fee is multiplied with *
   * @return a single-wrapped unsignedTx object
   */
  public Single<UnsignedTx> createUnsignedTransaction(boolean nativeMode, long minimalGasPrice) {
    if (nativeMode) {
      /** if no fee is given - use default fee to create a tx and get its size */
      if (fee == null) {
        fee = BigInteger.ONE.shiftLeft(48);
        Bytes encodedRLPArray = createRLPEncodedList();
        int byte_size = encodedRLPArray.bitLength() / 8;
        /** now calculate fee based on tx size */
        fee = feeCalculationModel.calculateFee(byte_size, minimalGasPrice);
        _logger.debug(
            String.format(
                "Using calculation model %s the following fee was calculated %s",
                feeCalculationModel.getClass().getName(), fee));
      } else {
        _logger.warn(
            "You defined a custom transaction fee which might be not sufficient to execute the transaction!");
      }
      /** create final RLP encoded representation of the transaction */
      Bytes encodedRLPArrayWithFee = createRLPEncodedList();

      return Single.just(
          new UnsignedTx()
              .tx(
                  EncodingUtils.encodeCheck(
                      encodedRLPArrayWithFee.toArray(), ApiIdentifiers.TRANSACTION)));
    }
    return createInternal();
  }

  /** @return the calculated or given fee for this transaction */
  public BigInteger getFee() {
    return fee;
  }

  /**
   * this method can be used to set a self-defined fee. otherwise the fee will automatically
   * calculated
   *
   * @param fee a self-defined fee
   */
  public void setFee(BigInteger fee) {
    this.fee = fee;
  }

  /**
   * @return the transaction model which is actually used to create the remote call to the generated
   *     api functions (classes of package {@link com.kryptokrauts.aeternity.generated.api.rxjava})
   */
  public TxModel getApiModel() {
    return toModel();
  }
}
