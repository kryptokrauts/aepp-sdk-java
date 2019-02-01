package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.crypto.CryptoException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;

import io.reactivex.Observable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class TransactionApiTest extends BaseTest {

    @Test
    public void buildNativeTransactionTest( TestContext context ) throws ExecutionException, InterruptedException {
        Async async = context.async();

        String sender = keyPairService.generateBaseKeyPair().getPublicKey();
        String recipient = keyPairService.generateBaseKeyPair().getPublicKey();
        SpendTx spendTx = new SpendTx();
        spendTx.setSenderId( sender );
        spendTx.setRecipientId( recipient );
        spendTx.setAmount( BigInteger.valueOf( 1000 ) );
        spendTx.setPayload( "payload" );
        spendTx.setFee( BigInteger.valueOf( 1 ) );
        spendTx.setTtl( BigInteger.valueOf( 100 ) );
        spendTx.setNonce( BigInteger.valueOf( 5 ) );

        UnsignedTx unsignedTxNative = transactionServiceNative.createTx( spendTx ).toFuture().get();

        Observable<UnsignedTx> unsignedTxObservable = transactionServiceDebug.createTx( spendTx );
        unsignedTxObservable.subscribe( it -> {
            Assertions.assertEquals( it, unsignedTxNative );
            async.complete();
        }, failure -> {
            context.fail();
        } );
    }

    @Test
    public void postSpendTxTest( TestContext context ) throws ExecutionException, InterruptedException, CryptoException {
        Async async = context.async();

        BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret( BENEFICIARY_PRIVATE_KEY );
        SpendTx spendTx = new SpendTx();
        spendTx.setSenderId( keyPair.getPublicKey() );

        // get the currents accounts nonce in case a transaction is already
        // created and increase it by one
        Observable<Account> acc = accountService.getAccount( keyPair.getPublicKey() );
        acc.subscribe( account -> {
            BaseKeyPair kp = keyPairService.generateBaseKeyPair();
            spendTx.setRecipientId( kp.getPublicKey() );

            spendTx.setAmount( BigInteger.valueOf( 1 ) );
            spendTx.setPayload( "payload" );
            spendTx.setFee( BigInteger.valueOf( BaseConstants.DEFAULT_FEE ) );
            spendTx.setTtl( BigInteger.valueOf( 20000 ) );
            spendTx.setNonce( account.getNonce().add( BigInteger.ONE ) );

            UnsignedTx unsignedTxNative = transactionServiceNative.createTx( spendTx ).toFuture().get();
            Tx signedTx = transactionServiceNative.signTransaction( unsignedTxNative, keyPair.getPrivateKey() );
            Observable<PostTxResponse> txResponseObservable = transactionServiceNative.postTransaction( signedTx );
            txResponseObservable.subscribe( it -> {
                Assertions.assertEquals( it.getTxHash(), transactionServiceNative.computeTxHash( signedTx.getTx() ) );
                async.complete();
            }, failure -> {
                context.fail();
            } );
        } );
    }
}
