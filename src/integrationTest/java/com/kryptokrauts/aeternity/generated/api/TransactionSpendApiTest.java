package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionSpendApiTest extends BaseTest {

  BaseKeyPair baseKeyPair;

  @Before
  public void initBeforeTest() {
    baseKeyPair =
        keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
  }

  /**
   * create an unsigned native spend transaction
   *
   * @param context
   */
  @Test
  public void buildNativeSpendTransactionTest(TestContext context) {
    Async async = context.async();

    String sender = keyPairService.generateBaseKeyPair().getPublicKey();
    String recipient = keyPairService.generateBaseKeyPair().getPublicKey();
    BigInteger amount = BigInteger.valueOf(1000);
    String payload = "payload";
    BigInteger ttl = BigInteger.valueOf(100);
    BigInteger nonce = BigInteger.valueOf(5);

    AbstractTransaction<?> spendTx =
        transactionServiceNative
            .getTransactionFactory()
            .createSpendTransaction(sender, recipient, amount, payload, ttl, nonce);
    UnsignedTx unsignedTxNative =
        transactionServiceNative.createUnsignedTransaction(spendTx).blockingGet();

    Single<UnsignedTx> unsignedTx = transactionServiceDebug.createUnsignedTransaction(spendTx);
    unsignedTx.subscribe(
        it -> {
          context.assertEquals(it, unsignedTxNative);
          async.complete();
        },
        throwable -> {
          context.fail(throwable);
        });
  }

  @Test
  public void postSpendTxTest(TestContext context) {
    Async async = context.async();

    BaseKeyPair keyPair =
        keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);

    // get the currents accounts nonce in case a transaction is already
    // created and increase it by one
    Single<AccountResult> acc = accountService.asyncGetAccount(keyPair.getPublicKey());
    acc.subscribe(
        account -> {
          BaseKeyPair kp = keyPairService.generateBaseKeyPair();
          String recipient = kp.getPublicKey();
          BigInteger amount = new BigInteger("1000000000000000000");
          String payload = "payload";
          BigInteger ttl = BigInteger.ZERO;
          BigInteger nonce = account.getNonce().add(BigInteger.ONE);

          AbstractTransaction<?> spendTx =
              transactionServiceNative
                  .getTransactionFactory()
                  .createSpendTransaction(
                      keyPair.getPublicKey(), recipient, amount, payload, ttl, nonce);
          UnsignedTx unsignedTxNative =
              transactionServiceNative.createUnsignedTransaction(spendTx).blockingGet();
          Tx signedTx =
              transactionServiceNative.signTransaction(unsignedTxNative, keyPair.getPrivateKey());

          context.assertEquals(
              spendTx, transactionServiceNative.decodeTransaction(signedTx.getTx()));

          Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTx);
          txResponse.subscribe(
              it -> {
                _logger.info("SpendTx hash: " + it.getTxHash());
                context.assertEquals(
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
