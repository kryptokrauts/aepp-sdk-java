package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import org.bouncycastle.crypto.CryptoException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class TransactionApiTest extends BaseTest {

  @Test
  @Ignore
  public void buildNativeTransactionTest(TestContext context)
      throws ExecutionException, InterruptedException {
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
            .createSpendTransaction(sender, recipient, amount, payload, null, ttl, nonce);
    UnsignedTx unsignedTxNative =
        transactionServiceNative.createUnsignedTransaction(spendTx).toFuture().get();

    Single<UnsignedTx> unsignedTx = transactionServiceDebug.createUnsignedTransaction(spendTx);
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
  @Ignore
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
          BigInteger ttl = BigInteger.valueOf(20000);
          BigInteger nonce = account.getNonce().add(BigInteger.ONE);
          AbstractTransaction<?> spendTx =
              transactionServiceNative
                  .getTransactionFactory()
                  .createSpendTransaction(
                      keyPair.getPublicKey(), recipient, amount, payload, null, ttl, nonce);
          UnsignedTx unsignedTxNative =
              transactionServiceNative.createUnsignedTransaction(spendTx).toFuture().get();
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

  @Test
  public void postContracTxTest(TestContext context)
      throws ExecutionException, InterruptedException, CryptoException {
    Async async = context.async();

    BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);

    String contractByteCode =
        "cb_+QPvRgGgeN05+tJcdqKtrzpqKaGf7e7wSc3ARZ/hNSgeuHcoXLn5Avv5ASqgaPJnYzj/UIg5q6R3Se/6i+h+8oTyB/s9mZhwHNU4h8WEbWFpbrjAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKD//////////////////////////////////////////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAuEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+QHLoLnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqhGluaXS4YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7kBQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEA//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///////////////////////////////////////////uMxiAABkYgAAhJGAgIBRf7nJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqFGIAAMBXUIBRf2jyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFFGIAAK9XUGABGVEAW2AAGVlgIAGQgVJgIJADYAOBUpBZYABRWVJgAFJgAPNbYACAUmAA81tZWWAgAZCBUmAgkANgABlZYCABkIFSYCCQA2ADgVKBUpBWW2AgAVFRWVCAkVBQgJBQkFZbUFCCkVBQYgAAjFaqo6ki";

    String contractCallData =
        "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACC5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAnHQYrA==";

    // get the currents accounts nonce in case a transaction is already
    // created and increase it by one
    Single<Account> acc = accountService.getAccount(keyPair.getPublicKey());
    acc.subscribe(
        account -> {
          String ownerId = keyPair.getPublicKey();
          BigInteger amount = BigInteger.valueOf(1);
          BigInteger ttl = BigInteger.valueOf(20000);

          BigInteger nonce = account.getNonce().add(BigInteger.ONE);
          AbstractTransaction<?> contractTx =
              transactionServiceNative
                  .getTransactionFactory()
                  .createContractCreateTransaction(
                      1,
                      0,
                      contractCallData,
                      contractByteCode,
                      0,
                      50,
                      50,
                      account.getNonce().intValue(),
                      ownerId,
                      0,
                      3);

          UnsignedTx unsignedTxNative =
              transactionServiceNative.createUnsignedTransaction(contractTx).toFuture().get();
          System.out.println(unsignedTxNative);
          Tx signedTx =
              transactionServiceNative.signTransaction(unsignedTxNative, keyPair.getPrivateKey());
          System.out.println(signedTx);
          Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTx);
          txResponse.subscribe(
              it -> {
                System.out.println(it.getTxHash());

                async.complete();
              },
              throwable -> {
                System.err.println("error ------------------------------------ " + throwable);
                context.fail();
              });
        },
        throwable -> {
          System.err.println("error ------------------------------------ " + throwable);
          context.fail();
        });
  }
}
