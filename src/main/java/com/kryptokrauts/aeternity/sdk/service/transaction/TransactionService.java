package com.kryptokrauts.aeternity.sdk.service.transaction;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.bouncycastle.crypto.CryptoException;

import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.domain.transaction.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;

import io.reactivex.Single;

public interface TransactionService {

	/**
	 * This method is for testing the signing process and needs to be removed
	 * 
	 * @param tx
	 * @return
	 */
	Single<PostTxResponse> postTransaction(String signedTx);

	/**
	 * @param unsignedTx
	 * @param privateKey
	 * @return signed and encoded transaction
	 * @throws CryptoException
	 */
	String signTransaction(String unsignedTx, String privateKey) throws TransactionCreateException;

	/**
	 * creates an unsignedTx object for further processing and especially abstracts
	 * the fee calculation for this transaction thus this is an
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
	 * dry run unsigned transactions to estimate gas (!) please make sure to use
	 * implementations of {@link List} to ensure correct order of transactions
	 * called by accounts
	 *
	 * @param accounts
	 * @param block
	 * @param unsignedTransactions
	 * @return
	 */
	Single<DryRunResults> asyncDryRunTransactions(List<Map<AccountParameter, Object>> accounts, BigInteger block,
			List<String> unsignedTransactions);

	DryRunResults blockingDryRunTransactions(List<Map<AccountParameter, Object>> accounts, BigInteger block,
			List<String> unsignedTransactions);

	/**
	 * async post a transaction for given model
	 * 
	 * @param tx
	 * @return
	 * @throws TransactionCreateException
	 */
	Single<PostTransactionResult> asyncPostTransaction(AbstractTransactionModel<?> tx)
			throws TransactionCreateException;

	/**
	 * @see asyncPostTransaction
	 * @param tx
	 * @return
	 * @throws TransactionCreateException
	 */
	PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx) throws TransactionCreateException;

	/**
	 * @param transaction object
	 * @return the hash from a signed and encoded transaction
	 */
	String computeTxHash(AbstractTransactionModel<?> tx) throws TransactionCreateException;

}
