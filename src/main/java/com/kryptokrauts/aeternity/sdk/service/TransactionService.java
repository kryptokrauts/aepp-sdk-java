package com.kryptokrauts.aeternity.sdk.service;

import com.kryptokrauts.aeternity.generated.epoch.api.TransactionApiImpl;
import com.kryptokrauts.aeternity.generated.epoch.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.epoch.model.*;
import com.kryptokrauts.aeternity.sdk.AEKit;
import com.kryptokrauts.aeternity.sdk.config.Network;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.signer.service.SignerService;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Observable;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.rlp.RLP;
import org.bouncycastle.crypto.CryptoException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * @TODO refactor to factory pattern
 */
public class TransactionService {

    private static TransactionService instanceNative;
    private static TransactionService instance;

    private TransactionApi transactionApi;

    // true = build tx-objects native with sdk
    // false = build tx-objects through internal api
    private boolean nativeMode;
    private Network network;

    public TransactionService(final Network network) {
        this.network = network;
    }

    // TODO use AEConfig for initialization
    public static TransactionService getInstance(final boolean nativeMode, final Network network) {
        if (instanceNative == null) {
            instanceNative = new TransactionService(network);
            instanceNative.nativeMode = true;
            instance = new TransactionService(network);
            instance.nativeMode = false;
        }
        if (nativeMode) {
            instanceNative.transactionApi = new TransactionApi(new TransactionApiImpl());
            return instanceNative;
        }
        instance.transactionApi = new TransactionApi(new TransactionApiImpl());
        return instance;
    }

    public Observable<UnsignedTx> createTx(SpendTx spendTx) {
        return this.nativeMode ? Observable.just(spendTxNative(spendTx)) : spendTxInternal(spendTx);
    }

    public Observable<PostTxResponse> postTransaction(Tx tx) {
        return transactionApi.rxPostTransaction(tx).toObservable();
    }

    public Observable<GenericSignedTx> getTransactionByHash(String txHash) {
        return transactionApi.rxGetTransactionByHash(txHash).toObservable();
    }

    private Observable<UnsignedTx> spendTxInternal(SpendTx spendTx) {
        return transactionApi.rxPostSpend(spendTx).toObservable();
    }

    private UnsignedTx spendTxNative(SpendTx spendTx) {
        Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
            rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SPEND_TRANSACTION);
            rlpWriter.writeInt(SerializationTags.VSN);
            byte[] senderWithTag = EncodingUtils.decodeCheckAndTag(spendTx.getSenderId(), SerializationTags.ID_TAG_ACCOUNT);
            byte[] recipientWithTag = EncodingUtils.decodeCheckAndTag(spendTx.getRecipientId(), SerializationTags.ID_TAG_ACCOUNT);
            rlpWriter.writeByteArray(senderWithTag);
            rlpWriter.writeByteArray(recipientWithTag);
            rlpWriter.writeBigInteger(spendTx.getAmount());
            rlpWriter.writeBigInteger(spendTx.getFee());
            rlpWriter.writeBigInteger(spendTx.getTtl());
            rlpWriter.writeBigInteger(spendTx.getNonce());
            rlpWriter.writeString(spendTx.getPayload());
        });
        UnsignedTx unsignedTx = new UnsignedTx().tx(EncodingUtils.encodeCheck(encodedRlp.toArray(), ApiIdentifiers.TRANSACTION));
        return unsignedTx;
    }

    /**
     *
     * @param encodedSignedTx an encoded signed transaction
     * @return the hash from a signed and encoded transaction
     */
    public String computeTxHash(final String encodedSignedTx) {
        byte[] signed = EncodingUtils.decodeCheckWithIdentifier(encodedSignedTx);
        return EncodingUtils.hashEncode(signed, ApiIdentifiers.TRANSACTION_HASH);
    }

    /**
     *
     * @param unsignedTx
     * @param privateKey
     * @return signed and encoded transaction
     * @throws CryptoException
     */
    public Tx signTransaction(final UnsignedTx unsignedTx, final String privateKey) throws CryptoException {
        byte[] networkData = this.network.getId().getBytes(StandardCharsets.UTF_8);
        byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier(unsignedTx.getTx());
        byte[] txAndNetwork = ByteUtils.concatenate(networkData, binaryTx);
        SignerService signerService = AEKit.getSignerService(); // TODO the right way to access other services?
        byte[] sig = signerService.sign(txAndNetwork, privateKey);
        String encodedSignedTx = encodeSignedTransaction(sig, binaryTx);
        Tx tx = new Tx();
        tx.setTx(encodedSignedTx);
        return tx;
    }

    /**
     *
     * @param sig
     * @param binaryTx
     * @return encoded transaction
     */
    private String encodeSignedTransaction(byte[] sig, byte[] binaryTx) {
        Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
            rlpWriter.writeBigInteger(BigInteger.valueOf(SerializationTags.OBJECT_TAG_SIGNED_TRANSACTION));
            rlpWriter.writeBigInteger(BigInteger.valueOf(SerializationTags.VSN));
            rlpWriter.writeList(writer -> {
                writer.writeByteArray(sig);
            });
            rlpWriter.writeByteArray(binaryTx);
        });
        return EncodingUtils.encodeCheck(encodedRlp.toArray(), ApiIdentifiers.TRANSACTION);
    }
}
