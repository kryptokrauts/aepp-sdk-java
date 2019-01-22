package com.kryptokrauts.aeternity.generated.epoch.api;

import com.kryptokrauts.aeternity.generated.epoch.model.SpendTx;
import com.kryptokrauts.aeternity.generated.epoch.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.config.AEConfig;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import io.reactivex.Observable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.bouncycastle.crypto.CryptoException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class TransactionApiTest extends BaseTest {

    private static final String recipient = "ak_21C5mC3RWkS4sTpaNjZapXpY3jVWZZj11cE8MsUfzSqyeguzBb";

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

    //@Ignore // currently invalidTx
    @Test
    public void postSpendTxTest(TestContext context) throws ExecutionException, InterruptedException, CryptoException {
        Async async = context.async();

        BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);
        SpendTx spendTx = new SpendTx();
        spendTx.setSenderId(keyPair.getPublicKey());
        System.out.println(keyPair.getPublicKey());
        spendTx.setRecipientId(recipient);
        spendTx.setAmount(BigInteger.valueOf(1));
        spendTx.setPayload("payload");
        spendTx.setFee(BigInteger.valueOf(AEConfig.DEFAULT_FEE));
        spendTx.setTtl(BigInteger.valueOf(AEConfig.DEFAULT_TX_TTL));
        spendTx.setNonce(BigInteger.valueOf(1));
//        AccountApi accountApi = new AccountApi(new AccountApiImpl());
//        accountApi.rxGetAccountByPubkey(keyPair.getPublicKey()).toObservable().subscribe(it -> {
//                    System.out.println(it.getNonce());
//                    Assertions.assertEquals(it.getNonce().longValue(), 0);
//                    async.complete();
//                },
//                failure -> {
//                    context.fail();
//                });

//        UnsignedTx unsignedTxNative = transactionServiceNative.createTx(spendTx).toFuture().get();
//        Tx signedTx = transactionServiceNative.signTransaction(unsignedTxNative, keyPair.getPrivateKey());
//        Observable<PostTxResponse> txResponseObservable = transactionServiceNative.postTransaction(signedTx);
//        txResponseObservable.subscribe(
//                it -> {
//                    Assertions.assertEquals(it.getTxHash(), transactionServiceNative.computeTxHash(signedTx.getTx()));
//                    async.complete();
//                },
//                failure -> {
//                    context.fail();
//                });
    }
}
