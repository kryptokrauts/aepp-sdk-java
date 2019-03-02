package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import io.reactivex.Single;
import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoException;

public interface TransactionService {

  Single<UnsignedTx> createSpendTx(
      String sender,
      String recipient,
      BigInteger amount,
      String payload,
      BigInteger fee,
      BigInteger ttl,
      BigInteger nonce);

  Single<PostTxResponse> postTransaction(Tx tx);

  Single<GenericSignedTx> getTransactionByHash(String txHash);

  /**
   * @param encodedSignedTx an encoded signed transaction
   * @return the hash from a signed and encoded transaction
   */
  String computeTxHash(String encodedSignedTx);

  /**
   * @param unsignedTx
   * @param privateKey
   * @return signed and encoded transaction
   * @throws CryptoException
   */
  Tx signTransaction(UnsignedTx unsignedTx, String privateKey) throws CryptoException;
}
