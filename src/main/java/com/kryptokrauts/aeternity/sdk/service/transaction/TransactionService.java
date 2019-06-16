package com.kryptokrauts.aeternity.sdk.service.transaction;

import org.bouncycastle.crypto.CryptoException;

import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.TransactionFactory;

import io.reactivex.Single;

public interface TransactionService {

	Single<PostTxResponse> postTransaction(Tx tx);

	Single<GenericSignedTx> getTransactionByHash(String txHash);

	/**
	 * @param encodedSignedTx an encoded signed transaction
	 * @return the hash from a signed and encoded transaction
	 */
	String computeTxHash(String encodedSignedTx);

	/**
	 * @param unsignedTx
	 * @param privateKey
	 * @return signed and encoded transaction
	 * @throws CryptoException
	 */
	Tx signTransaction(UnsignedTx unsignedTx, String privateKey) throws CryptoException;

	/**
	 * creates an unsignedTx object for further processing and especially abstracts
	 * the fee calculation for this transaction
	 *
	 * @param tx transaction typed model, one of {link AbstractTransaction}
	 * @return a single-wrapped unsignedTx object
	 */
	Single<UnsignedTx> createUnsignedTransaction(AbstractTransaction<?> tx);

	TransactionFactory getTransactionFactory();

	/**
	 * gets the information object for a tx hash
	 * 
	 * @param txHash
	 * @return
	 */
	Single<TxInfoObject> getTransactionInfoByHash(String txHash);
}
