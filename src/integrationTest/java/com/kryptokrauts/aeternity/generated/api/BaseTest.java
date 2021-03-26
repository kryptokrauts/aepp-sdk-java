package com.kryptokrauts.aeternity.generated.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.Account;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
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

  /** we wait max. for 5 Minutes to complete each testcase */
  protected static final long TEST_CASE_TIMEOUT_MILLIS = 300000L;

  protected static final BigInteger ONE = BigInteger.ONE;

  protected static final BigInteger ZERO = BigInteger.ZERO;

  protected static final Logger _logger =
      LoggerFactory.getLogger("com.kryptokrauts.IntegrationTest");

  private static final String AETERNITY_BASE_URL = "AETERNITY_BASE_URL";

  private static final String COMPILER_BASE_URL = "COMPILER_BASE_URL";

  private static final String AETERNAL_BASE_URL = "AETERNAL_BASE_URL";

  protected static final VirtualMachine targetVM = VirtualMachine.FATE;

  protected KeyPairService keyPairService;

  protected AeternityService aeternityServiceNative;

  protected AeternityService aeternityServiceDebug;

  protected ObjectMapper objectMapper = new ObjectMapper();

  Account baseAccount;

  @Rule
  public RunTestOnContext rule =
      new RunTestOnContext(
          new VertxOptions()
              .setMaxWorkerExecuteTime(TEST_CASE_TIMEOUT_MILLIS)
              .setMaxEventLoopExecuteTime(TEST_CASE_TIMEOUT_MILLIS)
              .setBlockedThreadCheckInterval(TEST_CASE_TIMEOUT_MILLIS));

  @Rule public Timeout timeoutRule = Timeout.millis(TEST_CASE_TIMEOUT_MILLIS);

  Vertx vertx;

  @Before
  public void setupApiClient(TestContext context) throws ConfigurationException {
    vertx = rule.vertx();

    keyPairService = new KeyPairServiceFactory().getService();

    baseAccount = keyPairService.generateAccountFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);

    aeternityServiceNative =
        new AeternityServiceFactory()
            .getService(
                AeternityServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .compilerBaseUrl(getCompilerBaseUrl())
                    .aeternalBaseUrl(getAeternalBaseUrl())
                    .network(Network.DEVNET)
                    .nativeMode(true)
                    .baseAccount(baseAccount)
                    .vertx(vertx)
                    .targetVM(targetVM)
                    .millisBetweenTrailsToWaitForConfirmation(500l)
                    .compile());
    aeternityServiceDebug =
        new AeternityServiceFactory()
            .getService(
                AeternityServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .compilerBaseUrl(getCompilerBaseUrl())
                    .aeternalBaseUrl(getAeternalBaseUrl())
                    .network(Network.DEVNET)
                    .nativeMode(false)
                    .baseAccount(baseAccount)
                    .vertx(vertx)
                    .targetVM(targetVM)
                    .compile());
  }

  @After
  public void shutdownClient(TestContext context) {
    _logger.info("Closing vertx");
    vertx.close();
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

  protected static String getAeternalBaseUrl() throws ConfigurationException {
    String aeternalBaseUrl = System.getenv(AETERNAL_BASE_URL);
    if (aeternalBaseUrl == null) {
      throw new ConfigurationException("ENV variable missing: AETERNAL_BASE_URL");
    }
    return aeternalBaseUrl;
  }

  @BeforeClass
  public static void startup() throws ConfigurationException {
    _logger.info(
        String.format(
            "--------------------------- %s ---------------------------",
            "Using following environment"));
    _logger.info(String.format("%s: %s", AETERNITY_BASE_URL, getAeternityBaseUrl()));
    _logger.info(String.format("%s: %s", COMPILER_BASE_URL, getCompilerBaseUrl()));
    _logger.info(String.format("%s: %s", AETERNAL_BASE_URL, getAeternalBaseUrl()));
    _logger.info(
        "-----------------------------------------------------------------------------------");
  }

  protected BigInteger getNextBaseKeypairNonce() {
    return getAccount(this.baseAccount.getAddress()).getNonce().add(ONE);
  }

  protected AccountResult getAccount(String publicKey) {
    return aeternityServiceNative.accounts.blockingGetAccount(Optional.ofNullable(publicKey));
  }

  protected TransactionInfoResult waitForTxInfoObject(String txHash) throws Throwable {
    return callMethodAndGetResult(
        () -> aeternityServiceNative.info.asyncGetTransactionInfoByHash(txHash),
        TransactionInfoResult.class);
  }

  protected PostTransactionResult blockingPostTx(
      AbstractTransactionModel<?> tx, Optional<String> privateKey) throws Throwable {
    PostTransactionResult postTxResponse =
        this.aeternityServiceNative.transactions.blockingPostTransaction(
            tx, privateKey.orElse(this.baseAccount.getPrivateKey()));
    _logger.info("PostTx hash: " + postTxResponse.getTxHash());
    TransactionResult txValue = waitForTxMined(postTxResponse.getTxHash());
    _logger.info(
        String.format(
            "Transaction of type %s is mined at block %s with height %s",
            txValue.getTxType(), txValue.getBlockHash(), txValue.getBlockHeight()));

    return postTxResponse;
  }

  protected PostTransactionResult postTx(AbstractTransactionModel<?> tx) throws Throwable {
    PostTransactionResult postTxResponse =
        callMethodAndGetResult(
            () -> this.aeternityServiceNative.transactions.asyncPostTransaction(tx),
            PostTransactionResult.class);
    _logger.info("PostTx hash: " + postTxResponse.getTxHash());
    TransactionResult txValue = waitForTxMined(postTxResponse.getTxHash());
    _logger.info(
        String.format(
            "Transaction of type %s is mined at block %s with height %s",
            txValue.getTxType(), txValue.getBlockHash(), txValue.getBlockHeight()));

    return postTxResponse;
  }

  protected TransactionResult waitForTxMined(String txHash) throws Throwable {
    int blockHeight = -1;
    TransactionResult minedTx = null;
    int doneTrials = 1;

    while (blockHeight == -1 && doneTrials < TestConstants.NUM_TRIALS_DEFAULT) {
      minedTx =
          callMethodAndGetResult(
              () -> aeternityServiceNative.info.asyncGetTransactionByHash(txHash),
              TransactionResult.class);
      if (minedTx.getBlockHeight().intValue() > 1) {
        _logger.debug("Mined tx: " + minedTx);
        blockHeight = minedTx.getBlockHeight().intValue();
      } else {
        _logger.warn(
            String.format(
                "Transaction not mined yet, trying again in 1 second (%s of %s)...",
                doneTrials, TestConstants.NUM_TRIALS_DEFAULT));
        Thread.sleep(1000);
        doneTrials++;
      }
    }

    if (blockHeight == -1) {
      throw new InterruptedException(
          String.format(
              "Transaction %s was not mined after %s trials, aborting", txHash, doneTrials));
    }

    return minedTx;
  }

  protected void waitForBlockHeight(BigInteger blockHeight, Long timeoutMilli) throws Throwable {
    BigInteger currentBlockHeight = BigInteger.ZERO;
    _logger.info(
        "waiting for blockHeight {} and checking every {} seconds",
        blockHeight,
        timeoutMilli / 1000d);
    while (currentBlockHeight.compareTo(blockHeight) == -1) {
      currentBlockHeight = aeternityServiceNative.info.blockingGetCurrentKeyBlock().getHeight();
      _logger.info("current blockHeight: {}", currentBlockHeight);
      Thread.sleep(timeoutMilli);
    }
  }

  protected String encodeCalldata(
      String contractSourceCode, String contractFunction, List<String> contractFunctionParams) {
    return this.aeternityServiceNative
        .compiler
        .blockingEncodeCalldata(contractSourceCode, contractFunction, contractFunctionParams)
        .getResult();
  }

  protected JsonObject decodeCalldata(String encodedValue, String sophiaReturnType) {
    return JsonObject.mapFrom(
        this.aeternityServiceNative.compiler.blockingDecodeCalldata(
            encodedValue, sophiaReturnType));
  }

  protected ObjectResultWrapper decodeCallResult(
      String source, String function, String callResult, String callValue) {
    return this.aeternityServiceNative.compiler.blockingDecodeCallResult(
        source, function, callResult, callValue);
  }

  protected <T> T callMethodAndAwaitException(
      Supplier<Single<T>> observerMethod, Class<T> exception) throws Throwable {
    return callMethodAndGetResult(
        TestConstants.NUM_TRIALS_DEFAULT, observerMethod, exception, true);
  }

  protected <T> T callMethodAndGetResult(Supplier<Single<T>> observerMethod, Class<T> type)
      throws Throwable {
    return callMethodAndGetResult(TestConstants.NUM_TRIALS_DEFAULT, observerMethod, type, false);
  }

  protected <T> T callMethodAndGetResult(
      Integer numTrials, Supplier<Single<T>> observerMethod, Class<T> type, boolean awaitException)
      throws Throwable {

    if (numTrials == null) {
      numTrials = TestConstants.NUM_TRIALS_DEFAULT;
    }

    int doneTrials = 1;
    T result = null;

    do {
      Single<T> resultSingle = observerMethod.get();
      TestObserver<T> singleTestObserver = resultSingle.test();
      singleTestObserver.awaitTerminalEvent();
      if (singleTestObserver.errorCount() > 0) {
        if (awaitException) {
          throw singleTestObserver.errors().get(0);
        }
        if (doneTrials == numTrials) {
          _logger.error("Following error(s) occured while waiting for result of call, aborting");
          for (Throwable error : singleTestObserver.errors()) {
            _logger.error(error.toString());
          }
          throw new InterruptedException("Max number of function call trials exceeded, aborting");
        }
        _logger.warn(
            String.format(
                "Unable to receive object of type %s, trying again in 1 second (%s of %s)...",
                type.getSimpleName(), doneTrials, numTrials));
        Thread.sleep(1000);
        doneTrials++;
      } else {
        if (!awaitException) {
          result = singleTestObserver.values().get(0);
        } else {
          _logger.warn(
              String.format(
                  "Waiting for exception, trying again in 1 second (%s of %s)...",
                  doneTrials, numTrials));
          Thread.sleep(1000);
          doneTrials++;
        }
      }
    } while (result == null);

    return result;
  }

  protected void executeTest(TestContext context, Consumer<?> method) {
    Async async = context.async();
    vertx.executeBlocking(
        future -> {
          try {
            method.accept(null);
            future.complete();
          } catch (Throwable e) {
            _logger.error("Error occured in test", e);
            context.fail();
          }
        },
        result -> {
          if (result.succeeded()) {
            async.complete();
          } else {
            context.fail(result.cause());
          }
        });
  }
}
