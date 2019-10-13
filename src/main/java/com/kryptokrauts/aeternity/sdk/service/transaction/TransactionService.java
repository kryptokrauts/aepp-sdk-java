package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import io.reactivex.Single;
import java.util.List;

public interface TransactionService {

  /**
   * [@PURPOSE-DEBUG] synchronously sign an unsigned transaction with the given private key
   *
   * @param unsignedTx
   * @param privateKey
   * @return signed and encoded transaction
   * @throws TransactionCreateException
   */
  String signTransaction(String unsignedTx, String privateKey) throws TransactionCreateException;

  /**
   * asynchronously creates an unsignedTx object for further processing and especially abstracts the
   * fee calculation for this transaction thus this is an
   *
   * @param tx transaction typed model, one of {link AbstractTransaction}
   * @return a single-wrapped unsignedTx object
   */
  Single<String> asyncCreateUnsignedTransaction(AbstractTransactionModel<?> tx);

  /**
   * synchronously creates an unsignedTx object for further processing and especially abstracts the
   * fee calculation for this transaction thus this is an
   *
   * @param tx transaction typed model, one of {link AbstractTransaction}
   * @return a single-wrapped unsignedTx object
   */
  String blockingCreateUnsignedTransaction(AbstractTransactionModel<?> tx);

  /**
   * asynchronously dry run unsigned transactions to estimate gas (!) please make sure to use
   * implementations of {@link List} to ensure correct order of transactions called by accounts
   *
   * @param accounts
   * @param block
   * @param unsignedTransactions
   * @return asynchronous result handler (RxJava Single) for @{DryRunTransactionResults}
   */
  Single<DryRunTransactionResults> asyncDryRunTransactions(DryRunRequest input);

  /**
   * synchronously dry run unsigned transactions to estimate gas (!) please make sure to use
   * implementations of {@link List} to ensure correct order of transactions called by accounts
   *
   * @param accounts
   * @param block
   * @param unsignedTransactions
   * @return
   */
  DryRunTransactionResults blockingDryRunTransactions(DryRunRequest input);

  /**
   * asynchronously post a transaction for given model
   *
   * @param tx
   * @param privateKey the privateKey to sign the tx
   * @return asynchronous result handler (RxJava Single) for {PostTransactionResult}
   */
  Single<PostTransactionResult> asyncPostTransaction(
      AbstractTransactionModel<?> tx, String privateKey);

  /**
   * asynchronously post a transaction for given model with the private key stored in the
   * configuration
   *
   * @param tx
   * @return asynchronous result handler (RxJava Single) for {PostTransactionResult}
   */
  Single<PostTransactionResult> asyncPostTransaction(AbstractTransactionModel<?> tx);

  /**
   * synchronously post a transaction for given model
   *
   * @param tx
   * @param privateKey the privateKey to sign the tx
   * @return
   */
  PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx, String privateKey);

  /**
   * synchronously post a transaction for given model with the private key stored in the
   * configuration
   *
   * @param tx
   * @return
   */
  PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx);

  /**
   * [@PURPOSE-DEBUG] synchronously compute the transaction hash for the given transaction model
   *
   * @param transaction object
   * @return the hash from a signed and encoded transaction
   * @throws TransactionCreateException
   */
  String computeTxHash(AbstractTransactionModel<?> tx) throws TransactionCreateException;

  /**
   * synchronously post a transaction based on the given signedTx as String
   *
   * @param signedTx
   * @return
   */
  PostTransactionResult blockingPostTransaction(String signedTx);

  /**
   * asynchronously post a transaction based on the given signedTx as String
   *
   * @param tx
   * @return asynchronous result handler (RxJava Single) for {PostTransactionResult}
   */
  Single<PostTransactionResult> asyncPostTransaction(String signedTx);
}
