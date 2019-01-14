package com.kryptokrauts.aeternity.sdk.service;

import com.kryptokrauts.aeternity.generated.epoch.api.TransactionApiImpl;
import com.kryptokrauts.aeternity.generated.epoch.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.epoch.model.SpendTx;
import com.kryptokrauts.aeternity.generated.epoch.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Observable;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kryptokrauts.aeternity.sdk.util.EncodingType.BASE58;

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

    public static TransactionService getInstance(final boolean nativeMode) {
        if (instanceNative == null) {
            instanceNative = new TransactionService();
            instanceNative.nativeMode = true;
            instance = new TransactionService();
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

    private Observable<UnsignedTx> spendTxInternal(SpendTx spendTx) {
        return transactionApi.rxPostSpend(spendTx).toObservable();
    }

    private UnsignedTx spendTxNative(SpendTx spendTx) {
        List<RlpType> rlpTypes = new ArrayList<>();
        rlpTypes.add(RlpString.create(SerializationTags.OBJECT_TAG_SPEND_TRANSACTION));
        rlpTypes.add(RlpString.create(SerializationTags.VSN));

        // TODO refactoring for byte[] modifications, encoding and hashing
        byte[] accountTag = BigInteger.valueOf(SerializationTags.ID_TAG_ACCOUNT).toByteArray();
        byte[] sender = EncodingUtils.decodeCheck(spendTx.getSenderId().substring(3), BASE58);
        byte[] senderWithTag = new byte[accountTag.length + sender.length];
        System.arraycopy(accountTag, 0, senderWithTag, 0, accountTag.length);
        System.arraycopy(sender, 0, senderWithTag, accountTag.length, sender.length);
        byte[] recipient = EncodingUtils.decodeCheck(spendTx.getRecipientId().substring(3), BASE58);
        byte[] recipientWithTag = new byte[accountTag.length + sender.length];
        System.arraycopy(accountTag, 0, recipientWithTag, 0, accountTag.length);
        System.arraycopy(recipient, 0, recipientWithTag, accountTag.length, recipient.length);

        rlpTypes.add(RlpString.create(senderWithTag));
        rlpTypes.add(RlpString.create(recipientWithTag));
        rlpTypes.add(RlpString.create(spendTx.getAmount()));
        rlpTypes.add(RlpString.create(spendTx.getFee()));
        rlpTypes.add(RlpString.create(spendTx.getTtl()));
        rlpTypes.add(RlpString.create(spendTx.getNonce()));
        rlpTypes.add(RlpString.create(spendTx.getPayload()));
        RlpList rlpList = new RlpList(rlpTypes);
        byte[] rawTx = RlpEncoder.encode(rlpList);
        String txBase64 = EncodingUtils.encodeCheck(rawTx, EncodingType.BASE64);
        UnsignedTx unsignedTx = new UnsignedTx().tx(ApiIdentifiers.TRANSACTION + "_" + txBase64);
        return unsignedTx;
    }
}
