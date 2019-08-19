package com.kryptokrauts.aeternity.sdk.service.transaction.impl;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;
import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.DryRunAccount;
import com.kryptokrauts.aeternity.generated.model.DryRunInput;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.GenericTxs;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;

import io.reactivex.Single;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	protected static final Logger _logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Nonnull
	private AeternityServiceConfiguration config;

	@Nonnull
	private ExternalApi externalApi;

	@Override
	public Single<UnsignedTx> createUnsignedTransaction(AbstractTransactionModel<?> tx) {
		return tx.buildTransaction(externalApi).createUnsignedTransaction(config.isNativeMode(),
				config.getMinimalGasPrice());
	}

	@Override
	public Single<PostTxResponse> postTransaction(Tx tx) {
		return externalApi.rxPostTransaction(tx);
	}

	@Override
	public Single<PostTxResponse> postTransaction(AbstractTransactionModel<?> tx) throws CryptoException {
		return externalApi.rxPostTransaction(signTransaction(createUnsignedTransaction(tx).blockingGet(),
				this.config.getBaseKeyPair().getPrivateKey()));
	}

	@Override
	public Single<GenericSignedTx> getTransactionByHash(String txHash) {
		return externalApi.rxGetTransactionByHash(txHash);
	}

	@Override
	public Single<TxInfoObject> getTransactionInfoByHash(String txHash) {
		return externalApi.rxGetTransactionInfoByHash(txHash);
	}

	@Override
	public String computeTxHash(final AbstractTransactionModel<?> tx) throws CryptoException {
		byte[] signed = EncodingUtils
				.decodeCheckWithIdentifier(signTransaction(createUnsignedTransaction(tx).blockingGet(),
						this.config.getBaseKeyPair().getPrivateKey()).getTx());
		return EncodingUtils.hashEncode(signed, ApiIdentifiers.TRANSACTION_HASH);
	}

	@Override
	public Tx signTransaction(final UnsignedTx unsignedTx, final String privateKey) throws CryptoException {
		byte[] networkData = config.getNetwork().getId().getBytes(StandardCharsets.UTF_8);
		byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier(unsignedTx.getTx());
		byte[] txAndNetwork = ByteUtils.concatenate(networkData, binaryTx);
		byte[] sig = SigningUtil.sign(txAndNetwork, privateKey);
		String encodedSignedTx = encodeSignedTransaction(sig, binaryTx);
		Tx tx = new Tx();
		tx.setTx(encodedSignedTx);
		return tx;
	}

	@Override
	public Single<DryRunResults> dryRunTransactions(@NonNull List<Map<AccountParameter, Object>> accounts,
			BigInteger block, @NonNull List<UnsignedTx> unsignedTransactions) {
		DryRunInput body = new DryRunInput();
		// Validate parameters
		ValidationUtil.checkParameters(validate -> Optional.ofNullable(accounts.size() > 0), accounts,
				"dryRunTransactions", Arrays.asList("accounts"), ValidationUtil.NO_ENTRIES);
		ValidationUtil.checkParameters(validate -> Optional.ofNullable(unsignedTransactions.size() > 0), accounts,
				"dryRunTransactions", Arrays.asList("unsignedTransactions"), ValidationUtil.NO_ENTRIES);
		ValidationUtil.checkParameters(validate -> Optional.ofNullable(unsignedTransactions.size() == accounts.size()),
				accounts, "dryRunTransactions", Arrays.asList("unsignedTransactions", "accounts"),
				ValidationUtil.LIST_NOT_SAME_SIZE);

		List<DryRunAccount> dryRunAccounts = new LinkedList<DryRunAccount>();

		for (Map<AccountParameter, Object> txParams : accounts) {
			DryRunAccount currAccount = new DryRunAccount();
			ValidationUtil.checkParameters(validate -> Optional.ofNullable(txParams.size() > 0), accounts,
					"dryRunTransactions", Arrays.asList("accounts.map"), ValidationUtil.NO_ENTRIES);
			ValidationUtil.checkParameters(
					validate -> Optional.ofNullable(txParams.containsKey(AccountParameter.PUBLIC_KEY)), accounts,
					"dryRunTransactions", Arrays.asList("accounts.map.values"), ValidationUtil.MAP_MISSING_VALUE,
					AccountParameter.PUBLIC_KEY);

			currAccount.setPubKey(txParams.get(AccountParameter.PUBLIC_KEY).toString());
			BigInteger amount = txParams.get(AccountParameter.AMOUNT) != null
					? new BigInteger(txParams.get(AccountParameter.AMOUNT).toString())
					: BigInteger.ZERO;
			currAccount.setAmount(amount);

			dryRunAccounts.add(currAccount);
		}

		body.setAccounts(dryRunAccounts);
		if (block != null) {
			body.setTop(block.toString());
		} else {
			body.top(null);
		}
		unsignedTransactions.forEach(item -> body.addTxsItem(item.getTx()));

		_logger.debug(String.format("Calling dry run on block %s with body %s", block, body));
		return this.externalApi.rxDryRunTxs(body);
	}

	/**
	 * @TODO auslagern in INFO Service
	 * @param microBlockHash
	 * @return
	 */
	public Single<GenericTxs> getMicroBlockTransactions(final String microBlockHash) {
		ValidationUtil.checkParameters(
				validate -> Optional.ofNullable(microBlockHash.startsWith(ApiIdentifiers.MICRO_BLOCK_HASH)),
				microBlockHash, "getMicroBlockTransactions", Arrays.asList("microBlockHash", ApiIdentifiers.NAME),
				ValidationUtil.MISSING_API_IDENTIFIER);
//		return this.getExternalApi().rxGetMicroBlockTransactionsByHash(microBlockHash);
		return null;
	}

	/**
	 * @param sig
	 * @param binaryTx
	 * @return encoded transaction
	 */
	private String encodeSignedTransaction(byte[] sig, byte[] binaryTx) {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SIGNED_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			rlpWriter.writeList(writer -> {
				writer.writeByteArray(sig);
			});
			rlpWriter.writeByteArray(binaryTx);
		});
		return EncodingUtils.encodeCheck(encodedRlp.toArray(), ApiIdentifiers.TRANSACTION);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.config.getNetwork().getId() + " " + this.config.isNativeMode();
	}
}
