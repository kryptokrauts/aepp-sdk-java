package com.kryptokrauts.aeternity.sdk.service.transaction.impl;

import com.kryptokrauts.aeternity.generated.api.TransactionApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.model.*;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import io.reactivex.Observable;
import lombok.RequiredArgsConstructor;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.rlp.RLP;
import org.bouncycastle.crypto.CryptoException;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @Nonnull
    private TransactionServiceConfiguration config;

    private TransactionApi transactionApi;

    private TransactionApi getTransactionApi() {
        if ( transactionApi == null ) {
            transactionApi = new TransactionApi( new TransactionApiImpl( config.getApiClient() ) );
        }
        return transactionApi;
    }

    @Override
    public Observable<UnsignedTx> createSpendTx( String sender, String recipient, BigInteger amount, String payload, BigInteger fee, BigInteger ttl, BigInteger nonce ) {
        SpendTx spendTx = new SpendTx();
        spendTx.setSenderId( sender );
        spendTx.setRecipientId( recipient );
        spendTx.setAmount( amount );
        spendTx.setPayload( payload );
        spendTx.setFee( fee );
        spendTx.setTtl( ttl );
        spendTx.setNonce( nonce );
        return config.isNativeMode() ? Observable.just( spendTxNative( spendTx ) ) : spendTxInternal( spendTx );
    }

    @Override
    public Observable<PostTxResponse> postTransaction( Tx tx ) {
        return getTransactionApi().rxPostTransaction( tx ).toObservable();
    }

    @Override
    public Observable<GenericSignedTx> getTransactionByHash( String txHash ) {
        return getTransactionApi().rxGetTransactionByHash( txHash ).toObservable();
    }

    @Override
    public String computeTxHash( final String encodedSignedTx ) {
        byte[] signed = EncodingUtils.decodeCheckWithIdentifier( encodedSignedTx );
        return EncodingUtils.hashEncode( signed, ApiIdentifiers.TRANSACTION_HASH );
    }

    @Override
    public Tx signTransaction( final UnsignedTx unsignedTx, final String privateKey ) throws CryptoException {
        byte[] networkData = config.getNetwork().getId().getBytes( StandardCharsets.UTF_8 );
        byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier( unsignedTx.getTx() );
        byte[] txAndNetwork = ByteUtils.concatenate( networkData, binaryTx );
        byte[] sig = SigningUtil.sign( txAndNetwork, privateKey );
        String encodedSignedTx = encodeSignedTransaction( sig, binaryTx );
        Tx tx = new Tx();
        tx.setTx( encodedSignedTx );
        return tx;
    }

    /**
     * for validate native tx generation
     */
    private Observable<UnsignedTx> spendTxInternal( SpendTx spendTx ) {
        return getTransactionApi().rxPostSpend( spendTx ).toObservable();
    }

    private UnsignedTx spendTxNative( SpendTx spendTx ) {
        Bytes encodedRlp = RLP.encodeList( rlpWriter -> {
            rlpWriter.writeInt( SerializationTags.OBJECT_TAG_SPEND_TRANSACTION );
            rlpWriter.writeInt( SerializationTags.VSN );
            byte[] senderWithTag = EncodingUtils.decodeCheckAndTag( spendTx.getSenderId(), SerializationTags.ID_TAG_ACCOUNT );
            byte[] recipientWithTag = EncodingUtils.decodeCheckAndTag( spendTx.getRecipientId(), SerializationTags.ID_TAG_ACCOUNT );
            rlpWriter.writeByteArray( senderWithTag );
            rlpWriter.writeByteArray( recipientWithTag );
            rlpWriter.writeBigInteger( spendTx.getAmount() );
            rlpWriter.writeBigInteger( spendTx.getFee() );
            rlpWriter.writeBigInteger( spendTx.getTtl() );
            rlpWriter.writeBigInteger( spendTx.getNonce() );
            rlpWriter.writeString( spendTx.getPayload() );
        } );
        UnsignedTx unsignedTx = new UnsignedTx().tx( EncodingUtils.encodeCheck( encodedRlp.toArray(), ApiIdentifiers.TRANSACTION ) );
        return unsignedTx;
    }

    /**
     *
     * @param sig
     * @param binaryTx
     * @return encoded transaction
     */
    private String encodeSignedTransaction( byte[] sig, byte[] binaryTx ) {
        Bytes encodedRlp = RLP.encodeList( rlpWriter -> {
            rlpWriter.writeBigInteger( BigInteger.valueOf( SerializationTags.OBJECT_TAG_SIGNED_TRANSACTION ) );
            rlpWriter.writeBigInteger( BigInteger.valueOf( SerializationTags.VSN ) );
            rlpWriter.writeList( writer -> {
                writer.writeByteArray( sig );
            } );
            rlpWriter.writeByteArray( binaryTx );
        } );
        return EncodingUtils.encodeCheck( encodedRlp.toArray(), ApiIdentifiers.TRANSACTION );
    }
}
