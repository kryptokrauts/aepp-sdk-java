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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.DryRunAccount;
import com.kryptokrauts.aeternity.generated.model.DryRunInput;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.domain.transaction.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

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

	@Nonnull
	private DefaultApi compilerApi;

	@Override
	public Single<String> asyncCreateUnsignedTransaction(AbstractTransactionModel<?> tx) {
		return tx.buildTransaction(externalApi, compilerApi)
				.createUnsignedTransaction(config.isNativeMode(), config.getMinimalGasPrice())
				.map(single -> single.getTx());
	}

	@Override
	public String blockingCreateUnsignedTransaction(AbstractTransactionModel<?> tx) {
		return tx.buildTransaction(externalApi, compilerApi)
				.createUnsignedTransaction(config.isNativeMode(), config.getMinimalGasPrice()).blockingGet().getTx();
	}

	@Override
	public Single<PostTxResponse> postTransaction(String signedTx) {
		return externalApi.rxPostTransaction(createTxObject(signedTx));
	}

	@Override
	public Single<PostTransactionResult> asyncPostTransaction(AbstractTransactionModel<?> tx)
			throws TransactionCreateException {
		return PostTransactionResult.builder().build()
				.asyncGet(externalApi.rxPostTransaction(
						createTxObject(signTransaction(asyncCreateUnsignedTransaction(tx).blockingGet(),
								this.config.getBaseKeyPair().getPrivateKey()))));
	}

	@Override
	public PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx)
			throws TransactionCreateException {
		return PostTransactionResult.builder().build()
				.blockingGet(externalApi.rxPostTransaction(
						createTxObject(signTransaction(asyncCreateUnsignedTransaction(tx).blockingGet(),
								this.config.getBaseKeyPair().getPrivateKey()))));
	}

	@Override
	public String computeTxHash(final AbstractTransactionModel<?> tx) throws TransactionCreateException {
		byte[] signed = EncodingUtils.decodeCheckWithIdentifier(signTransaction(
				asyncCreateUnsignedTransaction(tx).blockingGet(), this.config.getBaseKeyPair().getPrivateKey()));
		return EncodingUtils.hashEncode(signed, ApiIdentifiers.TRANSACTION_HASH);
	}

	@Override
	public String signTransaction(final String unsignedTx, final String privateKey) throws TransactionCreateException {
		try {
			byte[] networkData = config.getNetwork().getId().getBytes(StandardCharsets.UTF_8);
			byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier(unsignedTx);
			byte[] txAndNetwork = ByteUtils.concatenate(networkData, binaryTx);
			byte[] sig = SigningUtil.sign(txAndNetwork, privateKey);
			String encodedSignedTx = encodeSignedTransaction(sig, binaryTx);
			return encodedSignedTx;
		} catch (Exception e) {
			throw createException(e);
		}
	}

	@Override
	public Single<DryRunResults> asyncDryRunTransactions(@NonNull List<Map<AccountParameter, Object>> accounts,
			BigInteger block, @NonNull List<String> unsignedTransactions) {
		return this.externalApi.rxDryRunTxs(createDryRunBody(accounts, block, unsignedTransactions));
	}

	@Override
	public DryRunResults blockingDryRunTransactions(@NonNull List<Map<AccountParameter, Object>> accounts,
			BigInteger block, @NonNull List<String> unsignedTransactions) {
		return this.externalApi.rxDryRunTxs(createDryRunBody(accounts, block, unsignedTransactions)).blockingGet();
	}

	private DryRunInput createDryRunBody(List<Map<AccountParameter, Object>> accounts, BigInteger block,
			List<String> unsignedTransactions) {
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
		unsignedTransactions.forEach(item -> body.addTxsItem(item));

		_logger.debug(String.format("Calling dry run on block %s with body %s", block, body));
		return body;
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
		return this.config.getNetwork().getId() + " " + this.config.isNativeMode();
	}

	private TransactionCreateException createException(Exception e) {
		return new TransactionCreateException(String.format("Technical error creating exception: ", e.getMessage()), e);
	}

	private Tx createTxObject(String signedTx) {
		Tx tx = new Tx();
		tx.setTx(signedTx);
		return tx;
	}
}
