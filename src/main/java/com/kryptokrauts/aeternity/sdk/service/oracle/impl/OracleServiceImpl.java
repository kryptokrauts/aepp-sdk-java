package com.kryptokrauts.aeternity.sdk.service.oracle.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.oracle.OracleService;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleQueriesResult;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleQueryResult;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.QueryParams;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.RegisteredOracleResult;
import io.reactivex.Single;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OracleServiceImpl implements OracleService {

  @Nonnull private AeternityServiceConfiguration config;

  @Nonnull private ExternalApi externalApi;

  @Override
  public Single<RegisteredOracleResult> asyncGetRegisteredOracle(String publicKey) {
    return RegisteredOracleResult.builder()
        .build()
        .asyncGet(externalApi.rxGetOracleByPubkey(publicKey));
  }

  @Override
  public RegisteredOracleResult blockingGetRegisteredOracle(String publicKey) {
    return RegisteredOracleResult.builder()
        .build()
        .blockingGet(externalApi.rxGetOracleByPubkey(publicKey));
  }

  @Override
  public Single<OracleQueriesResult> asyncGetOracleQueries(String publicKey) {
    return asyncGetOracleQueries(publicKey, QueryParams.builder().build());
  }

  @Override
  public Single<OracleQueriesResult> asyncGetOracleQueries(
      String publicKey, QueryParams queryParams) {
    return OracleQueriesResult.builder()
        .build()
        .asyncGet(
            externalApi.rxGetOracleQueriesByPubkey(
                publicKey,
                queryParams.getFrom(),
                queryParams.getLimit(),
                queryParams.getQueryType().toString()));
  }

  @Override
  public OracleQueriesResult blockingGetOracleQueries(String publicKey) {
    return blockingGetOracleQueries(publicKey, QueryParams.builder().build());
  }

  @Override
  public OracleQueriesResult blockingGetOracleQueries(String publicKey, QueryParams queryParams) {
    return OracleQueriesResult.builder()
        .build()
        .blockingGet(
            externalApi.rxGetOracleQueriesByPubkey(
                publicKey,
                queryParams.getFrom(),
                queryParams.getLimit(),
                queryParams.getQueryType().toString()));
  }

  @Override
  public Single<OracleQueryResult> asyncGetOracleQuery(String pubkey, String queryId) {
    return OracleQueryResult.builder()
        .build()
        .asyncGet(externalApi.rxGetOracleQueryByPubkeyAndQueryId(pubkey, queryId));
  }

  @Override
  public OracleQueryResult blockingGetOracleQuery(String pubkey, String queryId) {
    return OracleQueryResult.builder()
        .build()
        .blockingGet(externalApi.rxGetOracleQueryByPubkeyAndQueryId(pubkey, queryId));
  }
}
