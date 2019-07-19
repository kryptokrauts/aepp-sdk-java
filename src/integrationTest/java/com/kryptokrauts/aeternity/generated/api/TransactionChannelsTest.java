package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCreateTransaction;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionChannelsTest extends BaseTest {

  BaseKeyPair initiator;
  BaseKeyPair responder;

  @Before
  public void initBeforeTest() {
    initiator = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
    responder = keyPairService.generateBaseKeyPair();
  }

  @Test
  public void aFundResponderAccount(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                BigInteger amount = UnitConversionUtil.toAettos("10", Unit.AE).toBigInteger();
                BigInteger nonce = getAccount(initiator.getPublicKey()).getNonce();
                transactionServiceNative
                    .getTransactionFactory()
                    .createSpendTransaction(
                        initiator.getPublicKey(),
                        responder.getPublicKey(),
                        amount,
                        "",
                        BigInteger.ZERO,
                        nonce);
              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void channelCreateTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                BigInteger nonce = getAccount(initiator.getPublicKey()).getNonce();
                ChannelCreateTransaction channelCreateTransaction =
                    transactionServiceNative
                        .getTransactionFactory()
                        .createChannelCreateTransaction(
                            initiator.getPublicKey(),
                            BigInteger.ZERO,
                            responder.getPublicKey(),
                            BigInteger.ZERO,
                            BigInteger.ZERO,
                            BigInteger.ZERO,
                            BigInteger.ZERO,
                            "",
                            nonce);
              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }
}
