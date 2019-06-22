package com.kryptokrauts.aeternity.sdk.service.transaction.impl;

import com.kryptokrauts.aeternity.generated.api.ChannelApiImpl;
import com.kryptokrauts.aeternity.generated.api.ContractApiImpl;
import com.kryptokrauts.aeternity.generated.api.DebugApiImpl;
import com.kryptokrauts.aeternity.generated.api.TransactionApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.DebugApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.model.DryRunAccount;
import com.kryptokrauts.aeternity.generated.model.DryRunInput;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.TransactionFactory;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.DefaultApiImpl;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import io.reactivex.Single;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;
import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  protected static final Logger _logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

  @Nonnull private TransactionServiceConfiguration config;

  private TransactionApi transactionApi;

  private ChannelApi channelApi;

  private ContractApi contractApi;

  private DefaultApi compilerApi;

  private DebugApi debugApi;

  private TransactionFactory transactionFactory;

  @Override
  public TransactionFactory getTransactionFactory() {
    if (transactionFactory == null) {
      transactionFactory =
          new TransactionFactory(
              getTransactionApi(),
              getChannelApi(),
              getContractApi(),
              getCompilerApi(),
              getDebugApi());
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
  public Single<TxInfoObject> getTransactionInfoByHash(String txHash) {
    return getTransactionApi().rxGetTransactionInfoByHash(txHash);
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

  @Override
  public Single<DryRunResults> dryRunTransactions(
      @NonNull List<Map<AccountParameter, Object>> accounts,
      BigInteger block,
      @NonNull List<UnsignedTx> unsignedTransactions) {
    DryRunInput body = new DryRunInput();
    // Validate parameters
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(accounts.size() > 0),
        accounts,
        "dryRunTransactions",
        Arrays.asList("accounts"),
        ValidationUtil.NO_ENTRIES);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(unsignedTransactions.size() > 0),
        accounts,
        "dryRunTransactions",
        Arrays.asList("unsignedTransactions"),
        ValidationUtil.NO_ENTRIES);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(unsignedTransactions.size() == accounts.size()),
        accounts,
        "dryRunTransactions",
        Arrays.asList("unsignedTransactions", "accounts"),
        ValidationUtil.LIST_NOT_SAME_SIZE);

    List<DryRunAccount> dryRunAccounts = new LinkedList<DryRunAccount>();

    for (Map<AccountParameter, Object> txParams : accounts) {
      DryRunAccount currAccount = new DryRunAccount();
      ValidationUtil.checkParameters(
          validate -> Optional.ofNullable(txParams.size() > 0),
          accounts,
          "dryRunTransactions",
          Arrays.asList("accounts.map"),
          ValidationUtil.NO_ENTRIES);
      ValidationUtil.checkParameters(
          validate -> Optional.ofNullable(txParams.containsKey(AccountParameter.PUBLIC_KEY)),
          accounts,
          "dryRunTransactions",
          Arrays.asList("accounts.map.values"),
          ValidationUtil.MAP_MISSING_VALUE,
          AccountParameter.PUBLIC_KEY);

      currAccount.setPubKey(txParams.get(AccountParameter.PUBLIC_KEY).toString());
      BigInteger amount =
          txParams.get(AccountParameter.AMOUNT) != null
              ? new BigInteger(txParams.get(AccountParameter.AMOUNT).toString())
              : BigInteger.ZERO;
      currAccount.setAmount(amount);

      dryRunAccounts.add(currAccount);
    }

    body.setAccounts(dryRunAccounts);
    if (block != null) {
      body.setTop(block.toString());
    } else {
      body.top(null);
    }
    unsignedTransactions.forEach(item -> body.addTxsItem(item.getTx()));

    _logger.debug(String.format("Calling dry run on block %s with body %s", block, body));
    return this.debugApi.rxDryRunTxs(body);
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

  private DebugApi getDebugApi() {
    if (debugApi == null) {
      debugApi = new DebugApi(new DebugApiImpl(config.getApiClient()));
    }
    return debugApi;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return this.config.getNetwork().getId() + " " + this.config.isNativeMode();
  }
}
