package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;

/**
 * This abstract subclass introduces variables holding information about the inner transaction
 *
 * @param <T> inner transaction, specific class of type {@link AbstractTransactionModel}
 */
@Getter
@SuperBuilder
public abstract class AbstractTransactionWithInnerTx<T extends AbstractTransactionModel<?>>
    extends AbstractTransaction<T> {

  @NonNull protected Bytes innerTxRLPEncodedList;

  public void setInnerTxRLPEncodedList(Bytes innerTxRLPEncodedList) {
    this.innerTxRLPEncodedList = innerTxRLPEncodedList;
  }
}
