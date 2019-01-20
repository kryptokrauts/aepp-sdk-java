package com.kryptokrauts.aeternity.generated.epoch.api;

import com.kryptokrauts.aeternity.generated.epoch.model.SpendTx;
import com.kryptokrauts.aeternity.generated.epoch.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.AEKit;
import com.kryptokrauts.aeternity.sdk.config.Network;
import com.kryptokrauts.aeternity.sdk.keypair.service.KeyPairService;
import io.reactivex.Observable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.ExecutionException;

public class TransactionApiTest extends BaseTest {

    @Test
    public void buildNativeTransactionTest(TestContext context) throws ExecutionException, InterruptedException {
        Async async = context.async();

        KeyPairService keyPairService = AEKit.getKeyPairService();
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

        UnsignedTx unsignedTxNative = AEKit.getTransactionService(true, Network.NETWORK_ID_TESTNET).createTx(spendTx).toFuture().get();
        System.out.println(unsignedTxNative.getTx());

        Observable<UnsignedTx> unsignedTxObservable = AEKit.getTransactionService(false, Network.NETWORK_ID_TESTNET).createTx(spendTx);
        unsignedTxObservable.subscribe(
                it -> {
                    Assertions.assertEquals(it, unsignedTxNative);
                    async.complete();
                },
                failure -> {
                    context.fail();
                });
    }
}
