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
import java.util.stream.Collectors;
import javax.naming.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;

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

  private static final List<String> testPrivateKeys =
      List.of(
          "7c6e602a94f30e4ea7edabe4376314f69ba7eaa2f355ecedb339df847b6f0d80575f81ffb0a297b7725dc671da0b1769b1fc5cbe45385c7b5ad1fc2eaf1d609d",
          "7fa7934d142c8c1c944e1585ec700f671cbc71fb035dc9e54ee4fb880edfe8d974f58feba752ae0426ecbee3a31414d8e6b3335d64ec416f3e574e106c7e5412",
          "1509d7d0e113528528b7ce4bf72c3a027bcc98656e46ceafcfa63e56597ec0d8206ff07f99ea517b7a028da8884fb399a2e3f85792fe418966991ba09b192c91",
          "58bd39ded1e3907f0b9c1fbaa4456493519995d524d168e0b04e86400f4aa13937bcec56026494dcf9b19061559255d78deea3281ac649ca307ead34346fa621",
          "50458d629ae7109a98e098c51c29ec39c9aea9444526692b1924660b5e2309c7c55aeddd5ebddbd4c6970e91f56e8aaa04eb52a1224c6c783196802e136b9459",
          "707881878eacacce4db463de9c7bf858b95c3144d52fafed4a41ffd666597d0393d23cf31fcd12324cd45d4784d08953e8df8283d129f357463e6795b40e88aa",
          "9262701814da8149615d025377e2a08b5f10a6d33d1acaf2f5e703e87fe19c83569ecc7803d297fde01758f1bdc9e0c2eb666865284dff8fa39edb2267de70db",
          "e15908673cda8a171ea31333538437460d9ca1d8ba2e61c31a9a3d01a8158c398a14cd12266e480f85cc1dc3239ed5cfa99f3d6955082446bebfe961449dc48b",
          "6eb127925aa10d6d468630a0ca28ff5e1b8ad00db151fdcc4878362514d6ae865951b78cf5ef047cab42218e0d5a4020ad34821ca043c0f1febd27aaa87d5ed7",
          "36595b50bf097cd19423c40ee66b117ed15fc5ec03d8676796bdf32bc8fe367d82517293a0f82362eb4f93d0de77af5724fba64cbcf55542328bc173dbe13d33");

  protected static final VirtualMachine targetVM = VirtualMachine.FATE;

  protected KeyPairService keyPairService;

  protected AeternityService aeternityService;

  protected KeyPair keyPair;

  protected List<KeyPair> otherKeyPairs;

  protected static String paymentSplitterSource,
      ethereumSignaturesSource,
      chatBotSource,
      gaBlindAuthSource,
      sophiaTypesSource,
      ecdsaAuthSource,
      statefulTest;

  protected UnitConversionService unitConversionService = new DefaultUnitConversionServiceImpl();

  protected static Credentials credentials =
      Credentials.create("9a4a5c038e7ce00f0ad216894afc00de6b41bbca1d4d7742104cb9f078c6d2df");
  protected static String ethereumAddress = "0xe53e2125f377d5c62a1ffbfeeb89a0826e9de54c";

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
  public void setupTestEnv(TestContext context) throws ConfigurationException {
    vertx = rule.vertx();

    keyPairService = new KeyPairServiceFactory().getService();

    keyPair = keyPairService.recoverKeyPair(TestConstants.BENEFICIARY_PRIVATE_KEY);

    otherKeyPairs =
        testPrivateKeys.stream()
            .map(key -> keyPairService.recoverKeyPair(key))
            .collect(Collectors.toList());

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
  public void shutdownTestEnv(TestContext context) {
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
    sophiaTypesSource = getContractSourceCode("SophiaTypes.aes");
    ecdsaAuthSource = getContractSourceCode("ECDSAAuth.aes");
    statefulTest = getContractSourceCode("StatefulTest.aes");
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

  protected byte[] web3jSignMessage(byte[] hashedMessage, ECKeyPair keyPair) {
    SignatureData signatureData = Sign.signMessage(hashedMessage, keyPair, false);
    byte[] signature = new byte[65];

    // note FATE/Sophia requires the recovery identifier "V" to be placed as first byte in the
    // signature in order to recover correctly via "ecrecover_secp256k1"
    System.arraycopy(signatureData.getV(), 0, signature, 0, 1);
    System.arraycopy(signatureData.getR(), 0, signature, 1, 32);
    System.arraycopy(signatureData.getS(), 0, signature, 33, 32);
    return signature;
  }

  protected byte[] web3jKeccak256(String msg, boolean ethereumPrefix) {
    byte[] hashedMessage;
    if (ethereumPrefix) {
      hashedMessage = Sign.getEthereumMessageHash(msg.getBytes(StandardCharsets.UTF_8));
    } else {
      hashedMessage = Hash.sha3(msg.getBytes(StandardCharsets.UTF_8));
    }
    return hashedMessage;
  }
}
