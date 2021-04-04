package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
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
   * wrap into a signed tx with empty list of signatures (to be used for Generalized Accounts)
   *
   * @param unsignedTx a raw unsigned transaction
   * @return wrapped signed and encoded transaction
   */
  String wrapSignedTransactionForGA(String unsignedTx);

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

  /**
   * asynchronously wait for a transaction to be confirmed
   *
   * <p>the number of keyblocks to consider the transaction confirmed is defined in the property
   * numOfConfirmations of the {@link
   * com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration} (default 10)
   *
   * @param txHash the tx-hash of the transaction to be confirmed
   * @return the actual {@link TransactionResult} of the transaction at the confirmation height
   *     <p>Note:
   *     <p>- check getRootErrorMessage(): if a rootErrorMessage is present the transaction is
   *     probably not mined.
   *     <p>- check getBlockHeight(): if the blockHeight is -1 it means the transaction isn't mined.
   */
  Single<TransactionResult> asyncWaitForConfirmation(String txHash);

  /**
   * asynchronously wait for a transaction to be confirmed
   *
   * @param txHash the tx-hash of the transaction to be confirmed
   * @param numOfConfirmations the amount of keyblocks required to consider a transaction to be
   *     confirmed/mined
   * @return the actual {@link TransactionResult} of the transaction at the confirmation height
   *     <p>Note:
   *     <p>- check getRootErrorMessage(): if a rootErrorMessage is present the transaction is
   *     probably not mined.
   *     <p>- check getBlockHeight():if the blockHeight is -1 it means the transaction isn't mined.
   */
  Single<TransactionResult> asyncWaitForConfirmation(String txHash, int numOfConfirmations);
}
