package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.account.domain.NextNonceStrategy;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.unit.UnitConversionService;
import com.kryptokrauts.aeternity.sdk.service.unit.impl.DefaultUnitConversionServiceImpl;
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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.naming.ConfigurationException;
import org.apache.commons.io.IOUtils;
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

  private static final String MDW_BASE_URL = "MDW_BASE_URL";

  protected static final VirtualMachine targetVM = VirtualMachine.FATE;

  protected KeyPairService keyPairService;

  protected AeternityService aeternityService;

  protected KeyPair keyPair;

  protected static String paymentSplitterSource,
      ethereumSignaturesSource,
      chatBotSource,
      gaBlindAuthSource;

  protected UnitConversionService unitConversionService = new DefaultUnitConversionServiceImpl();

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

    keyPair = keyPairService.recoverKeyPair(TestConstants.BENEFICIARY_PRIVATE_KEY);

    aeternityService =
        new AeternityServiceFactory()
            .getService(
                AeternityServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .debugBaseUrl(getAeternityBaseUrl())
                    .compilerBaseUrl(getCompilerBaseUrl())
                    .mdwBaseUrl(getMdwBaseUrl())
                    .network(Network.DEVNET)
                    .nativeMode(true)
                    .keyPair(keyPair)
                    .vertx(vertx)
                    .targetVM(targetVM)
                    .millisBetweenTrailsToWaitForConfirmation(500l)
                    .compile());
  }

  @After
  public void shutdownClient(TestContext context) {
    _logger.info("Closing vertx");
    vertx.close();
  }

  protected static String getAeternityBaseUrl() throws ConfigurationException {
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

  protected static String getMdwBaseUrl() throws ConfigurationException {
    String mdwBaseUrl = System.getenv(MDW_BASE_URL);
    if (mdwBaseUrl == null) {
      throw new ConfigurationException("ENV variable missing: MDW_BASE_URL");
    }
    return mdwBaseUrl;
  }

  @BeforeClass
  public static void startup() throws ConfigurationException, IOException {
    _logger.info(
        String.format(
            "--------------------------- %s ---------------------------",
            "Using following environment"));
    _logger.info(String.format("%s: %s", AETERNITY_BASE_URL, getAeternityBaseUrl()));
    _logger.info(String.format("%s: %s", COMPILER_BASE_URL, getCompilerBaseUrl()));
    _logger.info(String.format("%s: %s", MDW_BASE_URL, getMdwBaseUrl()));
    _logger.info(
        "-----------------------------------------------------------------------------------");
    paymentSplitterSource = getContractSourceCode("PaymentSplitter.aes");
    ethereumSignaturesSource = getContractSourceCode("EthereumSignatures.aes");
    chatBotSource = getContractSourceCode("ChatBot.aes");
    gaBlindAuthSource = getContractSourceCode("GaBlindAuth.aes");
  }

  protected BigInteger getNextKeypairNonce() {
    return getNextKeypairNonce(NextNonceStrategy.MAX);
  }

  protected BigInteger getNextKeypairNonce(NextNonceStrategy nextNonceStrategy) {
    return aeternityService.accounts.blockingGetNextNonce(nextNonceStrategy);
  }

  protected AccountResult getAccount(String publicKey) {
    if (publicKey == null) {
      return aeternityService.accounts.blockingGetAccount();
    }
    return aeternityService.accounts.blockingGetAccount(publicKey);
  }

  protected TransactionInfoResult waitForTxInfoObject(String txHash) throws Throwable {
    return callMethodAndGetResult(
        () -> aeternityService.info.asyncGetTransactionInfoByHash(txHash),
        TransactionInfoResult.class);
  }

  protected PostTransactionResult blockingPostTx(AbstractTransactionModel<?> tx) throws Throwable {
    return this.blockingPostTx(tx, null);
  }

  protected PostTransactionResult blockingPostTx(AbstractTransactionModel<?> tx, String privateKey)
      throws Throwable {
    if (privateKey == null) {
      privateKey = this.keyPair.getEncodedPrivateKey();
    }
    PostTransactionResult postTxResponse =
        this.aeternityService.transactions.blockingPostTransaction(tx, privateKey);
    _logger.info("PostTx hash: " + postTxResponse.getTxHash());
    TransactionResult txValue =
        aeternityService.info.blockingGetTransactionByHash(postTxResponse.getTxHash());
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
              () -> aeternityService.info.asyncGetTransactionByHash(txHash),
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
      currentBlockHeight = aeternityService.info.blockingGetCurrentKeyBlock().getHeight();
      _logger.info("current blockHeight: {}", currentBlockHeight);
      Thread.sleep(timeoutMilli);
    }
  }

  protected String encodeCalldata(
      String contractSourceCode,
      String contractFunction,
      List<String> contractFunctionParams,
      Map<String, String> fileSystem) {
    return this.aeternityService
        .compiler
        .blockingEncodeCalldata(
            contractSourceCode, contractFunction, contractFunctionParams, fileSystem)
        .getResult();
  }

  protected JsonObject decodeCalldata(String encodedValue, String sophiaReturnType) {
    return JsonObject.mapFrom(
        this.aeternityService.compiler.blockingDecodeCalldata(encodedValue, sophiaReturnType));
  }

  protected ObjectResultWrapper decodeCallResult(
      String source, String function, String type, String value) {
    return this.aeternityService.compiler.blockingDecodeCallResult(
        source, function, type, value, null);
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

  private static String getContractSourceCode(String filename) throws IOException {
    final InputStream inputStream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("contracts/" + filename);
    return IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
  }
}
