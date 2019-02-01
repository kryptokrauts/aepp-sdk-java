package com.kryptokrauts.aeternity.sdk.service.transaction;

import org.bouncycastle.crypto.CryptoException;

import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;

import io.reactivex.Observable;

public interface TransactionService {

    Observable<UnsignedTx> createTx( SpendTx spendTx );

    Observable<PostTxResponse> postTransaction( Tx tx );

    Observable<GenericSignedTx> getTransactionByHash( String txHash );

    /**
     *
     * @param encodedSignedTx
     *            an encoded signed transaction
     * @return the hash from a signed and encoded transaction
     */
    String computeTxHash( String encodedSignedTx );

    /**
     *
     * @param unsignedTx
     * @param privateKey
     * @return signed and encoded transaction
     * @throws CryptoException
     */
    Tx signTransaction( UnsignedTx unsignedTx, String privateKey ) throws CryptoException;

}
