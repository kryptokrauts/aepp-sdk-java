package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.generated.model.TTL.TypeEnum;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.OracleFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRegisterTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
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

  @Test
  public void aaGenerateKeyPairAndFundOracleAccount(TestContext context) {
    queryAccount =
        keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
    oracleAccount = keyPairService.generateBaseKeyPair();

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
  public void registerOracleTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                BigInteger nonce = getAccount(oracleAccount.getPublicKey()).getNonce();
                TTL oracleTtl = new TTL();
                oracleTtl.setType(TypeEnum.DELTA);
                oracleTtl.setValue(BigInteger.valueOf(800));
                OracleRegisterTransaction oracleRegisterTransaction =
                    OracleRegisterTransaction.builder()
                        .accountId(oracleAccount.getPublicKey())
                        /*
                         * TODO when using ABI version 1 we get an error 'cannot be applied due to
                         * an error bad_query_format'
                         */
                        .abiVersion(BigInteger.ZERO)
                        .nonce(nonce.add(BigInteger.ONE))
                        .oracleTtl(oracleTtl)
                        .queryFee(BigInteger.valueOf(100)) // 100 ættos
                        .queryFormat("query Specification") // TODO spec for ABI version 1
                        .responseFormat("response Specification") // TODO spec for ABI version 1
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
}
