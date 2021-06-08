package com.kryptokrauts.aeternity.sdk.service.transaction.impl;

import com.kryptokrauts.aeternity.generated.ApiException;
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
import com.kryptokrauts.aeternity.sdk.service.info.InfoService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
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

  @Nonnull private InfoService infoService;

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
    return this.asyncPostTransaction(tx, this.config.getKeyPair().getEncodedPrivateKey());
  }

  @Override
  public Single<PostTransactionResult> asyncPostTransaction(
      AbstractTransactionModel<?> tx, String privateKey) throws TransactionCreateException {
    String signedTx = blockingCreateUnsignedTransaction(tx).getResult();
    if (tx.doSign()) {
      signedTx = signTransaction(signedTx, privateKey);
    }
    return PostTransactionResult.builder()
        .build()
        .asyncGet(externalApi.rxPostTransaction(createGeneratedTxObject(signedTx)));
  }

  @Override
  public PostTransactionResult blockingPostTransaction(AbstractTransactionModel<?> tx) {
    return this.blockingPostTransaction(tx, this.config.getKeyPair().getEncodedPrivateKey());
  }

  @Override
  public PostTransactionResult blockingPostTransaction(String signedTx) {
    PostTransactionResult postTransactionResult =
        PostTransactionResult.builder()
            .build()
            .blockingGet(externalApi.rxPostTransaction(createGeneratedTxObject(signedTx)));
    if (this.config.isWaitForTxIncludedInBlockEnabled()) {
      return this.waitUntilTransactionIsIncludedInBlock(postTransactionResult);
    }
    return postTransactionResult;
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
    if (tx.hasInnerTx()) {
      StringResultWrapper innerTxUnsigned = blockingCreateUnsignedTransaction(tx.getInnerTxModel());
      if (tx.doSignInnerTx()) {
        tx.setInnerTxHash(
            signInnerTransaction(innerTxUnsigned.getResult(), tx.getPrivateKeyToSignerInnerTx()));
      }
    }
    String signedTx = blockingCreateUnsignedTransaction(tx).getResult();
    if (tx.doSign()) {
      signedTx = signTransaction(signedTx, privateKey);
    }
    PostTransactionResult postTransactionResult =
        PostTransactionResult.builder()
            .build()
            .blockingGet(externalApi.rxPostTransaction(createGeneratedTxObject(signedTx)));
    if (this.config.isWaitForTxIncludedInBlockEnabled()) {
      return this.waitUntilTransactionIsIncludedInBlock(postTransactionResult);
    }
    return postTransactionResult;
  }

  @Override
  public String computeTxHash(final AbstractTransactionModel<?> tx)
      throws TransactionCreateException {
    byte[] signed =
        EncodingUtils.decodeCheckWithIdentifier(
            signTransaction(
                blockingCreateUnsignedTransaction(tx).getResult(),
                this.config.getKeyPair().getEncodedPrivateKey()));
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

  /**
   * sign an unsigned transaction with the given private key. method uses an additional prefix and
   * must be used to sign inner transactions of PayingForTx
   *
   * @param unsignedTx the encoded unsigned transaction
   * @param privateKey the encoded private key to sign the transaction
   * @return signed and encoded transaction
   * @throws TransactionCreateException if an error occurs
   */
  private String signInnerTransaction(final String unsignedTx, final String privateKey)
      throws TransactionCreateException {
    try {
      byte[] networkDataWithAdditionalPrefix =
          (config.getNetwork().getId() + "-" + "inner_tx").getBytes(StandardCharsets.UTF_8);
      byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier(unsignedTx);
      byte[] txAndNetwork = ByteUtils.concatenate(networkDataWithAdditionalPrefix, binaryTx);
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

  @Override
  public Single<TransactionResult> asyncWaitForConfirmation(final String txHash) {
    return this.asyncWaitForConfirmation(txHash, this.config.getNumOfConfirmations());
  }

  @Override
  public Single<TransactionResult> asyncWaitForConfirmation(
      final String txHash, final int numOfConfirmations) {
    final BigInteger[] heights = new BigInteger[2];
    final boolean setConfirmationHeight[] = {true};
    return infoService
        .asyncGetCurrentKeyBlock()
        .doOnSuccess(
            keyBlockResult -> {
              heights[0] = keyBlockResult.getHeight(); // currentHeight
              if (setConfirmationHeight[0]) {
                heights[1] =
                    keyBlockResult
                        .getHeight()
                        .add(BigInteger.valueOf(numOfConfirmations)); // confirmationHeight
                _logger.info(
                    String.format(
                        "waiting %s keyblocks beginning at height %s to wait for confirmation of tx: %s",
                        numOfConfirmations, heights[0], txHash));
                setConfirmationHeight[0] = false;
              }
              _logger.debug(
                  String.format(
                      "waiting for confirmation - current height: %s - confirmation height: %s - tx: %s",
                      heights[0], heights[1], txHash));
            })
        .delay(config.getMillisBetweenTrailsToWaitForConfirmation(), TimeUnit.MILLISECONDS)
        .repeatUntil(() -> heights[0].compareTo(heights[1]) >= 0)
        .switchMap(
            keyBlockResult -> {
              // get transaction only if current height >= confirmationHeight
              if (heights[0].compareTo(heights[1]) >= 0) {
                return infoService
                    .asyncGetTransactionByHash(txHash)
                    .toFlowable()
                    .onErrorReturn(
                        throwable -> {
                          if (throwable instanceof ApiException) {
                            return TransactionResult.builder()
                                .hash(txHash)
                                .aeAPIErrorMessage(throwable.getMessage())
                                .rootErrorMessage(((ApiException) throwable).getResponseBody())
                                .build();
                          }
                          return TransactionResult.builder()
                              .hash(txHash)
                              .rootErrorMessage(throwable.getMessage())
                              .build();
                        });
              }
              return Flowable.empty();
            })
        .singleOrError();
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

  private PostTransactionResult waitUntilTransactionIsIncludedInBlock(
      PostTransactionResult postTransactionResult) {
    if (postTransactionResult != null) {
      String transactionHash = postTransactionResult.getTxHash();
      int currentBlockHeight = -1;
      GenericSignedTx includedTransaction = null;
      int elapsedTrials = 1;
      while (currentBlockHeight == -1
          && elapsedTrials < this.config.getNumTrialsToWaitForTxIncludedInBlock()) {
        includedTransaction =
            this.externalApi.rxGetTransactionByHash(transactionHash).blockingGet();
        if (includedTransaction.getBlockHeight().intValue() > 1) {
          _logger.debug("Transaction is included in a block - " + includedTransaction.toString());
          currentBlockHeight = includedTransaction.getBlockHeight().intValue();
        } else {
          double timeSpan =
              Double.valueOf(this.config.getMillisBetweenTrialsToWaitForTxIncludedInBlock()) / 1000;
          _logger.info(
              String.format(
                  "Transaction not included in a block yet, checking again in %.3f seconds (trial %s of %s)",
                  timeSpan, elapsedTrials, this.config.getNumTrialsToWaitForTxIncludedInBlock()));
          try {
            Thread.sleep(this.config.getMillisBetweenTrialsToWaitForTxIncludedInBlock());
          } catch (InterruptedException e) {
            throw new AException(
                String.format(
                    "Waiting for transaction %s to be included in a block was interrupted due to technical error",
                    transactionHash),
                e);
          }
          elapsedTrials++;
        }
      }
      if (currentBlockHeight == -1) {
        throw new TransactionWaitTimeoutExpiredException(
            String.format(
                "Transaction %s was not included in a block after %s trials, aborting",
                transactionHash, elapsedTrials));
      }
    }
    return postTransactionResult;
  }
}
