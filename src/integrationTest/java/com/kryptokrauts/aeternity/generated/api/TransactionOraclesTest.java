package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.OracleQuery;
import com.kryptokrauts.aeternity.generated.model.RegisteredOracle;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.domain.transaction.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleTTLType;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleExtendTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleQueryTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRegisterTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRespondTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionOraclesTest extends BaseTest {

  static BaseKeyPair oracleAccount;

  static String oracleId;
  static String queryId;

  static BigInteger initialOracleTtl;

  @Test
  public void aGenerateKeyPairAndFundOracleAccount(TestContext context) {
    oracleAccount = keyPairService.generateBaseKeyPair();

    oracleId = oracleAccount.getPublicKey().replace("ak_", "ok_");

    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                BigInteger amount = UnitConversionUtil.toAettos("10", Unit.AE).toBigInteger();
                SpendTransactionModel spendTx =
                    SpendTransactionModel.builder()
                        .amount(amount)
                        .sender(baseKeyPair.getPublicKey())
                        .recipient(oracleAccount.getPublicKey())
                        .ttl(ZERO)
                        .nonce(getNextBaseKeypairNonce())
                        .build();
                PostTransactionResult postResult = this.postTx(spendTx);
                _logger.info(postResult.getTxHash());
                waitForTxMined(postResult.getTxHash());
              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void bOracleRegisterTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
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
                    this.aeternityServiceNative.transactions.blockingPostTransaction(
                        oracleRegisterTx, oracleAccount.getPrivateKey());
                _logger.info(postResult.getTxHash());
                waitForTxMined(postResult.getTxHash());
              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void cOracleQueryTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                BigInteger nonce = getNextBaseKeypairNonce();
                OracleQueryTransactionModel oracleQueryTx =
                    OracleQueryTransactionModel.builder()
                        .senderId(baseKeyPair.getPublicKey())
                        .oracleId(oracleId)
                        .nonce(nonce)
                        .query("Am I stupid?")
                        .queryFee(BigInteger.valueOf(100))
                        .queryTtl(BigInteger.valueOf(50))
                        .ttl(ZERO)
                        .queryTtlType(OracleTTLType.DELTA)
                        .responseTtl(BigInteger.valueOf(100))
                        .build();

                PostTransactionResult postResult =
                    this.aeternityServiceNative.transactions.blockingPostTransaction(
                        oracleQueryTx, baseKeyPair.getPrivateKey());
                _logger.info(postResult.getTxHash());
                waitForTxMined(postResult.getTxHash());
                queryId = EncodingUtils.queryId(baseKeyPair.getPublicKey(), nonce, oracleId);
                OracleQuery oracleQuery =
                    this.aeternityServiceNative
                        .oracles
                        .getOracleQuery(oracleId, queryId)
                        .blockingGet();
                _logger.debug(oracleQuery.toString());
              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void dOracleRespondTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                BigInteger nonce = getAccount(oracleAccount.getPublicKey()).getNonce().add(ONE);
                OracleRespondTransactionModel oracleRespondTx =
                    OracleRespondTransactionModel.builder()
                        .senderId(oracleAccount.getPublicKey())
                        .oracleId(oracleAccount.getPublicKey().replace("ak_", "ok_"))
                        .queryId(queryId)
                        .nonce(nonce)
                        .response("yes you are nuts!")
                        .responseTtl(BigInteger.valueOf(100))
                        .responseFormat("response Specification")
                        .ttl(ZERO)
                        .build();

                PostTransactionResult postResult =
                    this.aeternityServiceNative.transactions.blockingPostTransaction(
                        oracleRespondTx, oracleAccount.getPrivateKey());
                _logger.info(postResult.getTxHash());
                waitForTxMined(postResult.getTxHash());

              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void eOracleExtendTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
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
                    this.aeternityServiceNative.transactions.blockingPostTransaction(
                        oracleExtendTx, oracleAccount.getPrivateKey());
                _logger.info(postResult.getTxHash());
                waitForTxMined(postResult.getTxHash());

                RegisteredOracle registeredOracle =
                    this.aeternityServiceNative.oracles.getRegisteredOracle(oracleId).blockingGet();
                context.assertEquals(
                    initialOracleTtl.add(additionalTtl), registeredOracle.getTtl());
                _logger.info(registeredOracle.toString());
              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }
}
