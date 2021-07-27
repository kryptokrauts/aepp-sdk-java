package com.kryptokrauts.aeternity.sdk.service.info.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.info.InfoService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResults;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import io.reactivex.Single;
import java.util.Arrays;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

  protected static final Logger _logger = LoggerFactory.getLogger(InfoServiceImpl.class);

  @Nonnull private AeternityServiceConfiguration config;

  @Nonnull private ExternalApi externalApi;

  @Override
  public Single<TransactionResult> asyncGetTransactionByHash(String txHash) {
    return TransactionResult.builder()
        .build()
        .asyncGet(externalApi.rxGetTransactionByHash(txHash, false));
  }

  @Override
  public TransactionResult blockingGetTransactionByHash(String txHash) {
    return TransactionResult.builder()
        .build()
        .blockingGet(externalApi.rxGetTransactionByHash(txHash, false));
  }

  @Override
  public Single<TransactionInfoResult> asyncGetTransactionInfoByHash(String txHash) {
    return TransactionInfoResult.builder()
        .build()
        .asyncGet(externalApi.rxGetTransactionInfoByHash(txHash, false));
  }

  @Override
  public TransactionInfoResult blockingGetTransactionInfoByHash(String txHash) {
    return TransactionInfoResult.builder()
        .build()
        .blockingGet(externalApi.rxGetTransactionInfoByHash(txHash, false));
  }

  @Override
  public Single<TransactionResults> asyncGetMicroBlockTransactions(final String microBlockHash) {
    this.validateMicroTxHash(microBlockHash);
    return TransactionResults.builder()
        .build()
        .asyncGet(this.externalApi.rxGetMicroBlockTransactionsByHash(microBlockHash, false));
  }

  @Override
  public TransactionResults blockingGetMicroBlockTransactions(final String microBlockHash) {
    this.validateMicroTxHash(microBlockHash);
    return TransactionResults.builder()
        .build()
        .blockingGet(this.externalApi.rxGetMicroBlockTransactionsByHash(microBlockHash, false));
  }

  @Override
  public StringResultWrapper blockingGetContractByteCode(final String contractId) {
    this.validateContractId(contractId);
    return StringResultWrapper.builder()
        .build()
        .blockingGet(
            this.externalApi
                .rxGetContractCode(contractId, false)
                .map(contactCode -> contactCode.getBytecode()));
  }

  @Override
  public Single<StringResultWrapper> asnycGetContractByteCode(final String contractId) {
    this.validateContractId(contractId);
    return StringResultWrapper.builder()
        .build()
        .asyncGet(
            this.externalApi
                .rxGetContractCode(contractId, false)
                .map(contactCode -> contactCode.getBytecode()));
  }

  private void validateMicroTxHash(final String microBlockHash) {
    ValidationUtil.checkParameters(
        validate -> microBlockHash.startsWith(ApiIdentifiers.MICRO_BLOCK_HASH),
        microBlockHash,
        "getMicroBlockTransactions",
        Arrays.asList("microBlockHash", ApiIdentifiers.NAME),
        ValidationUtil.MISSING_API_IDENTIFIER);
  }

  private void validateContractId(final String contractId) {
    ValidationUtil.checkParameters(
        validate -> contractId.startsWith(ApiIdentifiers.CONTRACT_PUBKEY),
        contractId,
        "getContract",
        Arrays.asList("contractId", ApiIdentifiers.CONTRACT_PUBKEY),
        ValidationUtil.MISSING_API_IDENTIFIER);
  }

  @Override
  public Single<KeyBlockResult> asyncGetCurrentKeyBlock() {
    return KeyBlockResult.builder().build().asyncGet(this.externalApi.rxGetCurrentKeyBlock(false));
  }

  @Override
  public KeyBlockResult blockingGetCurrentKeyBlock() {
    return KeyBlockResult.builder()
        .build()
        .blockingGet(this.externalApi.rxGetCurrentKeyBlock(false));
  }
}
