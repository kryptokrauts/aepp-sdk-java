package com.kryptokrauts.aeternity.sdk.service.oracle.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleQueries;
import com.kryptokrauts.aeternity.generated.model.OracleQuery;
import com.kryptokrauts.aeternity.generated.model.RegisteredOracle;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.oracle.OracleService;
import io.reactivex.Single;
import java.math.BigInteger;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OracleServiceImpl implements OracleService {

  @Nonnull private AeternityServiceConfiguration config;

  @Nonnull private ExternalApi externalApi;

  @Override
  public Single<RegisteredOracle> getRegisteredOracle(String publicKey) {
    return externalApi.rxGetOracleByPubkey(publicKey);
  }

  @Override
  public Single<OracleQueries> getOracleQueries(
      String pubkey, Optional<String> from, Optional<BigInteger> limit, Optional<String> type) {
    return externalApi.rxGetOracleQueriesByPubkey(
        pubkey, from.orElse(null), limit.orElse(null), type.orElse(null));
  }

  @Override
  public Single<OracleQuery> getOracleQuery(String pubkey, String queryId) {
    return externalApi.rxGetOracleQueryByPubkeyAndQueryId(pubkey, queryId);
  }
}
