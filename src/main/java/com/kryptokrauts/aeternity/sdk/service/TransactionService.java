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
import org.bouncycastle.crypto.CryptoException;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        List<RlpType> rlpTypes = new ArrayList<>();
        rlpTypes.add(RlpString.create(SerializationTags.OBJECT_TAG_SPEND_TRANSACTION));
        rlpTypes.add(RlpString.create(SerializationTags.VSN));
        byte[] senderWithTag = EncodingUtils.decodeCheckAndTag(spendTx.getSenderId(), SerializationTags.ID_TAG_ACCOUNT);
        byte[] recipientWithTag = EncodingUtils.decodeCheckAndTag(spendTx.getRecipientId(), SerializationTags.ID_TAG_ACCOUNT);
        rlpTypes.add(RlpString.create(senderWithTag));
        rlpTypes.add(RlpString.create(recipientWithTag));
        rlpTypes.add(RlpString.create(spendTx.getAmount()));
        rlpTypes.add(RlpString.create(spendTx.getFee()));
        rlpTypes.add(RlpString.create(spendTx.getTtl()));
        rlpTypes.add(RlpString.create(spendTx.getNonce()));
        rlpTypes.add(RlpString.create(spendTx.getPayload()));
        RlpList rlpList = new RlpList(rlpTypes);
        byte[] rawTx = RlpEncoder.encode(rlpList);
        UnsignedTx unsignedTx = new UnsignedTx().tx(EncodingUtils.encodeCheck(rawTx, ApiIdentifiers.TRANSACTION));
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
        List<RlpType> rlpTypes = new ArrayList<>();
        rlpTypes.add(RlpString.create(SerializationTags.OBJECT_TAG_SIGNED_TRANSACTION));
        rlpTypes.add(RlpString.create(SerializationTags.VSN));
        rlpTypes.add(RlpString.create(sig));
        rlpTypes.add(RlpString.create(binaryTx));
        RlpList rlpList = new RlpList(rlpTypes);
        byte[] encodedRlp = RlpEncoder.encode(rlpList);
        return EncodingUtils.encodeCheck(encodedRlp, ApiIdentifiers.TRANSACTION);
    }
}
