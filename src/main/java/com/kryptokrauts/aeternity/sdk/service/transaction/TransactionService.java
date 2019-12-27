package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import io.reactivex.Single;
import java.util.List;

public interface TransactionService {

  /**
   * synchronously sign an unsigned transaction with the given private key
   *
   * @param unsignedTx a raw unsigned transaction
   * @param privateKey the private key to sign the transaction
   * @return signed and encoded transaction
   * @throws TransactionCreateException if an error occurs
   */
  String signTransaction(String unsignedTx, String privateKey) throws TransactionCreateException;

  /**
   * asynchronously creates an unsignedTx object for further processing and especially abstracts the
   * fee calculation for this transaction
   *
   * @param tx transaction typed model, one of {@link
   *     com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction}
   * @return a single-wrapped unsignedTx object
   */
  Single<StringResultWrapper> asyncCreateUnsignedTransaction(AbstractTransactionModel<?> tx);

  /**
   * synchronously creates an unsignedTx object for further processing and especially abstracts the
   * fee calculation for this transaction
   *
   * @param tx transaction typed model, one of {@link
   *     com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction}
   * @return a single-wrapped unsignedTx object
   */
  StringResultWrapper blockingCreateUnsignedTransaction(AbstractTransactionModel<?> tx);

  /**
   * asynchronously dry run unsigned transactions to estimate gas (!) please make sure to use
   * implementations of {@link List} to ensure correct order of transactions called by accounts
   *
   * @param input {@link DryRunRequest} object
   * @return asynchronous result handler (RxJava Single) for {@link DryRunTransactionResults}
   */
  Single<DryRunTransactionResults> asyncDryRunTransactions(DryRunRequest input);

  /**
   * synchronously dry run unsigned transactions to estimate gas (!) please make sure to use
   * implementations of {@link List} to ensure correct order of transactions called by accounts
   *
   * @param input instance of {@link DryRunRequest}
   * @return instance of {@link DryRunTransactionResults}
   */
  DryRunTransactionResults blockingDryRunTransactions(DryRunRequest input);

  /**
   * asynchronously post a transaction for given model
   *
   * @param tx instance of a specific TransactionModel-class that extends {@link
   *     AbstractTransactionModel}
   * @param privateKey the privateKey to sign the tx
   * @return asynchronous result handler (RxJava Single) for {@link PostTransactionResult}
   */
  Single<PostTransactionResult> asyncPostTransaction(
      AbstractTransactionModel<?> tx, String privateKey);

  /**
   * asynchronously post a transaction for given model with the private key stored in the
   * configuration
   *
   * @param tx instance of a specific TransactionModel-class that extends {@link
   *     AbstractTransactionModel}
   * @return asynchronous result handler (RxJava Single) for {@link PostTransactionResult}
   */
  Single<PostTransactionResult> asyncPostTransaction(AbstractTransactionModel<?> tx);

  /**
   * synchronously post a transaction for given model
   *
   * @param tx instance of a specific TransactionModel-class that extends {@link
   *     AbstractTransactionModel}
   * @param privateKey the privateKey to sign the tx
   * @return instance of {@link PostTransactionResult}
   */
  PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx, String privateKey);

  /**
   * synchronously post a transaction for given model with the private key stored in the
   * configuration
   *
   * @param tx instance of a specific TransactionModel-class that extends {@link
   *     AbstractTransactionModel}
   * @return instance of {@link PostTransactionResult}
   */
  PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx);

  /**
   * [@PURPOSE-DEBUG] synchronously compute the transaction hash for the given transaction model
   *
   * @param tx instance of a specific TransactionModel-class that extends {@link
   *     AbstractTransactionModel}
   * @return the hash from a signed and encoded transaction
   * @throws TransactionCreateException if an error occurs
   */
  String computeTxHash(AbstractTransactionModel<?> tx) throws TransactionCreateException;

  /**
   * synchronously post a transaction based on the given signedTx as String
   *
   * @param signedTx a signed and encoded transaction
   * @return instance of {@link PostTransactionResult}
   */
  PostTransactionResult blockingPostTransaction(String signedTx);

  /**
   * asynchronously post a transaction based on the given signedTx as String
   *
   * @param signedTx a signed and encoded transaction
   * @return asynchronous result handler (RxJava Single) for {@link PostTransactionResult}
   */
  Single<PostTransactionResult> asyncPostTransaction(String signedTx);
}
