package com.kryptokrauts.aeternity.sdk.service.transaction.impl;

import com.kryptokrauts.aeternity.generated.ApiException;
import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.EncodedTx;
import com.kryptokrauts.aeternity.generated.model.SignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionWaitTimeoutExpiredException;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.info.InfoService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.CheckTxInPoolResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunAccountModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.PayingForTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import io.netty.util.internal.StringUtil;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  protected static final Logger _logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

  @Nonnull private AeternityServiceConfiguration config;

  @Nonnull private ExternalApi externalApi;

  @Nonnull private InternalApi internalApi;

  @Nonnull private InfoService infoService;

  @Override
  public Single<StringResultWrapper> asyncCreateUnsignedTransaction(
      AbstractTransactionModel<?> tx) {
    return StringResultWrapper.builder()
        .build()
        .asyncGet(
            tx.buildTransaction(externalApi, internalApi)
                .createUnsignedTransaction(config.isNativeMode(), config.getMinimalGasPrice())
                .map(single -> single.getTx()));
  }

  @Override
  public StringResultWrapper blockingCreateUnsignedTransaction(AbstractTransactionModel<?> tx) {
    return StringResultWrapper.builder()
        .build()
        .blockingGet(
            tx.buildTransaction(externalApi, internalApi)
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
        .asyncGet(externalApi.rxPostTransaction(createEncodedTxObject(signedTx), false));
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
            .blockingGet(externalApi.rxPostTransaction(createEncodedTxObject(signedTx), false));
    if (this.config.isWaitForTxIncludedInBlockEnabled()) {
      return this.waitUntilTransactionIsIncludedInBlock(postTransactionResult);
    }
    return postTransactionResult;
  }

  @Override
  public Single<PostTransactionResult> asyncPostTransaction(String signedTx) {
    return PostTransactionResult.builder()
        .build()
        .asyncGet(externalApi.rxPostTransaction(createEncodedTxObject(signedTx), false));
  }

  @Override
  public PostTransactionResult blockingPostTransaction(
      AbstractTransactionModel<?> tx, String privateKey) throws TransactionCreateException {
    /*
     * ga transactions have an inner tx model, for which an unsigned tx needs to be
     * created
     */
    if (tx.hasInnerTx()) {
      blockingCreateUnsignedTransaction(tx.getInnerTxModel());
    }
    String signedTx = blockingCreateUnsignedTransaction(tx).getResult();
    if (tx.doSign()) {
      signedTx = signTransaction(signedTx, privateKey);
    }
    PostTransactionResult postTransactionResult =
        PostTransactionResult.builder()
            .build()
            .blockingGet(externalApi.rxPostTransaction(createEncodedTxObject(signedTx), false));
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
  public String computeGAInnerTxHash(final AbstractTransactionModel<?> tx)
      throws TransactionCreateException {

    StringResultWrapper unsignedInnerTxResult = this.blockingCreateUnsignedTransaction(tx);

    if (!StringUtil.isNullOrEmpty(unsignedInnerTxResult.getAeAPIErrorMessage())) {
      return unsignedInnerTxResult.getAeAPIErrorMessage();
    }
    if (!StringUtil.isNullOrEmpty(unsignedInnerTxResult.getRootErrorMessage())) {
      return unsignedInnerTxResult.getRootErrorMessage();
    }

    byte[] networkDataWithAdditionalPrefix =
        (config.getNetwork().getId()).getBytes(StandardCharsets.UTF_8);
    byte[] txAndNetwork =
        ByteUtils.concatenate(
            networkDataWithAdditionalPrefix,
            EncodingUtils.decodeCheckWithIdentifier(unsignedInnerTxResult.getResult()));

    return new String(Hex.encode(EncodingUtils.hash(txAndNetwork)));
  }

  @Override
  public CheckTxInPoolResult blockingCheckTxInPool(final String txHash) {
    return CheckTxInPoolResult.builder()
        .build()
        .blockingGet(internalApi.rxGetCheckTxInPool(txHash, false));
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
  public String signPayingForInnerTransaction(
      final AbstractTransactionModel<?> model, final String privateKey)
      throws TransactionCreateException {
    try {
      if (model instanceof PayingForTransactionModel) {
        throw new TransactionCreateException(
            "Inner transaction of payingFor cannot be of type payingFor!",
            new IllegalArgumentException());
      }
      String unsignedTx = blockingCreateUnsignedTransaction(model).getResult();

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
    Single<DryRunResults> dryRunResultsSingle;
    if (config.isDebugDryRun()) {
      dryRunResultsSingle = this.internalApi.rxDryRunTxs(input.toGeneratedModel(), false);
    } else {
      dryRunResultsSingle = this.externalApi.rxProtectedDryRunTxs(input.toGeneratedModel(), false);
    }
    return DryRunTransactionResults.builder().build().asyncGet(dryRunResultsSingle);
  }

  @Override
  public DryRunTransactionResult blockingDryRunContractCall(
      ContractCallTransactionModel contractCall, boolean useZeroAddress) {
    DryRunRequest request;
    if (useZeroAddress) {
      ContractCallTransactionModel contractCallTransactionModel =
          contractCall
              .toBuilder()
              .callerId(config.getZeroAddressAccount())
              .nonce(getZeroAddressAccountNonce())
              .build();
      _logger.info(contractCallTransactionModel.toString());
      request =
          DryRunRequest.builder()
              .build()
              .account(
                  DryRunAccountModel.builder()
                      .amount(new BigInteger(config.getZeroAddressAccountAmount()))
                      .publicKey(config.getZeroAddressAccount())
                      .build())
              .transactionInputItem(contractCallTransactionModel);
    } else {
      request =
          DryRunRequest.builder()
              .build()
              .account(DryRunAccountModel.builder().publicKey(contractCall.getCallerId()).build())
              .transactionInputItem(contractCall);
    }
    Single<DryRunResults> dryRunResultsSingle;
    if (config.isDebugDryRun()) {
      dryRunResultsSingle = this.internalApi.rxDryRunTxs(request.toGeneratedModel(), false);
    } else {
      dryRunResultsSingle =
          this.externalApi.rxProtectedDryRunTxs(request.toGeneratedModel(), false);
    }
    return DryRunTransactionResults.builder().build().blockingGet(dryRunResultsSingle).getResults()
        .stream()
        .findFirst()
        .orElse(null);
  }

  @Override
  public DryRunTransactionResults blockingDryRunTransactions(DryRunRequest input) {
    Single<DryRunResults> dryRunResultsSingle;
    if (config.isDebugDryRun()) {
      dryRunResultsSingle = this.internalApi.rxDryRunTxs(input.toGeneratedModel(), false);
    } else {
      dryRunResultsSingle = this.externalApi.rxProtectedDryRunTxs(input.toGeneratedModel(), false);
    }
    return DryRunTransactionResults.builder().build().blockingGet(dryRunResultsSingle);
  }

  // get zero address accounts nonce, depending on configured network
  private BigInteger getZeroAddressAccountNonce() {
    if (Arrays.asList(Network.TESTNET, Network.MAINNET).contains(config.getNetwork())) {
      return BaseConstants.ZERO_ADDRESS_ACCOUNT_DEFAULT_NONCE;
    } else return BaseConstants.ZERO_ADDRESS_ACCOUNT_DEVNET_NONCE;
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
        String.format("Technical error creating exception: %s", e.getMessage()), e);
  }

  private EncodedTx createEncodedTxObject(String signedTx) {
    EncodedTx encodedTx = new EncodedTx();
    encodedTx.setTx(signedTx);
    return encodedTx;
  }

  private PostTransactionResult waitUntilTransactionIsIncludedInBlock(
      PostTransactionResult postTransactionResult) {
    if (postTransactionResult != null) {
      if (!StringUtil.isNullOrEmpty(postTransactionResult.getRootErrorMessage())) {
        throw new AException(
            "An error occured while waiting for transaction to be included in block: "
                + postTransactionResult.getRootErrorMessage(),
            postTransactionResult.getThrowable());
      }
      String transactionHash = postTransactionResult.getTxHash();
      int currentBlockHeight = -1;
      SignedTx includedTransaction = null;
      int elapsedTrials = 1;
      while (currentBlockHeight == -1
          && elapsedTrials < this.config.getNumTrialsToWaitForTxIncludedInBlock()) {
        includedTransaction =
            this.externalApi.rxGetTransactionByHash(transactionHash, false).blockingGet();
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
