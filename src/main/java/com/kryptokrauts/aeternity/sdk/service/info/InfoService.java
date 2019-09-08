package com.kryptokrauts.aeternity.sdk.service.info;

import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResults;

import io.reactivex.Single;

public interface InfoService {

	/**
	 * get the transaction object for given tx hash
	 *
	 * @param txHash
	 * @return
	 */
	Single<TransactionResult> asyncGetTransactionByHash(String txHash);

	TransactionResult blockingGetTransactionByHash(String txHash);

	/**
	 * gets the information object for given tx hash
	 *
	 * @param txHash
	 * @return
	 */
	Single<TransactionInfoResult> asyncGetTransactionInfoByHash(String txHash);

	TransactionInfoResult blockingGetTransactionInfoByHash(String txHash);

	/**
	 * get transaction object for given microblock tx hash
	 *
	 * @param microBlockHash
	 * @return
	 */
	Single<TransactionResults> asyncGetMicroBlockTransactions(String microBlockHash);

	TransactionResults blockingGetMicroBlockTransactions(String microBlockHash);

	/**
	 * retrieve the current keyblock
	 * 
	 * @return the current keyblock
	 */
	Single<KeyBlockResult> asyncGetCurrentKeyBlock();

	KeyBlockResult blockingGetCurrentKeyBlock();
}
