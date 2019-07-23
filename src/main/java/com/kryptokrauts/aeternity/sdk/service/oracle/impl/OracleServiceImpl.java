package com.kryptokrauts.aeternity.sdk.service.oracle.impl;

import com.kryptokrauts.aeternity.generated.api.OracleApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.OracleApi;
import com.kryptokrauts.aeternity.generated.model.OracleQueries;
import com.kryptokrauts.aeternity.generated.model.OracleQuery;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.oracle.OracleService;
import io.reactivex.Single;
import java.math.BigInteger;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OracleServiceImpl implements OracleService {
  @Nonnull private ServiceConfiguration config;

  private OracleApi oracleApi;

  private OracleApi getOracleApi() {
    if (oracleApi == null) {
      oracleApi = new OracleApi(new OracleApiImpl(config.getApiClient()));
    }
    return oracleApi;
  }

  @Override
  public Single<OracleQueries> getOracleQueriesByPublicKey(
      String pubkey, Optional<String> from, Optional<BigInteger> limit, Optional<String> type) {
    return getOracleApi()
        .rxGetOracleQueriesByPubkey(
            pubkey, from.orElse(null), limit.orElse(null), type.orElse(null));
  }

  @Override
  public Single<OracleQuery> getOracleQueryByPubkeyAndQueryId(String pubkey, String queryId) {
    return getOracleApi().rxGetOracleQueryByPubkeyAndQueryId(pubkey, queryId);
  }
}
