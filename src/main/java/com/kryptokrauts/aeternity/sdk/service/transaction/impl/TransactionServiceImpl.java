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
import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaTypeTransformer;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionWaitTimeoutExpiredException;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.info.InfoService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.CheckTxInPoolResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunAccountModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.PayingForTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import io.netty.util.internal.StringUtil;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
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

  @Nonnull private CompilerService compilerService;

  @Nonnull private AccountService accountService;

  @Override
  public Single<StringResultWrapper> asyncCreateUnsignedTransaction(
      AbstractTransactionModel<?> tx) {
    return StringResultWrapper.builder()
        .build()
        .asyncGet(
            tx.buildTransaction(externalApi, internalApi)
                .createUnsignedTransaction(config.isNativeMode(), config.getDefaultGasPrice())
                .map(single -> single.getTx()));
  }

  @Override
  public StringResultWrapper blockingCreateUnsignedTransaction(AbstractTransactionModel<?> tx) {
    return StringResultWrapper.builder()
        .build()
        .blockingGet(
            tx.buildTransaction(externalApi, internalApi)
                .createUnsignedTransaction(config.isNativeMode(), config.getDefaultGasPrice())
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
     * ga transactions have an inner tx model, for which an unsigned tx needs to be created
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
  public DryRunTransactionResult blockingDryRunContractTx(
      AbstractTransactionModel<?> contractTx, boolean useZeroAddress) {
    if (!(contractTx instanceof ContractCreateTransactionModel)
        && !(contractTx instanceof ContractCallTransactionModel)) {
      throw new InvalidParameterException(
          "contractTx must be one of: ContractCreateTransactionModel, ContractCallTransactionModel");
    }
    DryRunRequest request = null;
    if (useZeroAddress) {
      if (contractTx instanceof ContractCallTransactionModel) {
        ContractCallTransactionModel contractCallTransactionModel =
            ((ContractCallTransactionModel) contractTx)
                .toBuilder()
                .callerId(config.getZeroAddressAccount())
                .nonce(getZeroAddressAccountNonce())
                .build();
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
        throw new InvalidParameterException(
            "usage of zeroAddress is only allowed for ContractCallTransactionModel");
      }
    } else {
      String dryRunAccount;
      if (contractTx instanceof ContractCreateTransactionModel) {
        dryRunAccount = ((ContractCreateTransactionModel) contractTx).getOwnerId();
      } else {
        dryRunAccount = ((ContractCallTransactionModel) contractTx).getCallerId();
      }
      String unsignedTx = blockingCreateUnsignedTransaction(contractTx).getResult();
      request =
          DryRunRequest.builder()
              .build()
              .account(DryRunAccountModel.builder().publicKey(dryRunAccount).build())
              .transactionInputItem(unsignedTx);
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
        .orElseThrow(
            () -> new AException("DryRun call returned no result, please check environment"));
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

  @Override
  public ContractTxResult blockingContractCreate(String sourceCode, ContractTxOptions txOptions) {
    if (sourceCode == null || txOptions == null) {
      throw new InvalidParameterException("Arguments must not be null.");
    }
    String entrypoint = "init";
    String byteCode =
        this.compilerService.blockingCompile(sourceCode, txOptions.getFilesystem()).getResult();
    String callData =
        this.compilerService
            .blockingEncodeCalldata(
                sourceCode,
                entrypoint,
                SophiaTypeTransformer.toCompilerInput(txOptions.getParams()),
                txOptions.getFilesystem())
            .getResult();
    BigInteger nonce;
    nonce =
        txOptions.getNonce() != null
            ? txOptions.getNonce()
            : this.accountService.blockingGetNextNonce();
    ContractCreateTransactionModel contractCreateModel =
        new ContractCreateTransactionModel(
            byteCode,
            callData,
            this.config.getKeyPair().getAddress(),
            txOptions.getAmount(),
            nonce,
            txOptions.getGasLimit(),
            txOptions.getGasPrice(),
            txOptions.getTtl());
    ObjectResultWrapper objectResultWrapper;
    if (this.config.isDryRunStatefulCalls()) {
      DryRunTransactionResult dryRunResult = blockingDryRunContractTx(contractCreateModel, false);
      if (!("ok".equals(dryRunResult.getResult())
          && BaseConstants.CONTRACT_EMPTY_RETURN_DATA.equals(
              dryRunResult.getContractCallObject().getReturnValue()))) {
        objectResultWrapper =
            this.compilerService.blockingDecodeCallResult(
                sourceCode,
                entrypoint,
                dryRunResult.getContractCallObject().getReturnType(),
                dryRunResult.getContractCallObject().getReturnValue(),
                txOptions.getFilesystem());
        handleContractTxError(dryRunResult.getResult(), objectResultWrapper, entrypoint);
      }
      _logger.debug("Gas used in dry-run: {}", dryRunResult.getContractCallObject().getGasUsed());
      BigInteger gasLimitWithMargin =
          getGasLimitWithReserveMargin(dryRunResult.getContractCallObject().getGasUsed());
      _logger.debug(
          "Gas with reserve margin of {}: {}",
          this.config.getDryRunGasReserveMargin(),
          gasLimitWithMargin);
      contractCreateModel = contractCreateModel.toBuilder().gasLimit(gasLimitWithMargin).build();
    }
    PostTransactionResult contractCreatePostTxResult =
        this.blockingPostTransaction(contractCreateModel);
    if (contractCreatePostTxResult == null) {
      throw new RuntimeException("Unexpected error: transaction not broadcasted.");
    }
    TransactionInfoResult contractCallPostTxInfo =
        this.infoService.blockingGetTransactionInfoByHash(contractCreatePostTxResult.getTxHash());
    Object decodedValue = null;
    if (!BaseConstants.CONTRACT_EMPTY_RETURN_DATA.equals(
        contractCallPostTxInfo.getCallInfo().getReturnValue())) {
      objectResultWrapper =
          this.compilerService.blockingDecodeCallResult(
              sourceCode,
              entrypoint,
              contractCallPostTxInfo.getCallInfo().getReturnType(),
              contractCallPostTxInfo.getCallInfo().getReturnValue(),
              txOptions.getFilesystem());
      handleContractTxError(
          contractCallPostTxInfo.getCallInfo().getReturnType(), objectResultWrapper, entrypoint);
      decodedValue = objectResultWrapper.getResult();
    }
    return ContractTxResult.builder()
        .txHash(contractCreatePostTxResult.getTxHash())
        .callResult(contractCallPostTxInfo.getCallInfo())
        .decodedValue(decodedValue)
        .build();
  }

  @Override
  public ContractTxResult blockingContractCreate(String sourceCode) {
    return blockingContractCreate(sourceCode, ContractTxOptions.builder().build());
  }

  @Override
  public Object blockingReadOnlyContractCall(
      String contractId, String entrypoint, String sourceCode, ContractTxOptions txOptions) {
    if (contractId == null || entrypoint == null || sourceCode == null || txOptions == null) {
      throw new InvalidParameterException("Arguments must not be null.");
    }
    String calldata =
        this.compilerService
            .blockingEncodeCalldata(
                sourceCode,
                entrypoint,
                SophiaTypeTransformer.toCompilerInput(txOptions.getParams()),
                txOptions.getFilesystem())
            .getResult();
    DryRunTransactionResult dryRunResult =
        this.blockingDryRunContractTx(
            ContractCallTransactionModel.builder()
                .contractId(contractId)
                .callData(calldata)
                .build(),
            true);
    ObjectResultWrapper objectResultWrapper =
        this.compilerService.blockingDecodeCallResult(
            sourceCode,
            entrypoint,
            dryRunResult.getContractCallObject().getReturnType(),
            dryRunResult.getContractCallObject().getReturnValue(),
            txOptions.getFilesystem());
    handleContractTxError(dryRunResult.getResult(), objectResultWrapper, entrypoint);
    return objectResultWrapper.getResult();
  }

  @Override
  public Object blockingReadOnlyContractCall(
      String contractId, String entrypoint, String sourceCode) {
    return blockingReadOnlyContractCall(
        contractId, entrypoint, sourceCode, ContractTxOptions.builder().build());
  }

  private void handleContractTxError(
      final String returnType,
      final ObjectResultWrapper objectResultWrapper,
      final String entrypoint) {
    if ("ok".equalsIgnoreCase(returnType)) {
      if (objectResultWrapper != null && objectResultWrapper.getResult() instanceof Map) {
        JsonObject resultJSONMap = JsonObject.mapFrom(objectResultWrapper.getResult());
        if (resultJSONMap.containsKey("abort")) {
          throw new AException(
              String.format(
                  "Calling entrypoint %s was aborted due to %s",
                  entrypoint, resultJSONMap.getValue("abort")));
        }
        if (resultJSONMap.containsKey("error")) {
          throw new AException(
              String.format(
                  "An error occured calling entrypoint %s: %s",
                  entrypoint, resultJSONMap.getValue("error")));
        }
      }
    }
  }

  @Override
  public ContractTxResult blockingStatefulContractCall(
      String contractId, String entrypoint, String sourceCode, ContractTxOptions txOptions) {
    if (contractId == null || entrypoint == null || sourceCode == null || txOptions == null) {
      throw new InvalidParameterException("Arguments must not be null.");
    }
    String calldata =
        this.compilerService
            .blockingEncodeCalldata(
                sourceCode,
                entrypoint,
                SophiaTypeTransformer.toCompilerInput(txOptions.getParams()),
                txOptions.getFilesystem())
            .getResult();
    BigInteger nonce;
    nonce =
        txOptions.getNonce() != null
            ? txOptions.getNonce()
            : this.accountService.blockingGetNextNonce();
    ContractCallTransactionModel contractCallModel =
        new ContractCallTransactionModel(
            contractId,
            calldata,
            this.config.getKeyPair().getAddress(),
            txOptions.getAmount(),
            nonce,
            txOptions.getGasLimit(),
            txOptions.getGasPrice(),
            txOptions.getTtl());
    ObjectResultWrapper objectResultWrapper;
    if (this.config.isDryRunStatefulCalls()) {
      DryRunTransactionResult dryRunResult = blockingDryRunContractTx(contractCallModel, false);
      if (!("ok".equals(dryRunResult.getResult())
          && BaseConstants.CONTRACT_EMPTY_RETURN_DATA.equals(
              dryRunResult.getContractCallObject().getReturnValue()))) {
        objectResultWrapper =
            this.compilerService.blockingDecodeCallResult(
                sourceCode,
                entrypoint,
                dryRunResult.getContractCallObject().getReturnType(),
                dryRunResult.getContractCallObject().getReturnValue(),
                txOptions.getFilesystem());
        handleContractTxError(dryRunResult.getResult(), objectResultWrapper, entrypoint);
      }
      _logger.debug("Gas used in dry-run: {}", dryRunResult.getContractCallObject().getGasUsed());
      BigInteger gasLimitWithMargin =
          getGasLimitWithReserveMargin(dryRunResult.getContractCallObject().getGasUsed());
      _logger.debug(
          "Gas with reserve margin of {}: {}",
          this.config.getDryRunGasReserveMargin(),
          gasLimitWithMargin);
      contractCallModel = contractCallModel.toBuilder().gasLimit(gasLimitWithMargin).build();
    }
    PostTransactionResult contractCallPostTxResult =
        this.blockingPostTransaction(contractCallModel);
    if (contractCallPostTxResult == null) {
      throw new RuntimeException("Unexpected error: transaction not broadcasted.");
    }
    TransactionInfoResult contractCallPostTxInfo =
        this.infoService.blockingGetTransactionInfoByHash(contractCallPostTxResult.getTxHash());
    Object decodedValue = null;
    if (!BaseConstants.CONTRACT_EMPTY_RETURN_DATA.equals(
        contractCallPostTxInfo.getCallInfo().getReturnValue())) {
      objectResultWrapper =
          this.compilerService.blockingDecodeCallResult(
              sourceCode,
              entrypoint,
              contractCallPostTxInfo.getCallInfo().getReturnType(),
              contractCallPostTxInfo.getCallInfo().getReturnValue(),
              txOptions.getFilesystem());
      handleContractTxError(
          contractCallPostTxInfo.getCallInfo().getReturnType(), objectResultWrapper, entrypoint);
      decodedValue = objectResultWrapper.getResult();
    }
    return ContractTxResult.builder()
        .txHash(contractCallPostTxResult.getTxHash())
        .callResult(contractCallPostTxInfo.getCallInfo())
        .decodedValue(decodedValue)
        .build();
  }

  @Override
  public ContractTxResult blockingStatefulContractCall(
      String contractId, String entrypoint, String sourceCode) {
    return blockingStatefulContractCall(
        contractId, entrypoint, sourceCode, ContractTxOptions.builder().build());
  }

  private BigInteger getGasLimitWithReserveMargin(BigInteger dryRunGasUsed) {
    return new BigDecimal(dryRunGasUsed)
        .multiply(new BigDecimal(this.config.getDryRunGasReserveMargin()))
        .toBigInteger();
  }

  // get zero address accounts nonce, depending on configured network
  private BigInteger getZeroAddressAccountNonce() {
    if (Arrays.asList(Network.TESTNET, Network.MAINNET).contains(config.getNetwork())) {
      return BaseConstants.ZERO_ADDRESS_ACCOUNT_DEFAULT_NONCE;
    } else {
      return BaseConstants.ZERO_ADDRESS_ACCOUNT_DEVNET_NONCE;
    }
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
        String.format("Technical error creating transaction: %s", e.getMessage()), e);
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
