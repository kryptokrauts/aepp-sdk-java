package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import io.reactivex.Single;
import java.util.List;
import org.bouncycastle.crypto.CryptoException;

public interface TransactionService {

  /**
   * @param unsignedTx
   * @param privateKey
   * @return signed and encoded transaction
   * @throws CryptoException
   */
  String signTransaction(String unsignedTx, String privateKey) throws TransactionCreateException;

  /**
   * creates an unsignedTx object for further processing and especially abstracts the fee
   * calculation for this transaction thus this is an
   *
   * @param tx transaction typed model, one of {link AbstractTransaction}
   * @return a single-wrapped unsignedTx object
   */
  Single<String> asyncCreateUnsignedTransaction(AbstractTransactionModel<?> tx);

  /**
   * @see asyncCreateUnsignedTransaction
   * @param tx
   * @return
   */
  String blockingCreateUnsignedTransaction(AbstractTransactionModel<?> tx);

  /**
   * dry run unsigned transactions to estimate gas (!) please make sure to use implementations of
   * {@link List} to ensure correct order of transactions called by accounts
   *
   * @param accounts
   * @param block
   * @param unsignedTransactions
   * @return
   */
  Single<DryRunTransactionResults> asyncDryRunTransactions(DryRunRequest input);

  DryRunTransactionResults blockingDryRunTransactions(DryRunRequest input);

  /**
   * async post a transaction for given model
   *
   * @param tx
   * @param privateKey the privateKey to sign the tx, if none is given it will be
   * @return
   * @throws TransactionCreateException
   */
  Single<PostTransactionResult> asyncPostTransaction(
      AbstractTransactionModel<?> tx, String privateKey);

  /**
   * async post a transaction for given model
   *
   * @param tx
   * @return
   * @throws TransactionCreateException
   */
  Single<PostTransactionResult> asyncPostTransaction(AbstractTransactionModel<?> tx);

  PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx, String privateKey);

  PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx);

  /**
   * @param transaction object
   * @return the hash from a signed and encoded transaction
   */
  String computeTxHash(AbstractTransactionModel<?> tx) throws TransactionCreateException;
}
