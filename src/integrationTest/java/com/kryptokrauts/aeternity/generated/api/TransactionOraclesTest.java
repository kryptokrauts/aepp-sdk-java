package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleQueriesResult;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleQueryResult;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleTTLType;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.QueryType;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.RegisteredOracleResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleExtendTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleQueryTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRegisterTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRespondTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.Optional;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionOraclesTest extends BaseTest {

  static BaseKeyPair oracleAccount;

  static String oracleId;
  static String queryId;

  static String queryString = "{\"lat\":48.78,\"lon\":9.18}";
  /** example from https://api.openweathermap.org/ */
  static String responseString =
      "{\"coord\":{\"lon\":9.18,\"lat\":48.78},\"weather\":[{\"id\":310,\"main\":\"Drizzle\",\"description\":\"light intensity drizzle rain\",\"icon\":\"09n\"}],\"base\":\"stations\",\"main\":{\"temp\":282.56,\"pressure\":1021,\"humidity\":93,\"temp_min\":279.82,\"temp_max\":285.37},\"visibility\":7000,\"wind\":{\"speed\":4.1,\"deg\":330},\"clouds\":{\"all\":90},\"dt\":1572217099,\"sys\":{\"type\":1,\"id\":1274,\"country\":\"DE\",\"sunrise\":1572156074,\"sunset\":1572192774},\"timezone\":3600,\"id\":2825297,\"name\":\"Stuttgart\",\"cod\":200}";

  static BigInteger initialOracleTtl;

  @Test
  public void aGenerateKeyPairAndFundOracleAccount(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            oracleAccount = keyPairService.generateBaseKeyPair();

            oracleId = oracleAccount.getOraclePK();
            BigInteger amount = UnitConversionUtil.toAettos("10", Unit.AE).toBigInteger();
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .amount(amount)
                    .sender(baseKeyPair.getPublicKey())
                    .recipient(oracleAccount.getPublicKey())
                    .ttl(ZERO)
                    .nonce(getNextBaseKeypairNonce())
                    .build();
            PostTransactionResult postResult = this.blockingPostTx(spendTx, Optional.empty());
            _logger.info(postResult.getTxHash());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void bOracleRegisterTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            BigInteger nonce = getAccount(oracleAccount.getPublicKey()).getNonce().add(ONE);
            BigInteger currentHeight =
                this.aeternityServiceNative.info.blockingGetCurrentKeyBlock().getHeight();
            initialOracleTtl = currentHeight.add(BigInteger.valueOf(5000));

            OracleRegisterTransactionModel oracleRegisterTx =
                OracleRegisterTransactionModel.builder()
                    .accountId(oracleAccount.getPublicKey())
                    .abiVersion(ZERO)
                    .nonce(nonce)
                    .oracleTtl(initialOracleTtl)
                    .oracleTtlType(OracleTTLType.BLOCK)
                    .queryFee(BigInteger.valueOf(100))
                    .queryFormat("string")
                    .responseFormat("string")
                    .ttl(ZERO)
                    .build();

            PostTransactionResult postResult =
                this.blockingPostTx(oracleRegisterTx, Optional.of(oracleAccount.getPrivateKey()));
            _logger.info(postResult.getTxHash());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void cOracleQueryTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            BigInteger nonce = getNextBaseKeypairNonce();
            OracleQueryTransactionModel oracleQueryTx =
                OracleQueryTransactionModel.builder()
                    .senderId(baseKeyPair.getPublicKey())
                    .oracleId(oracleId)
                    .nonce(nonce)
                    .query(queryString)
                    .queryFee(BigInteger.valueOf(100))
                    .queryTtl(BigInteger.valueOf(50))
                    .ttl(ZERO)
                    .queryTtlType(OracleTTLType.DELTA)
                    .responseTtl(BigInteger.valueOf(100))
                    .build();

            PostTransactionResult postResult =
                this.blockingPostTx(oracleQueryTx, Optional.of(baseKeyPair.getPrivateKey()));
            _logger.info(postResult.getTxHash());
            queryId = EncodingUtils.queryId(baseKeyPair.getPublicKey(), nonce, oracleId);
            OracleQueryResult oracleQuery =
                this.aeternityServiceNative.oracles.blockingGetOracleQuery(oracleId, queryId);
            _logger.debug(oracleQuery.toString());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void dOracleRespondTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            QueryType type = QueryType.ALL;
            _logger.info("get OracleQueries with type: {}", type);
            OracleQueriesResult oracleQueriesResult =
                this.aeternityServiceNative.oracles.blockingGetOracleQueries(
                    oracleAccount.getOraclePK(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(type));
            _logger.info("OracleQueriesResult: {}", oracleQueriesResult);
            _logger.info("OracleQuery count: {}", oracleQueriesResult.getQueryResults().size());
            context.assertFalse(oracleQueriesResult.getQueryResults().isEmpty());
            OracleQueryResult oracleQueryResult =
                oracleQueriesResult.getQueryResults().stream()
                    .filter(query -> query.getId().equals(queryId))
                    .findFirst()
                    .get();
            context.assertEquals(queryString, oracleQueryResult.getQuery());
            BigInteger nonce = getAccount(oracleAccount.getPublicKey()).getNonce().add(ONE);
            OracleRespondTransactionModel oracleRespondTx =
                OracleRespondTransactionModel.builder()
                    .oracleId(oracleAccount.getOraclePK())
                    .queryId(oracleQueryResult.getId())
                    .nonce(nonce)
                    .response(responseString)
                    .responseTtl(BigInteger.valueOf(100))
                    .ttl(ZERO)
                    .build();

            PostTransactionResult postResult =
                this.blockingPostTx(oracleRespondTx, Optional.of(oracleAccount.getPrivateKey()));
            _logger.info(postResult.getTxHash());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void eOracleExtendTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            BigInteger additionalTtl = BigInteger.valueOf(100);
            BigInteger nonce = getAccount(oracleAccount.getPublicKey()).getNonce().add(ONE);

            OracleExtendTransactionModel oracleExtendTx =
                OracleExtendTransactionModel.builder()
                    .nonce(nonce)
                    .oracleId(oracleId)
                    .oracleRelativeTtl(additionalTtl)
                    .ttl(ZERO)
                    .build();

            PostTransactionResult postResult =
                this.blockingPostTx(oracleExtendTx, Optional.of(oracleAccount.getPrivateKey()));
            _logger.info(postResult.getTxHash());

            RegisteredOracleResult registeredOracle =
                this.aeternityServiceNative.oracles.blockingGetRegisteredOracle(oracleId);
            context.assertEquals(initialOracleTtl.add(additionalTtl), registeredOracle.getTtl());
            _logger.info(registeredOracle.toString());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }
}
