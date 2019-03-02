package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import org.bouncycastle.crypto.CryptoException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TransactionApiTest extends BaseTest {

  @Test
  public void buildNativeTransactionTest(TestContext context)
      throws ExecutionException, InterruptedException {
    Async async = context.async();

    String sender = keyPairService.generateBaseKeyPair().getPublicKey();
    String recipient = keyPairService.generateBaseKeyPair().getPublicKey();
    BigInteger amount = BigInteger.valueOf(1000);
    String payload = "payload";
    // TODO calculate that correctly
    long additionalGasCostAccordingToByteSize = 50000000;
    BigInteger fee =
        BigInteger.valueOf(
            (BaseConstants.BASE_GAS + additionalGasCostAccordingToByteSize)
                * BaseConstants.ON_CHAIN_FEE_MULTIPLIER);
    BigInteger ttl = BigInteger.valueOf(100);
    BigInteger nonce = BigInteger.valueOf(5);

    UnsignedTx unsignedTxNative =
        transactionServiceNative
            .createSpendTx(sender, recipient, amount, payload, fee, ttl, nonce)
            .toFuture()
            .get();

    Single<UnsignedTx> unsignedTx =
        transactionServiceDebug.createSpendTx(sender, recipient, amount, payload, fee, ttl, nonce);
    unsignedTx.subscribe(
        it -> {
          Assertions.assertEquals(it, unsignedTxNative);
          async.complete();
        },
        throwable -> {
          context.fail();
        });
  }

  @Test
  public void postSpendTxTest(TestContext context)
      throws ExecutionException, InterruptedException, CryptoException {
    Async async = context.async();

    BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);

    // get the currents accounts nonce in case a transaction is already
    // created and increase it by one
    Single<Account> acc = accountService.getAccount(keyPair.getPublicKey());
    acc.subscribe(
        account -> {
          BaseKeyPair kp = keyPairService.generateBaseKeyPair();
          String recipient = kp.getPublicKey();
          BigInteger amount = BigInteger.valueOf(1);
          String payload = "payload";
          // TODO calculate that correctly
          long additionalGasCostAccordingToByteSize = 50000000;
          BigInteger fee =
              BigInteger.valueOf(
                  (BaseConstants.BASE_GAS + additionalGasCostAccordingToByteSize)
                      * BaseConstants.ON_CHAIN_FEE_MULTIPLIER);
          BigInteger ttl = BigInteger.valueOf(20000);
          BigInteger nonce = account.getNonce().add(BigInteger.ONE);
          UnsignedTx unsignedTxNative =
              transactionServiceNative
                  .createSpendTx(
                      keyPair.getPublicKey(), recipient, amount, payload, fee, ttl, nonce)
                  .toFuture()
                  .get();
          Tx signedTx =
              transactionServiceNative.signTransaction(unsignedTxNative, keyPair.getPrivateKey());
          Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTx);
          txResponse.subscribe(
              it -> {
                Assertions.assertEquals(
                    it.getTxHash(), transactionServiceNative.computeTxHash(signedTx.getTx()));
                async.complete();
              },
              throwable -> {
                context.fail();
              });
        },
        throwable -> {
          context.fail();
        });
  }
}
