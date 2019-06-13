package com.kryptokrauts.aeternity.sdk.service.transaction.impl;

import com.kryptokrauts.aeternity.generated.api.ChannelApiImpl;
import com.kryptokrauts.aeternity.generated.api.ContractApiImpl;
import com.kryptokrauts.aeternity.generated.api.TransactionApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.TransactionFactory;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import com.kryptokrauts.sophia.compiler.generated.api.DefaultApiImpl;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import io.reactivex.Single;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;
import org.bouncycastle.crypto.CryptoException;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  @Nonnull private TransactionServiceConfiguration config;

  private TransactionApi transactionApi;

  private ChannelApi channelApi;

  private ContractApi contractApi;

  private DefaultApi compilerApi;

  private TransactionFactory transactionFactory;

  @Override
  public TransactionFactory getTransactionFactory() {
    if (transactionFactory == null) {
      transactionFactory =
          new TransactionFactory(
              getTransactionApi(), getChannelApi(), getContractApi(), getCompilerApi());
    }
    return transactionFactory;
  }

  @Override
  public Single<UnsignedTx> createUnsignedTransaction(AbstractTransaction<?> tx) {
    return tx.createUnsignedTransaction(config.isNativeMode(), config.getMinimalGasPrice());
  }

  @Override
  public Single<PostTxResponse> postTransaction(Tx tx) {
    return getTransactionApi().rxPostTransaction(tx);
  }

  @Override
  public Single<GenericSignedTx> getTransactionByHash(String txHash) {
    return getTransactionApi().rxGetTransactionByHash(txHash);
  }

  @Override
  public String computeTxHash(final String encodedSignedTx) {
    byte[] signed = EncodingUtils.decodeCheckWithIdentifier(encodedSignedTx);
    return EncodingUtils.hashEncode(signed, ApiIdentifiers.TRANSACTION_HASH);
  }

  @Override
  public Tx signTransaction(final UnsignedTx unsignedTx, final String privateKey)
      throws CryptoException {
    byte[] networkData = config.getNetwork().getId().getBytes(StandardCharsets.UTF_8);
    byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier(unsignedTx.getTx());
    byte[] txAndNetwork = ByteUtils.concatenate(networkData, binaryTx);
    byte[] sig = SigningUtil.sign(txAndNetwork, privateKey);
    String encodedSignedTx = encodeSignedTransaction(sig, binaryTx);
    Tx tx = new Tx();
    tx.setTx(encodedSignedTx);
    return tx;
  }

  /**
   * @param sig
   * @param binaryTx
   * @return encoded transaction
   */
  private String encodeSignedTransaction(byte[] sig, byte[] binaryTx) {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SIGNED_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              rlpWriter.writeList(
                  writer -> {
                    writer.writeByteArray(sig);
                  });
              rlpWriter.writeByteArray(binaryTx);
            });
    return EncodingUtils.encodeCheck(encodedRlp.toArray(), ApiIdentifiers.TRANSACTION);
  }

  private TransactionApi getTransactionApi() {
    if (transactionApi == null) {
      transactionApi = new TransactionApi(new TransactionApiImpl(config.getApiClient()));
    }
    return transactionApi;
  }

  private ChannelApi getChannelApi() {
    if (channelApi == null) {
      channelApi = new ChannelApi(new ChannelApiImpl(config.getApiClient()));
    }
    return channelApi;
  }

  private ContractApi getContractApi() {
    if (contractApi == null) {
      contractApi = new ContractApi(new ContractApiImpl(config.getApiClient()));
    }
    return contractApi;
  }

  private DefaultApi getCompilerApi() {
    if (compilerApi == null) {
      compilerApi = new DefaultApi(new DefaultApiImpl(config.getCompilerApiClient()));
    }
    return compilerApi;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return this.config.getNetwork().getId() + " " + this.config.isNativeMode();
  }
}
