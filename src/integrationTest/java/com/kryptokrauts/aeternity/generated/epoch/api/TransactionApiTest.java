package com.kryptokrauts.aeternity.generated.epoch.api;

import com.kryptokrauts.aeternity.generated.epoch.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.epoch.model.SpendTx;
import com.kryptokrauts.aeternity.generated.epoch.model.Tx;
import com.kryptokrauts.aeternity.generated.epoch.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import io.reactivex.Observable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.bouncycastle.crypto.CryptoException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.ExecutionException;

public class TransactionApiTest extends BaseTest {

    private static final String recipient = "ak_21C5mC3RWkS4sTpaNjZapXpY3jVWZZj11cE8MsUfzSqyeguzBb";

    @Ignore // currently debug api doesn't work when running local docker nodes
    @Test
    public void buildNativeTransactionTest(TestContext context) throws ExecutionException, InterruptedException {
        Async async = context.async();

        String sender = keyPairService.generateBaseKeyPair().getPublicKey();
        String recipient = keyPairService.generateBaseKeyPair().getPublicKey();
        SpendTx spendTx = new SpendTx();
        spendTx.setSenderId(sender);
        spendTx.setRecipientId(recipient);
        spendTx.setAmount(1000L);
        spendTx.setPayload("payload");
        spendTx.setFee(1L);
        spendTx.setTtl(100L);
        spendTx.setNonce(5L);

        UnsignedTx unsignedTxNative = transactionService.createTx(spendTx).toFuture().get();

        Observable<UnsignedTx> unsignedTxObservable = transactionService.createTx(spendTx);
        unsignedTxObservable.subscribe(
                it -> {
                    Assertions.assertEquals(it, unsignedTxNative);
                    async.complete();
                },
                failure -> {
                    context.fail();
                });
    }

    @Test
    public void postSpendTxTest(TestContext context) throws ExecutionException, InterruptedException, CryptoException {
        Async async = context.async();

        BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);
        SpendTx spendTx = new SpendTx();
        spendTx.setSenderId(keyPair.getPublicKey());
        spendTx.setRecipientId(recipient);
        spendTx.setAmount(100000L);
        spendTx.setPayload("payload");
        spendTx.setFee(10000L);
        spendTx.setTtl(100L);
        spendTx.setNonce(0L);

        UnsignedTx unsignedTxNative = transactionService.createTx(spendTx).toFuture().get();
        Tx signedTx = transactionService.signTransaction(unsignedTxNative, keyPair.getPrivateKey());
        Observable<PostTxResponse> txResponseObservable = transactionService.postTransaction(signedTx);
        txResponseObservable.subscribe(
                it -> {
                    Assertions.assertEquals(it.getTxHash(), transactionService.computeTxHash(signedTx.getTx()));
                    async.complete();
                },
                failure -> {
                    context.fail();
                });
    }
}
