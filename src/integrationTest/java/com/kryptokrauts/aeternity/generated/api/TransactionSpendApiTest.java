package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.domain.account.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.domain.transaction.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.Optional;
import org.bouncycastle.crypto.CryptoException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionSpendApiTest extends BaseTest {

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

    // sender, recipient, amount, payload, ttl, nonce

    SpendTransactionModel spendTx =
        SpendTransactionModel.builder()
            .sender(sender)
            .recipient(recipient)
            .amount(amount)
            .payload(payload)
            .ttl(ttl)
            .nonce(nonce)
            .build();

    String unsignedTxNative =
        aeternityServiceNative.transactions.asyncCreateUnsignedTransaction(spendTx).blockingGet();

    Single<String> unsignedTx =
        aeternityServiceNative.transactions.asyncCreateUnsignedTransaction(spendTx);
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
  public void postSpendSelfSignTxTest(TestContext context) {
    Async async = context.async();

    // get the currents accounts nonce in case a transaction is already
    // created and increase it by one
    Single<AccountResult> acc =
        this.aeternityServiceNative.accounts.asyncGetAccount(Optional.empty());
    acc.subscribe(
        account -> {
          BaseKeyPair kp = keyPairService.generateBaseKeyPair();
          String recipient = kp.getPublicKey();
          BigInteger amount = new BigInteger("1000000000000000000");
          String payload = "payload";
          BigInteger nonce = account.getNonce().add(ONE);

          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(recipient)
                  .amount(amount)
                  .payload(payload)
                  .ttl(ZERO)
                  .nonce(nonce)
                  .build();

          Single<PostTransactionResult> txResponse =
              aeternityServiceNative.transactions.asyncPostTransaction(spendTx);
          txResponse.subscribe(
              it -> {
                _logger.info("SpendTx hash: " + it.getTxHash());
                context.assertEquals(
                    it.getTxHash(), aeternityServiceNative.transactions.computeTxHash(spendTx));
                async.complete();
              },
              throwable -> {
                context.fail();
              });
        },
        throwable -> {
          context.fail();
        });
    async.awaitSuccess(TEST_CASE_TIMEOUT_MILLIS);
  }

  @Test
  public void postSpendSelfSignTxTestWithModel(TestContext context)
      throws CryptoException, TransactionCreateException {
    Async async = context.async();

    AccountResult acc = this.aeternityServiceNative.accounts.blockingGetAccount(Optional.empty());

    BaseKeyPair recipient = keyPairService.generateBaseKeyPair();

    SpendTransactionModel spendTx =
        SpendTransactionModel.builder()
            .sender(acc.getPublicKey())
            .recipient(recipient.getPublicKey())
            .amount(new BigInteger("1000000000000000000"))
            .payload("donation")
            .ttl(ZERO)
            .nonce(acc.getNonce().add(ONE))
            .build();

    Single<PostTransactionResult> txResponse =
        aeternityServiceNative.transactions.asyncPostTransaction(spendTx);

    txResponse.subscribe(
        it -> {
          _logger.info("SpendTx hash: " + it.getTxHash());
          context.assertEquals(
              it.getTxHash(), aeternityServiceNative.transactions.computeTxHash(spendTx));
          async.complete();
        },
        throwable -> {
          context.fail();
        });
  }
}
