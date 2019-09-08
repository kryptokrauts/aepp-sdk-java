package com.kryptokrauts.aeternity.sdk.service.info.impl;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.info.InfoService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResults;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;

import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

	protected static final Logger _logger = LoggerFactory.getLogger(InfoServiceImpl.class);

	@Nonnull
	private AeternityServiceConfiguration config;

	@Nonnull
	private ExternalApi externalApi;

	@Override
	public Single<TransactionResult> asyncGetTransactionByHash(String txHash) {
		return TransactionResult.builder().build().asyncGet(externalApi.rxGetTransactionByHash(txHash));
	}

	@Override
	public TransactionResult blockingGetTransactionByHash(String txHash) {
		return TransactionResult.builder().build().blockingGet(externalApi.rxGetTransactionByHash(txHash));
	}

	@Override
	public Single<TransactionInfoResult> asyncGetTransactionInfoByHash(String txHash) {
		return TransactionInfoResult.builder().build().asyncGet(externalApi.rxGetTransactionInfoByHash(txHash));
	}

	@Override
	public TransactionInfoResult blockingGetTransactionInfoByHash(String txHash) {
		return TransactionInfoResult.builder().build().blockingGet(externalApi.rxGetTransactionInfoByHash(txHash));
	}

	@Override
	public Single<TransactionResults> asyncGetMicroBlockTransactions(final String microBlockHash) {
		this.validateMicroTxHash(microBlockHash);
		return TransactionResults.builder().build()
				.asyncGet(this.externalApi.rxGetMicroBlockTransactionsByHash(microBlockHash));
	}

	@Override
	public TransactionResults blockingGetMicroBlockTransactions(final String microBlockHash) {
		this.validateMicroTxHash(microBlockHash);
		return TransactionResults.builder().build()
				.blockingGet(this.externalApi.rxGetMicroBlockTransactionsByHash(microBlockHash));
	}

	private void validateMicroTxHash(final String microBlockHash) {
		ValidationUtil.checkParameters(
				validate -> Optional.ofNullable(microBlockHash.startsWith(ApiIdentifiers.MICRO_BLOCK_HASH)),
				microBlockHash, "getMicroBlockTransactions", Arrays.asList("microBlockHash", ApiIdentifiers.NAME),
				ValidationUtil.MISSING_API_IDENTIFIER);
	}

	@Override
	public Single<KeyBlockResult> asyncGetCurrentKeyBlock() {
		return KeyBlockResult.builder().build().asyncGet(this.externalApi.rxGetCurrentKeyBlock());
	}

	@Override
	public KeyBlockResult blockingGetCurrentKeyBlock() {
		return KeyBlockResult.builder().build().blockingGet(this.externalApi.rxGetCurrentKeyBlock());
	}
}
