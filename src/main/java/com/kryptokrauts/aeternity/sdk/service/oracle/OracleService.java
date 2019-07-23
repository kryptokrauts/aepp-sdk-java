package com.kryptokrauts.aeternity.sdk.service.oracle;

import com.kryptokrauts.aeternity.generated.model.OracleQueries;
import com.kryptokrauts.aeternity.generated.model.OracleQuery;
import io.reactivex.Single;
import java.math.BigInteger;
import java.util.Optional;

public interface OracleService {

  /**
   * Get oracle queries by public key
   *
   * @param pubkey The public key of the oracle (required)
   * @param from Last query id in previous page (optional)
   * @param limit Max number of oracle queries (optional)
   * @param type The type of a query: open, closed or all (optional)
   * @return Asynchronous result handler (RxJava Single) for {@link OracleQueries}
   */
  Single<OracleQueries> getOracleQueriesByPublicKey(
      String pubkey, Optional<String> from, Optional<BigInteger> limit, Optional<String> type);

  /**
   * Get an oracle query by public key and query ID
   *
   * @param pubkey The public key of the oracle (required)
   * @param queryId The ID of the query (required)
   * @return Asynchronous result handler (RxJava Single) for {@link OracleQuery}
   */
  Single<OracleQuery> getOracleQueryByPubkeyAndQueryId(String pubkey, String queryId);
}
