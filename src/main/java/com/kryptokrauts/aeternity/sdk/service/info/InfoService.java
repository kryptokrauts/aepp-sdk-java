package com.kryptokrauts.aeternity.sdk.service.info;

import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResults;
import io.reactivex.Single;

public interface InfoService {

  /**
   * asynchronously get the transaction object for given tx hash
   *
   * @param txHash
   * @return asynchronous result handler (RxJava Single) for {@link TransactionResult}
   */
  Single<TransactionResult> asyncGetTransactionByHash(String txHash);

  /**
   * synchronously get the transaction object for given tx hash
   *
   * @param txHash
   * @return result of {@link TransactionResult}
   */
  TransactionResult blockingGetTransactionByHash(String txHash);

  /**
   * asynchronously gets the information object for given tx hash
   *
   * @param txHash
   * @return asynchronous result handler (RxJava Single) for {@link TransactionInfoResult}
   */
  Single<TransactionInfoResult> asyncGetTransactionInfoByHash(String txHash);

  /**
   * synchronously gets the information object for given tx hash
   *
   * @param txHash
   * @return result of {@link TransactionResult}
   */
  TransactionInfoResult blockingGetTransactionInfoByHash(String txHash);

  /**
   * asynchronously get transaction object for given microblock tx hash
   *
   * @param microBlockHash
   * @return asynchronous result handler (RxJava Single) for {@link TransactionResults}
   */
  Single<TransactionResults> asyncGetMicroBlockTransactions(String microBlockHash);

  /**
   * synchronously get transaction object for given microblock tx hash
   *
   * @param microBlockHash
   * @return result for {@link TransactionResults}
   */
  TransactionResults blockingGetMicroBlockTransactions(String microBlockHash);

  /**
   * asynchronously retrieve the current keyblock
   *
   * @return asynchronous result handler (RxJava Single) for {@link KeyBlockResult}
   */
  Single<KeyBlockResult> asyncGetCurrentKeyBlock();

  /**
   * synchronously retrieve the current keyblock
   *
   * @return result for {@link KeyBlockResult}
   */
  KeyBlockResult blockingGetCurrentKeyBlock();

  /**
   * asynchronously retrieve the contracts byteCode
   *
   * @param contractId
   * @return
   */
  Single<StringResultWrapper> asnycGetContractByteCode(String contractId);

  /**
   * synchronously retrieve the contracts byteCode
   *
   * @param contractId
   * @return
   */
  StringResultWrapper blockingGetContractByteCode(String contractId);
}
