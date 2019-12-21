package com.kryptokrauts.aeternity.sdk.service.transaction.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionWaitTimeoutExpiredException;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import io.reactivex.Single;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  protected static final Logger _logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

  @Nonnull private AeternityServiceConfiguration config;

  @Nonnull private ExternalApi externalApi;

  @Nonnull private DefaultApi compilerApi;

  @Override
  public Single<StringResultWrapper> asyncCreateUnsignedTransaction(
      AbstractTransactionModel<?> tx) {
    return StringResultWrapper.builder()
        .build()
        .asyncGet(
            tx.buildTransaction(externalApi, compilerApi)
                .createUnsignedTransaction(config.isNativeMode(), config.getMinimalGasPrice())
                .map(single -> single.getTx()));
  }

  @Override
  public StringResultWrapper blockingCreateUnsignedTransaction(AbstractTransactionModel<?> tx) {
    return StringResultWrapper.builder()
        .build()
        .blockingGet(
            tx.buildTransaction(externalApi, compilerApi)
                .createUnsignedTransaction(config.isNativeMode(), config.getMinimalGasPrice())
                .map(result -> result.getTx()));
  }

  @Override
  public Single<PostTransactionResult> asyncPostTransaction(AbstractTransactionModel<?> tx) {
    return this.asyncPostTransaction(tx, this.config.getBaseKeyPair().getPrivateKey());
  }

  @Override
  public Single<PostTransactionResult> asyncPostTransaction(
      AbstractTransactionModel<?> tx, String privateKey) throws TransactionCreateException {
    return PostTransactionResult.builder()
        .build()
        .asyncGet(
            externalApi.rxPostTransaction(
                createGeneratedTxObject(
                    signTransaction(
                        asyncCreateUnsignedTransaction(tx).blockingGet().getResult(),
                        privateKey))));
  }

  @Override
  public PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx) {
    return this.blockingPostTransaction(tx, this.config.getBaseKeyPair().getPrivateKey());
  }

  @Override
  public PostTransactionResult blockingPostTransaction(String signedTx) {
    return this.waitUntilTransactionIsMined(
        PostTransactionResult.builder()
            .build()
            .blockingGet(externalApi.rxPostTransaction(createGeneratedTxObject(signedTx))));
  }

  @Override
  public Single<PostTransactionResult> asyncPostTransaction(String signedTx) {
    return PostTransactionResult.builder()
        .build()
        .asyncGet(externalApi.rxPostTransaction(createGeneratedTxObject(signedTx)));
  }

  @Override
  public PostTransactionResult blockingPostTransaction(
      AbstractTransactionModel<?> tx, String privateKey) throws TransactionCreateException {
    return this.waitUntilTransactionIsMined(
        PostTransactionResult.builder()
            .build()
            .blockingGet(
                externalApi.rxPostTransaction(
                    createGeneratedTxObject(
                        signTransaction(
                            asyncCreateUnsignedTransaction(tx).blockingGet().getResult(),
                            privateKey)))));
  }

  @Override
  public String computeTxHash(final AbstractTransactionModel<?> tx)
      throws TransactionCreateException {
    byte[] signed =
        EncodingUtils.decodeCheckWithIdentifier(
            signTransaction(
                asyncCreateUnsignedTransaction(tx).blockingGet().getResult(),
                this.config.getBaseKeyPair().getPrivateKey()));
    return EncodingUtils.hashEncode(signed, ApiIdentifiers.TRANSACTION_HASH);
  }

  @Override
  public String signTransaction(final String unsignedTx, final String privateKey)
      throws TransactionCreateException {
    try {
      byte[] networkData = config.getNetwork().getId().getBytes(StandardCharsets.UTF_8);
      byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier(unsignedTx);
      byte[] txAndNetwork = ByteUtils.concatenate(networkData, binaryTx);
      byte[] sig = SigningUtil.sign(txAndNetwork, privateKey);
      String encodedSignedTx = encodeSignedTransaction(sig, binaryTx);
      return encodedSignedTx;
    } catch (Exception e) {
      throw createException(e);
    }
  }

  @Override
  public Single<DryRunTransactionResults> asyncDryRunTransactions(DryRunRequest input) {
    return DryRunTransactionResults.builder()
        .build()
        .asyncGet(this.externalApi.rxDryRunTxs(input.toGeneratedModel()));
  }

  @Override
  public DryRunTransactionResults blockingDryRunTransactions(DryRunRequest input) {
    return DryRunTransactionResults.builder()
        .build()
        .blockingGet(this.externalApi.rxDryRunTxs(input.toGeneratedModel()));
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
              rlpWriter.writeInt(SerializationTags.VSN_1);
              rlpWriter.writeList(
                  writer -> {
                    writer.writeByteArray(sig);
                  });
              rlpWriter.writeByteArray(binaryTx);
            });
    return EncodingUtils.encodeCheck(encodedRlp.toArray(), ApiIdentifiers.TRANSACTION);
  }

  @Override
  public String toString() {
    return this.config.getNetwork().getId() + " " + this.config.isNativeMode();
  }

  private TransactionCreateException createException(Exception e) {
    return new TransactionCreateException(
        String.format("Technical error creating exception: ", e.getMessage()), e);
  }

  private Tx createGeneratedTxObject(String signedTx) {
    Tx tx = new Tx();
    tx.setTx(signedTx);
    return tx;
  }

  private PostTransactionResult waitUntilTransactionIsMined(
      PostTransactionResult postTransactionResult) {
    if (postTransactionResult != null) {
      String transactionHash = postTransactionResult.getTxHash();
      int currentBlockHeight = -1;
      GenericSignedTx minedTransaction = null;
      int elapsedTrials = 1;
      while (currentBlockHeight == -1
          && elapsedTrials < this.config.getNumTrialsToWaitForTxMined()) {
        minedTransaction = this.externalApi.rxGetTransactionByHash(transactionHash).blockingGet();
        if (minedTransaction.getBlockHeight().intValue() > 1) {
          _logger.debug("Transaction is mined - " + minedTransaction.toString());
          currentBlockHeight = minedTransaction.getBlockHeight().intValue();
        } else {
          double timeSpan =
              Double.valueOf(this.config.getMillisBetweenTrialsToWaitForTxMined()) / 1000;
          _logger.info(
              String.format(
                  "Transaction not mined yet, checking again in %.3f seconds (trial %s of %s)",
                  timeSpan, elapsedTrials, this.config.getNumTrialsToWaitForTxMined()));
          try {
            Thread.sleep(this.config.getMillisBetweenTrialsToWaitForTxMined());
          } catch (InterruptedException e) {
            throw new AException(
                String.format(
                    "Waiting for transaction %s to be mined was interrupted due to technical error",
                    transactionHash),
                e);
          }
          elapsedTrials++;
        }
      }
      if (currentBlockHeight == -1) {
        throw new TransactionWaitTimeoutExpiredException(
            String.format(
                "Transaction %s was not mined after %s trials, aborting",
                transactionHash, elapsedTrials));
      }
    }
    return postTransactionResult;
  }
}
