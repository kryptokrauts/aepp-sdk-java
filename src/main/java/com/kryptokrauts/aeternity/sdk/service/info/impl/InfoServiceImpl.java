package com.kryptokrauts.aeternity.sdk.service.info.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.GenericTxs;
import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.info.InfoService;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import io.reactivex.Single;
import java.util.Arrays;
import java.util.Optional;
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
  public Single<GenericSignedTx> getTransactionByHash(String txHash) {
    return externalApi.rxGetTransactionByHash(txHash);
  }

  @Override
  public Single<TxInfoObject> getTransactionInfoByHash(String txHash) {
    return externalApi.rxGetTransactionInfoByHash(txHash);
  }

  @Override
  public Single<GenericTxs> getMicroBlockTransactions(final String microBlockHash) {
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(microBlockHash.startsWith(ApiIdentifiers.MICRO_BLOCK_HASH)),
        microBlockHash,
        "getMicroBlockTransactions",
        Arrays.asList("microBlockHash", ApiIdentifiers.NAME),
        ValidationUtil.MISSING_API_IDENTIFIER);
    return this.externalApi.rxGetMicroBlockTransactionsByHash(microBlockHash);
  }

  @Override
  public Single<KeyBlock> asyncGetCurrentKeyBlock() {
    return this.externalApi.rxGetCurrentKeyBlock();
  }

  @Override
  public KeyBlock blockingGetCurrentKeyBlock() {
    return this.externalApi.rxGetCurrentKeyBlock().blockingGet();
  }
}
