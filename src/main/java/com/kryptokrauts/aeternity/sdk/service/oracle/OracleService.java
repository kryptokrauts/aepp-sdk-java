package com.kryptokrauts.aeternity.sdk.service.oracle;

import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleQueriesResult;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleQueryResult;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.QueryParams;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.RegisteredOracleResult;
import io.reactivex.Single;

public interface OracleService {

  /**
   * asynchronously get an oracle by its public key
   *
   * @param publicKey The public key of the oracle (required)
   * @return asynchronous result handler (RxJava Single) for {@link RegisteredOracleResult}
   */
  Single<RegisteredOracleResult> asyncGetRegisteredOracle(String publicKey);

  /**
   * synchronously get an oracle by its public key
   *
   * @param publicKey The public key of the oracle (required)
   * @return result of {@link RegisteredOracleResult}
   */
  RegisteredOracleResult blockingGetRegisteredOracle(String publicKey);

  /**
   * asynchronously get oracle queries by public key; providing default values of {@link
   * QueryParams}
   *
   * @param publicKey The public key of the oracle
   * @return asynchronous result handler (RxJava Single) for {@link OracleQueriesResult}
   */
  Single<OracleQueriesResult> asyncGetOracleQueries(String publicKey);

  /**
   * asynchronously get oracle queries by public key
   *
   * @param publicKey The public key of the oracle
   * @param queryParams optional query params (from, limit, type)
   * @return asynchronous result handler (RxJava Single) for {@link OracleQueriesResult}
   */
  Single<OracleQueriesResult> asyncGetOracleQueries(String publicKey, QueryParams queryParams);

  /**
   * synchronously get oracle queries by public key; providing default values of {@link QueryParams}
   *
   * @param publicKey The public key of the oracle
   * @return result of {@link OracleQueriesResult}
   */
  OracleQueriesResult blockingGetOracleQueries(String publicKey);

  /**
   * synchronously get oracle queries by public key
   *
   * @param publicKey The public key of the oracle
   * @param queryParams optional query params (from, limit, type)
   * @return result of {@link OracleQueriesResult}
   */
  OracleQueriesResult blockingGetOracleQueries(String publicKey, QueryParams queryParams);

  /**
   * asynchronously get an oracle query by public key and query ID
   *
   * @param publicKey The public key of the oracle (required)
   * @param queryId The ID of the query (required)
   * @return asynchronous result handler (RxJava Single) for {@link OracleQueryResult}
   */
  Single<OracleQueryResult> asyncGetOracleQuery(String publicKey, String queryId);

  /**
   * synchronously get an oracle query by public key and query ID
   *
   * @param publicKey The public key of the oracle (required)
   * @param queryId The ID of the query (required)
   * @return result of {@link OracleQueryResult}
   */
  OracleQueryResult blockingGetOracleQuery(String publicKey, String queryId);
}
