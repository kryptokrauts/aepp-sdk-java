package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.AccountServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainService;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import javax.naming.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(VertxUnitRunner.class)
public abstract class BaseTest {

  protected static final Logger _logger = LoggerFactory.getLogger("IntegrationTest");

  private static final String AETERNITY_BASE_URL = "AETERNITY_BASE_URL";

  private static final String COMPILER_BASE_URL = "COMPILER_BASE_URL";

  protected static final String BENEFICIARY_PRIVATE_KEY =
      "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF";

  protected KeyPairService keyPairService;

  protected ChainService chainService;

  protected TransactionService transactionServiceNative;

  protected TransactionService transactionServiceDebug;

  protected AccountService accountService;

  protected CompilerService sophiaCompilerService;

  @Rule public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void setupApiClient(TestContext context) throws ConfigurationException {
    Vertx vertx = rule.vertx();
    keyPairService = new KeyPairServiceFactory().getService();
    accountService =
        new AccountServiceFactory()
            .getService(
                ServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .vertx(vertx)
                    .compile());
    chainService =
        new ChainServiceFactory()
            .getService(
                ServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .vertx(vertx)
                    .compile());
    transactionServiceNative =
        new TransactionServiceFactory()
            .getService(
                TransactionServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .network(Network.DEVNET)
                    .vertx(vertx)
                    .compile());
    transactionServiceDebug =
        new TransactionServiceFactory()
            .getService(
                TransactionServiceConfiguration.configure()
                    .nativeMode(false)
                    .baseUrl(getAeternityBaseUrl())
                    .network(Network.DEVNET)
                    .vertx(vertx)
                    .compile());

    sophiaCompilerService =
        new CompilerServiceFactory()
            .getService(
                ServiceConfiguration.configure()
                    .contractBaseUrl(getCompilerBaseUrl())
                    .vertx(vertx)
                    .compile());
  }

  @After
  public void shutdownClient(TestContext context) {
    _logger.info("Closing vertx");
    Vertx vertx = rule.vertx();
    vertx.close();
    keyPairService = null;
    accountService = null;
    chainService = null;
    transactionServiceDebug = null;
    transactionServiceNative = null;
  }

  private static String getAeternityBaseUrl() throws ConfigurationException {
    String aeternityBaseUrl = System.getenv(AETERNITY_BASE_URL);
    if (aeternityBaseUrl == null) {
      throw new ConfigurationException("ENV variable missing: AETERNITY_BASE_URL");
    }
    return aeternityBaseUrl;
  }

  protected static String getCompilerBaseUrl() throws ConfigurationException {
    String compilerBaseUrl = System.getenv(COMPILER_BASE_URL);
    if (compilerBaseUrl == null) {
      throw new ConfigurationException("ENV variable missing: COMPILER_BASE_URL");
    }
    return compilerBaseUrl;
  }

  @BeforeClass
  public static void startup() throws ConfigurationException {
    _logger.info(
        String.format(
            "--------------------------- %s ---------------------------",
            "Using following environment"));
    _logger.info(String.format("%s: %s", AETERNITY_BASE_URL, getAeternityBaseUrl()));
    _logger.info(String.format("%s: %s", COMPILER_BASE_URL, getCompilerBaseUrl()));
    _logger.info(
        "-----------------------------------------------------------------------------------");
  }

  protected Account getAccount(String publicKey) {
    Single<Account> accountSingle = accountService.getAccount(publicKey);
    TestObserver<Account> accountTestObserver = accountSingle.test();
    accountTestObserver.awaitTerminalEvent();
    Account account = accountTestObserver.values().get(0);
    return account;
  }

  protected PostTxResponse postTx(Tx signedTx) {
    Single<PostTxResponse> postTxResponseSingle =
        transactionServiceNative.postTransaction(signedTx);
    TestObserver<PostTxResponse> postTxResponseTestObserver = postTxResponseSingle.test();
    postTxResponseTestObserver.awaitTerminalEvent();
    PostTxResponse postTxResponse = postTxResponseTestObserver.values().get(0);
    return postTxResponse;
  }

  protected void waitForTxInfoObject(TxInfoObject txInfoObject, String txHash)
      throws InterruptedException {
    boolean waiting = true;
    do {
      Single<TxInfoObject> txInfoObjectSingle =
          transactionServiceNative.getTransactionInfoByHash(txHash);
      TestObserver<TxInfoObject> txInfoObjectTestObserver = txInfoObjectSingle.test();
      txInfoObjectTestObserver.awaitTerminalEvent();
      if (txInfoObjectTestObserver.errorCount() > 0) {
        _logger.warn("unable to receive txInfoObject. trying again in 1 second ...");
        Thread.sleep(1000);
      } else {
        txInfoObject = txInfoObjectTestObserver.values().get(0);
        _logger.info("Call contract tx object: " + txInfoObject.toString());
        waiting = false;
      }
    } while (waiting);
  }

  protected Calldata encodeCalldata(
      String contractSourceCode, String contractFunction, List<String> contractFunctionParams) {
    Single<Calldata> callDataSingle =
        this.sophiaCompilerService.encodeCalldata(
            contractSourceCode, contractFunction, contractFunctionParams);
    TestObserver<Calldata> calldataTestObserver = callDataSingle.test();
    calldataTestObserver.awaitTerminalEvent();
    Calldata callData = calldataTestObserver.values().get(0);
    return callData;
  }

  protected JsonObject decodeCalldata(String encodedValue, String sophiaReturnType) {
    // decode the result to json
    Single<SophiaJsonData> sophiaJsonDataSingle =
        this.sophiaCompilerService.decodeCalldata(encodedValue, sophiaReturnType);
    TestObserver<SophiaJsonData> sophiaJsonDataTestObserver = sophiaJsonDataSingle.test();
    sophiaJsonDataTestObserver.awaitTerminalEvent();
    SophiaJsonData sophiaJsonData = sophiaJsonDataTestObserver.values().get(0);
    JsonObject json = JsonObject.mapFrom(sophiaJsonData.getData());
    return json;
  }

  protected DryRunResults performDryRunTransactions(
      List<Map<AccountParameter, Object>> accounts,
      BigInteger block,
      List<UnsignedTx> unsignedTxes) {
    Single<DryRunResults> dryRunResults =
        this.transactionServiceNative.dryRunTransactions(accounts, block, unsignedTxes);
    TestObserver<DryRunResults> dryRunTestObserver = dryRunResults.test();
    dryRunTestObserver.awaitTerminalEvent();
    DryRunResults results = dryRunTestObserver.values().get(0);
    return results;
  }

  protected UnsignedTx createUnsignedContractCallTx(
      String callerId,
      BigInteger nonce,
      String calldata,
      BigInteger gasPrice,
      String contractId,
      BigInteger amount) {
    BigInteger abiVersion = BigInteger.ONE;
    BigInteger ttl = BigInteger.ZERO;
    BigInteger gas = BigInteger.valueOf(1579000);

    AbstractTransaction<?> contractTx =
        transactionServiceNative
            .getTransactionFactory()
            .createContractCallTransaction(
                abiVersion,
                calldata,
                contractId,
                gas,
                gasPrice != null ? gasPrice : BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE),
                nonce,
                callerId,
                ttl);
    if (amount != null) {
      ((ContractCallTransaction) contractTx).setAmount(amount);
    }

    Single<UnsignedTx> unsignedTxSingle =
        transactionServiceNative.createUnsignedTransaction(contractTx);
    TestObserver<UnsignedTx> unsignedTxTestObserver = unsignedTxSingle.test();
    unsignedTxTestObserver.awaitTerminalEvent();
    UnsignedTx unsignedTx = unsignedTxTestObserver.values().get(0);
    return unsignedTx;
  }
}
