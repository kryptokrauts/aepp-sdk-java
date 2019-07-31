package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.OracleQuery;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.RegisteredOracle;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.generated.model.TTL.TypeEnum;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.OracleFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleExtendTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleQueryTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRegisterTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleResponseTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
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

  static BaseKeyPair queryAccount;
  static BaseKeyPair oracleAccount;

  static String oracleId;
  static String queryId;

  static BigInteger initialOracleTtl;

  @Test
  public void aGenerateKeyPairAndFundOracleAccount(TestContext context) {
    queryAccount =
        keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
    oracleAccount = keyPairService.generateBaseKeyPair();

    oracleId = oracleAccount.getPublicKey().replace("ak_", "ok_");

    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                BigInteger amount = UnitConversionUtil.toAettos("10", Unit.AE).toBigInteger();
                BigInteger nonce = getAccount(queryAccount.getPublicKey()).getNonce();
                SpendTransaction spendTx =
                    transactionServiceNative
                        .getTransactionFactory()
                        .createSpendTransaction(
                            queryAccount.getPublicKey(),
                            oracleAccount.getPublicKey(),
                            amount,
                            "",
                            BigInteger.ZERO,
                            nonce.add(BigInteger.ONE));
                UnsignedTx unsignedTx =
                    transactionServiceNative.createUnsignedTransaction(spendTx).blockingGet();
                Tx signedTx =
                    transactionServiceNative.signTransaction(
                        unsignedTx, queryAccount.getPrivateKey());
                PostTxResponse txResponse =
                    transactionServiceNative.postTransaction(signedTx).blockingGet();
                _logger.info(txResponse.getTxHash());
                waitForTxMined(txResponse.getTxHash());
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
                BigInteger nonce = getAccount(oracleAccount.getPublicKey()).getNonce();
                BigInteger currentHeight =
                    chainService.getCurrentKeyBlock().blockingGet().getHeight();
                initialOracleTtl = currentHeight.add(BigInteger.valueOf(5000));
                TTL oracleTtl = new TTL();
                oracleTtl.setType(TypeEnum.BLOCK);
                oracleTtl.setValue(initialOracleTtl);
                OracleRegisterTransaction oracleRegisterTransaction =
                    OracleRegisterTransaction.builder()
                        .accountId(oracleAccount.getPublicKey())
                        .abiVersion(BigInteger.ZERO)
                        .nonce(nonce.add(BigInteger.ONE))
                        .oracleTtl(oracleTtl)
                        .queryFee(BigInteger.valueOf(100)) // 100 ættos
                        .queryFormat("string")
                        .responseFormat("string")
                        .ttl(BigInteger.ZERO)
                        .feeCalculationModel(new OracleFeeCalculationModel())
                        .build();

                UnsignedTx unsignedTx =
                    transactionServiceNative
                        .createUnsignedTransaction(oracleRegisterTransaction)
                        .blockingGet();
                Tx signedTx =
                    transactionServiceNative.signTransaction(
                        unsignedTx, oracleAccount.getPrivateKey());
                _logger.info("SignedTx: " + signedTx.getTx());
                PostTxResponse postTxResponse =
                    transactionServiceNative.postTransaction(signedTx).blockingGet();
                _logger.info("OracleRegisterTx-Hash: " + postTxResponse.getTxHash());
                waitForTxMined(postTxResponse.getTxHash());
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
                BigInteger nonce = getAccount(queryAccount.getPublicKey()).getNonce();
                TTL queryTtl = new TTL();
                queryTtl.setType(TypeEnum.DELTA);
                queryTtl.setValue(BigInteger.valueOf(50));
                RelativeTTL responseTtl = new RelativeTTL();
                responseTtl.setType(RelativeTTL.TypeEnum.DELTA);
                responseTtl.setValue(BigInteger.valueOf(100));
                OracleQueryTransaction oracleQueryTransaction =
                    OracleQueryTransaction.builder()
                        .senderId(queryAccount.getPublicKey())
                        .oracleId(oracleId)
                        .nonce(nonce.add(BigInteger.ONE))
                        .query("Am I stupid?")
                        .queryFee(BigInteger.valueOf(100)) // 100 ættos
                        .queryTtl(queryTtl)
                        .responseTtl(responseTtl)
                        .ttl(BigInteger.ZERO)
                        .feeCalculationModel(new OracleFeeCalculationModel())
                        .build();

                UnsignedTx unsignedTx =
                    transactionServiceNative
                        .createUnsignedTransaction(oracleQueryTransaction)
                        .blockingGet();
                Tx signedTx =
                    transactionServiceNative.signTransaction(
                        unsignedTx, queryAccount.getPrivateKey());
                _logger.info("SignedTx: " + signedTx.getTx());
                PostTxResponse postTxResponse =
                    transactionServiceNative.postTransaction(signedTx).blockingGet();
                _logger.info("OracleQueryTx-Hash: " + postTxResponse.getTxHash());
                waitForTxMined(postTxResponse.getTxHash());
                queryId =
                    EncodingUtils.queryId(
                        queryAccount.getPublicKey(), nonce.add(BigInteger.ONE), oracleId);
                OracleQuery oracleQuery =
                    oracleService.getOracleQuery(oracleId, queryId).blockingGet();
                _logger.debug(oracleQuery.toString());
              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void dOracleResponseTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                BigInteger nonce = getAccount(oracleAccount.getPublicKey()).getNonce();
                RelativeTTL responseTtl = new RelativeTTL();
                responseTtl.setType(RelativeTTL.TypeEnum.DELTA);
                responseTtl.setValue(BigInteger.valueOf(100));
                OracleResponseTransaction oracleResponseTransaction =
                    OracleResponseTransaction.builder()
                        .senderId(oracleAccount.getPublicKey())
                        .oracleId(oracleAccount.getPublicKey().replace("ak_", "ok_"))
                        .queryId(queryId)
                        .nonce(nonce.add(BigInteger.ONE))
                        .response("yes you are!")
                        .responseTtl(responseTtl)
                        .responseFormat("response Specification")
                        .ttl(BigInteger.ZERO)
                        .feeCalculationModel(new OracleFeeCalculationModel())
                        .build();

                UnsignedTx unsignedTx =
                    transactionServiceNative
                        .createUnsignedTransaction(oracleResponseTransaction)
                        .blockingGet();
                Tx signedTx =
                    transactionServiceNative.signTransaction(
                        unsignedTx, oracleAccount.getPrivateKey());
                _logger.info("SignedTx: " + signedTx.getTx());
                PostTxResponse postTxResponse =
                    transactionServiceNative.postTransaction(signedTx).blockingGet();
                _logger.info("OracleResponseTx-Hash: " + postTxResponse.getTxHash());
                waitForTxMined(postTxResponse.getTxHash());
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
                BigInteger nonce = getAccount(oracleAccount.getPublicKey()).getNonce();
                RelativeTTL relativeTTL = new RelativeTTL();
                relativeTTL.setType(RelativeTTL.TypeEnum.DELTA);
                relativeTTL.setValue(additionalTtl);
                OracleExtendTransaction oracleExtendTransaction =
                    OracleExtendTransaction.builder()
                        .nonce(nonce.add(BigInteger.ONE))
                        .oracleId(oracleId)
                        .oracleRelativeTtl(relativeTTL)
                        .ttl(BigInteger.ZERO)
                        .feeCalculationModel(new OracleFeeCalculationModel())
                        .build();
                UnsignedTx unsignedTx =
                    transactionServiceNative
                        .createUnsignedTransaction(oracleExtendTransaction)
                        .blockingGet();
                Tx signedTx =
                    transactionServiceNative.signTransaction(
                        unsignedTx, oracleAccount.getPrivateKey());
                _logger.info("SignedTx: " + signedTx.getTx());
                PostTxResponse postTxResponse =
                    transactionServiceNative.postTransaction(signedTx).blockingGet();
                _logger.info("OracleExtendTx-Hash: " + postTxResponse.getTxHash());
                waitForTxMined(postTxResponse.getTxHash());
                RegisteredOracle registeredOracle =
                    oracleService.getRegisteredOracle(oracleId).blockingGet();
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
