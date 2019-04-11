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
  public void postASpendTxTest(TestContext context)
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
          BigInteger amount = new BigInteger("1000000000000000000");
          String payload = "payload";
          BigInteger ttl = BigInteger.valueOf(20000);
          BigInteger nonce = account.getNonce().add(BigInteger.ONE);
          System.out.println("Next nonce" + nonce);
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

    BaseKeyPair kp = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);

    String contractByteCode =
        "cb_+QPvRgGgeN05+tJcdqKtrzpqKaGf7e7wSc3ARZ/hNSgeuHcoXLn5Avv5ASqgaPJnYzj/UIg5q6R3Se/6i+h+8oTyB/s9mZhwHNU4h8WEbWFpbrjAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKD//////////////////////////////////////////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAuEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+QHLoLnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqhGluaXS4YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7kBQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEA//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///////////////////////////////////////////uMxiAABkYgAAhJGAgIBRf7nJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqFGIAAMBXUIBRf2jyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFFGIAAK9XUGABGVEAW2AAGVlgIAGQgVJgIJADYAOBUpBZYABRWVJgAFJgAPNbYACAUmAA81tZWWAgAZCBUmAgkANgABlZYCABkIFSYCCQA2ADgVKBUpBWW2AgAVFRWVCAkVBQgJBQkFZbUFCCkVBQYgAAjFaqo6ki";

    String contractCallData =
        "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACC5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAnHQYrA==";

    // get the currents accounts nonce in case a transaction is already
    // created and increase it by one
    Single<Account> acc = accountService.getAccount(kp.getPublicKey());
    acc.subscribe(
        account -> {
          String ownerId = kp.getPublicKey();
          BigInteger amount = BigInteger.valueOf(1);
          BigInteger ttl = BigInteger.valueOf(20000);

          BigInteger nonce = account.getNonce().add(BigInteger.ONE);
          System.out.println("Next nonce" + nonce);
          AbstractTransaction<?> contractTx =
              transactionServiceNative
                  .getTransactionFactory()
                  .createContractCreateTransaction(
                      1,
                      0,
                      contractCallData,
                      contractByteCode,
                      0,
                      500,
                      50,
                      nonce.intValue(),
                      ownerId,
                      0,
                      3);

          System.out.println(contractTx);
          System.out.println(transactionServiceNative);

          UnsignedTx unsignedTxNative =
              transactionServiceNative.createUnsignedTransaction(contractTx).toFuture().get();
          System.out.println(unsignedTxNative);
          Tx signedTx =
              transactionServiceNative.signTransaction(unsignedTxNative, kp.getPrivateKey());

          //          signedTx.setTx(
          //
          // "tx_+QTiCwH4QrhAishbFS9zXWJ/oESFpT83K0q8nYJFWaQrf7vaPxGPLq9VSLktCVircLa55sx/iEwg0jWidVSea/9kGO5odG3DB7kEmfkElioBoQELtO15J/l7UeG8teE0DRIzWyorEsi8UiHWPEvLOdQeYYIi3rkD8vkD70YBoHjdOfrSXHaira86aimhn+3u8EnNwEWf4TUoHrh3KFy5+QL7+QEqoGjyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFhG1haW64wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACg//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPkBy6C5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6oRpbml0uGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//////////////////////////////////////////+5AUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAP//////////////////////////////////////////AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7jMYgAAZGIAAISRgICAUX+5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6hRiAADAV1CAUX9o8mdjOP9QiDmrpHdJ7/qL6H7yhPIH+z2ZmHAc1TiHxRRiAACvV1BgARlRAFtgABlZYCABkIFSYCCQA2ADgVKQWWAAUVlSYABSYADzW2AAgFJgAPNbWVlgIAGQgVJgIJADYAAZWWAgAZCBUmAgkANgA4FSgVKQVltgIAFRUVlQgJFQUICQUJBWW1BQgpFQUGIAAIxWgwMAAYcD5x3GeLgAAAAAgw9CQIQ7msoAuGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAILnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADRRFqR");

          System.out.println(signedTx);
          Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTx);
          txResponse.subscribe(
              it -> {
                System.out.println(it.getTxHash());

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
