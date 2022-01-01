package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.CheckTxInPoolResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import io.reactivex.Single;
import java.util.List;

public interface TransactionService {

  /**
   * sign an unsigned transaction with the given private key
   *
   * @param unsignedTx the encoded unsigned transaction
   * @param privateKey the encoded private key to sign the transaction
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
   * convenience function to perform a dry-run for a single tx by providing one of {@link
   * com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction}
   *
   * <p>using the zeroAddress for the dry-run MUST be used if no KeyPair is provided in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}
   *
   * @param contractTx must be of type {@link ContractCreateTransactionModel} or {@link
   *     ContractCallTransactionModel}
   * @param useZeroAddress true to use the zero address which makes sense for a non-stateful
   *     (ready-only) contract call, false if account in configuration should be used to simulate a
   *     stateful tx
   * @return instance of {@link DryRunTransactionResult} if call was successful, otherwise throws
   *     {@link AException}
   */
  DryRunTransactionResult blockingDryRunContractTx(
      AbstractTransactionModel<?> contractTx, boolean useZeroAddress);

  /**
   * synchronously dry run unsigned transactions to estimate gas (!) please make sure to use
   * implementations of {@link List} to ensure correct order of transactions called by accounts
   *
   * @param input instance of {@link DryRunRequest}
   * @return instance of {@link DryRunTransactionResults}
   */
  DryRunTransactionResults blockingDryRunTransactions(DryRunRequest input);

  /**
   * convenience method to deploy a contract and return the tx-hash along with the decoded result
   * and other useful information related to the tx. performs following steps under the hood: 1.
   * generate bytecode via compiler, 2. encode calldata via compiler, 3. sign & broadcast the
   * ContractCreateTx with the keyPair configured in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}, 4. wait for the tx to be included
   * in a microblock, 5. fetch the tx-info from node, 6. decode result calldata via compiler, 7.
   * return the {@link ContractTxResult}
   *
   * @param sourceCode the source code of the contract
   * @param txOptions custom tx-options as defined in the {@link ContractTxOptions} class
   * @return the result of the contract creation (deployment) including the tx-hash and other useful
   *     information related to the tx
   */
  ContractTxResult blockingContractCreate(String sourceCode, ContractTxOptions txOptions);

  /**
   * convenience method to deploy a contract and return the tx-hash along with the decoded result
   * and other useful information related to the tx. performs following steps under the hood: 1.
   * generate bytecode via compiler, 2. encode calldata via compiler, 3. sign & broadcast the
   * ContractCreateTx with the keyPair configured in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}, 4. wait for the tx to be included
   * in a microblock, 5. fetch the tx-info from node, 6. decode result calldata via compiler, 7.
   * return the {@link ContractTxResult}
   *
   * @param sourceCode the source code of the contract
   * @return the result of the contract creation (deployment) including the tx-hash and other useful
   *     information related to the tx
   */
  ContractTxResult blockingContractCreate(String sourceCode);

  /**
   * convenience method to perform a read-only contract call and return the decoded call result.
   * performs following steps under the hood: 1. encode calldata via compiler, 2. dry-run the
   * contract call via node, 3. decode result calldata via compiler
   *
   * @param contractId the id of the contract (ct_...)
   * @param entrypoint the name of the entrypoint
   * @param sourceCode the source code of the contract
   * @param txOptions custom tx-options as defined in the {@link ContractTxOptions} class. only the
   *     attributes "params" and "filesystem" are relevant here. other attributes will be ignored
   * @return the decoded rawResult as String
   */
  Object blockingReadOnlyContractCall(
      String contractId, String entrypoint, String sourceCode, ContractTxOptions txOptions);

  /**
   * convenience method to perform a read-only contract call and return the decoded call result.
   * performs following steps under the hood: 1. encode calldata via compiler, 2. dry-run the
   * contract call via node, 3. decode result calldata via compiler
   *
   * @param contractId the id of the contract (ct_...)
   * @param entrypoint the name of the entrypoint
   * @param sourceCode the source code of the contract
   * @return the decoded rawResult as String
   */
  Object blockingReadOnlyContractCall(String contractId, String entrypoint, String sourceCode);

  /**
   * convenience method to perform a stateful contract call and return the tx-hash along with the
   * decoded result and other useful information related to the tx. performs following steps under
   * the hood: 1. encode calldata via compiler, 2. sign & broadcast the ContractCallTx with the
   * keyPair configured in the {@link com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration},
   * 3. wait for the tx to be included in a microblock, 4. fetch the tx-info from node, 5. decode
   * result calldata via compiler, 6. return the {@link ContractTxResult}
   *
   * @param contractId the id of the contract (ct_...)
   * @param entrypoint the name of the entrypoint
   * @param sourceCode the source code of the contract
   * @param txOptions custom tx-options as defined in the {@link ContractTxOptions} class
   * @return the result of the contract call including the tx-hash and other useful information
   *     related to the tx
   */
  ContractTxResult blockingStatefulContractCall(
      String contractId, String entrypoint, String sourceCode, ContractTxOptions txOptions);

  /**
   * convenience method to perform a stateful contract call and return the tx-hash along with the
   * decoded result and other useful information related to the tx. performs following steps under
   * the hood: 1. encode calldata via compiler, 2. sign & broadcast the ContractCallTx with the
   * keyPair configured in the {@link com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration},
   * 3. wait for the tx to be included in a microblock, 4. fetch the tx-info from node, 5. decode
   * result calldata via compiler, 6. return the {@link ContractTxResult}
   *
   * @param contractId the id of the contract (ct_...)
   * @param entrypoint the name of the entrypoint
   * @param sourceCode the source code of the contract
   * @return the result of the contract call including the tx-hash and other useful information
   *     related to the tx
   */
  ContractTxResult blockingStatefulContractCall(
      String contractId, String entrypoint, String sourceCode);

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
   *     <p>Note: @Override
   *     <p>- check getRootErrorMessage(): if a rootErrorMessage is present the transaction is
   *     probably not mined.
   *     <p>- check getBlockHeight():if the blockHeight is -1 it means the transaction isn't mined.
   */
  Single<TransactionResult> asyncWaitForConfirmation(String txHash, int numOfConfirmations);

  /**
   * sign an unsigned transaction with the given private key. method uses an additional prefix and
   * must be used to sign inner transactions of PayingForTx
   *
   * @param model of the inner tx to be signed
   * @param privateKey the encoded private key to sign the transaction
   * @return signed and encoded transaction
   * @throws TransactionCreateException if an error occurs
   */
  String signPayingForInnerTransaction(
      final AbstractTransactionModel<?> model, final String privateKey)
      throws TransactionCreateException;

  /**
   * synchronously creates an unsigned transaction out of the given AbstractTransactionModel and
   * append network data to the resulting byte array. This hash is intended to be used for
   * generalized accounts which provide their own authentication mechanism
   *
   * @param tx instance of a specific TransactionModel-class that extends {@link
   *     AbstractTransactionModel}
   * @return hash of created unsigned transaction appended with network data
   * @throws TransactionCreateException
   */
  String computeGAInnerTxHash(AbstractTransactionModel<?> tx) throws TransactionCreateException;

  /**
   * @param txHash the tx-hash of the transaction to be confirmed
   * @return info if tx can be included by miners or if it is blocked for some reason
   */
  CheckTxInPoolResult blockingCheckTxInPool(String txHash);
}
