package com.kryptokrauts.aeternity.sdk.service.transaction.type;

import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.BaseFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLPWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this abstract class is the base of all transactions and wraps the calculation of transaction fees
 * by using a default fee, generating the RLP and gaining the byte size of the transaction
 *
 * @param <TxModel> a TransactionModel class that extends {@link AbstractTransactionModel}
 */
@SuperBuilder
public abstract class AbstractTransaction<TxModel extends AbstractTransactionModel<?>> {

  private static final Logger _logger = LoggerFactory.getLogger(AbstractTransaction.class);

  @NonNull protected TxModel model;

  /**
   * fee calculation model for this transaction type, one of {@link FeeCalculationModel}
   *
   * @return instance of the {@link FeeCalculationModel} to use for fee calculation
   */
  protected FeeCalculationModel getFeeCalculationModel() {
    return new BaseFeeCalculationModel();
  }

  /**
   * generates a Bytes object from the attributes. this is necessary for calculating the fee based
   * on RLP encoding
   *
   * @return {@link Bytes}
   */
  public abstract Bytes createRLPEncodedList();

  /**
   * this method needs to be implemented for testing purposes (non native mode)
   *
   * @param <T> type that extends {@link UnsignedTx}
   * @return a single-wrapped unsignedTx object
   */
  protected abstract <T extends UnsignedTx> Single<T> createInternal();

  /**
   * this method creates an unsigned transaction whether in native or debug mode if no fee is
   * defined (null), the fee will be calculated based on the transactions byte size and maybe other
   * transaction attributes using the fee calculation model
   *
   * @param nativeMode native or debug mode
   * @param minimalGasPrice the minimal gas price, which the fee is multiplied with
   * @return a single-wrapped unsignedTx object
   */
  public Single<UnsignedTx> createUnsignedTransaction(boolean nativeMode, long minimalGasPrice) {
    /** before creating the unsigned transaction we validate the input */
    String field = model.checkMandatoryFields();
    if (field != null) {
      throw new InvalidParameterException(
          "Attribute \"" + field + "\" is mandatory - please set value");
    }
    model.validateInput();
    if (nativeMode) {
      /** if no fee is given - use default fee to create a tx and get its size */
      if (model.getFee() == null) {
        model.setFee(BigInteger.ONE.shiftLeft(48));
        Bytes encodedRLPArray = createRLPEncodedList();
        int byte_size = encodedRLPArray.bitLength() / 8;
        /** now calculate fee based on tx size */
        model.setFee(getFeeCalculationModel().calculateFee(byte_size, minimalGasPrice, this));
        _logger.info(
            String.format(
                "Using calculation model %s the following fee was calculated %s",
                getFeeCalculationModel().getClass().getName(), model.getFee()));
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

  /**
   * check if value is 0 <br>
   * if so, serialize as byte, otherwise serialize value as BigInteger
   *
   * @param rlpWriter instance of {@link RLPWriter}
   * @param value the value to check for {@link BigInteger#ZERO}
   */
  protected void checkZeroAndWriteValue(RLPWriter rlpWriter, BigInteger value) {
    if (BigInteger.ZERO.equals(value)) {
      rlpWriter.writeByte((byte) 0);
    } else {
      rlpWriter.writeBigInteger(value);
    }
  }

  public TxModel getModel() {
    return model;
  }
}
