package com.kryptokrauts.aeternity.generated.epoch.api;

import com.kryptokrauts.aeternity.generated.epoch.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.epoch.model.SpendTx;
import com.kryptokrauts.aeternity.generated.epoch.model.Tx;
import com.kryptokrauts.aeternity.generated.epoch.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.config.AEConfig;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import io.reactivex.Observable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.bouncycastle.crypto.CryptoException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class TransactionApiTest extends BaseTest {

    @Test
    public void buildNativeTransactionTest(TestContext context) throws ExecutionException, InterruptedException {
        Async async = context.async();

        String sender = keyPairService.generateBaseKeyPair().getPublicKey();
        String recipient = keyPairService.generateBaseKeyPair().getPublicKey();
        SpendTx spendTx = new SpendTx();
        spendTx.setSenderId(sender);
        spendTx.setRecipientId(recipient);
        spendTx.setAmount(BigInteger.valueOf(1000));
        spendTx.setPayload("payload");
        spendTx.setFee(BigInteger.valueOf(1));
        spendTx.setTtl(BigInteger.valueOf(100));
        spendTx.setNonce(BigInteger.valueOf(5));

        UnsignedTx unsignedTxNative = transactionServiceNative.createTx(spendTx).toFuture().get();

        Observable<UnsignedTx> unsignedTxObservable = transactionServiceDebug.createTx(spendTx);
        unsignedTxObservable.subscribe(
                it -> {
                    Assertions.assertEquals(it, unsignedTxNative);
                    async.complete();
                },
                failure -> {
                    context.fail();
                });
    }

    //@Ignore // tx is invalid at the moment
    @Test
    public void postSpendTxTest(TestContext context) throws ExecutionException, InterruptedException, CryptoException {
        Async async = context.async();

        BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);
        SpendTx spendTx = new SpendTx();
        spendTx.setSenderId(keyPair.getPublicKey());
        System.out.println(keyPair.getPublicKey());

        BaseKeyPair kp = keyPairService.generateBaseKeyPair();
        spendTx.setRecipientId(kp.getPublicKey());

        spendTx.setAmount(BigInteger.valueOf(1));
        spendTx.setPayload("payload");
        spendTx.setFee(BigInteger.valueOf(AEConfig.DEFAULT_FEE));
        spendTx.setTtl(BigInteger.valueOf(20000));
        spendTx.setNonce(BigInteger.valueOf(1));

        UnsignedTx unsignedTxNative = transactionServiceNative.createTx(spendTx).toFuture().get();
        Tx signedTx = transactionServiceNative.signTransaction(unsignedTxNative, keyPair.getPrivateKey());
        Observable<PostTxResponse> txResponseObservable = transactionServiceNative.postTransaction(signedTx);
        txResponseObservable.subscribe(
                it -> {
                    Assertions.assertEquals(it.getTxHash(), transactionServiceNative.computeTxHash(signedTx.getTx()));
                    async.complete();
                },
                failure -> {
                    context.fail();
                });
    }
}
