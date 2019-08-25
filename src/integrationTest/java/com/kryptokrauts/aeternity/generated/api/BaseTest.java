package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.naming.ConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.aens.NameService;
import com.kryptokrauts.aeternity.sdk.service.aens.NameServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainService;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.domain.account.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.oracle.OracleService;
import com.kryptokrauts.aeternity.sdk.service.oracle.OracleServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public abstract class BaseTest {

	protected static final long TEST_CASE_TIMEOUT_MILLIS = 5000l;

	protected static final Logger _logger = LoggerFactory.getLogger("com.kryptokrauts.IntegrationTest");

	private static final String AETERNITY_BASE_URL = "AETERNITY_BASE_URL";

	private static final String COMPILER_BASE_URL = "COMPILER_BASE_URL";

	protected KeyPairService keyPairService;

	protected ChainService chainService;

	protected TransactionService transactionServiceNative;

	protected TransactionService transactionServiceDebug;

	protected AccountService accountService;

//	protected CompilerService sophiaCompilerService;

	protected NameService nameService;

	protected OracleService oracleService;

	protected AeternityService aeternityServiceNative;

	protected AeternityService aeternityServiceDebug;

	BaseKeyPair baseKeyPair;

	@Rule
	public RunTestOnContext rule = new RunTestOnContext();

	@Before
	public void setupApiClient(TestContext context) throws ConfigurationException {
		Vertx vertx = rule.vertx();
		keyPairService = new KeyPairServiceFactory().getService();
//		accountService = null;// new AccountServiceFactory()
//				.getService(ServiceConfiguration.configure().baseUrl(getAeternityBaseUrl()).vertx(vertx).compile());
		chainService = new ChainServiceFactory()
				.getService(ServiceConfiguration.configure().baseUrl(getAeternityBaseUrl()).vertx(vertx).compile());
		transactionServiceNative = null;// new TransactionServiceFactory().getService(TransactionServiceConfiguration
//				.configure().baseUrl(getAeternityBaseUrl()).network(Network.DEVNET).vertx(vertx).compile());
		transactionServiceDebug = null;// new
										// TransactionServiceFactory().getService(TransactionServiceConfiguration.configure()
//				.nativeMode(false).baseUrl(getAeternityBaseUrl()).network(Network.DEVNET).vertx(vertx).compile());

//		sophiaCompilerService = new CompilerServiceFactory().getService(
//				ServiceConfiguration.configure().contractBaseUrl(getCompilerBaseUrl()).vertx(vertx).compile());

		nameService = new NameServiceFactory()
				.getService(ServiceConfiguration.configure().baseUrl(getAeternityBaseUrl()).vertx(vertx).compile());

		oracleService = new OracleServiceFactory()
				.getService(ServiceConfiguration.configure().baseUrl(getAeternityBaseUrl()).vertx(vertx).compile());

		baseKeyPair = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);

		aeternityServiceNative = new AeternityServiceFactory().getService(AeternityServiceConfiguration.configure()
				.baseUrl(getAeternityBaseUrl()).contractBaseUrl(getCompilerBaseUrl()).network(Network.DEVNET)
				.nativeMode(true).baseKeyPair(baseKeyPair).compile());
		aeternityServiceDebug = new AeternityServiceFactory().getService(AeternityServiceConfiguration.configure()
				.baseUrl(getAeternityBaseUrl()).contractBaseUrl(getCompilerBaseUrl()).network(Network.DEVNET)
				.nativeMode(false).baseKeyPair(baseKeyPair).compile());
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
//		sophiaCompilerService = null;
		oracleService = null;
		nameService = null;
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
		_logger.info(String.format("--------------------------- %s ---------------------------",
				"Using following environment"));
		_logger.info(String.format("%s: %s", AETERNITY_BASE_URL, getAeternityBaseUrl()));
		_logger.info(String.format("%s: %s", COMPILER_BASE_URL, getCompilerBaseUrl()));
		_logger.info("-----------------------------------------------------------------------------------");
	}

	protected AccountResult getAccount(String publicKey, TestContext context) {
		return aeternityServiceNative.accounts.blockingGetAccount(Optional.ofNullable(publicKey));
	}

	protected TxInfoObject waitForTxInfoObject(String txHash) throws Throwable {
		return callMethodAndGetResult(() -> aeternityServiceNative.info.getTransactionInfoByHash(txHash),
				TxInfoObject.class);
	}

	protected GenericSignedTx waitForTxMined(String txHash) throws Throwable {
		int blockHeight = -1;
		GenericSignedTx minedTx = null;
		int doneTrials = 1;

		while (blockHeight == -1 && doneTrials < TestConstants.NUM_TRIALS_DEFAULT) {
			minedTx = callMethodAndGetResult(() -> aeternityServiceNative.info.getTransactionByHash(txHash),
					GenericSignedTx.class);
			if (minedTx.getBlockHeight().intValue() > 1) {
				_logger.debug("Mined tx: " + minedTx);
				blockHeight = minedTx.getBlockHeight().intValue();
			} else {
				_logger.warn(String.format("Transaction not mined yet, trying again in 1 second (%s of %s)...",
						doneTrials, TestConstants.NUM_TRIALS_DEFAULT));
				Thread.sleep(1000);
				doneTrials++;
			}
		}

		if (blockHeight == -1) {
			throw new InterruptedException(
					String.format("Transaction %s was not mined after %s trials, aborting", txHash, doneTrials));
		}

		return minedTx;
	}

	protected String encodeCalldata(String contractSourceCode, String contractFunction,
			List<String> contractFunctionParams) {
		return this.aeternityServiceNative.compiler.blockingEncodeCalldata(contractSourceCode, contractFunction,
				contractFunctionParams);
	}

	protected JsonObject decodeCalldata(String encodedValue, String sophiaReturnType)  {
		return JsonObject
				.mapFrom(this.aeternityServiceNative.compiler.blockingDecodeCalldata(encodedValue, sophiaReturnType));
	}

	protected DryRunResults performDryRunTransactions(List<Map<AccountParameter, Object>> accounts, BigInteger block,
			List<String> unsignedTxes) {

		return this.aeternityServiceNative.transactions.blockingDryRunTransactions(accounts, block, unsignedTxes);
	}

	/**
	 * @TODO FIX
	 * 
	 */

	protected <T> T callMethodAndAwaitException(Supplier<Single<T>> observerMethod, Class<T> exception)
			throws Throwable {
		return callMethodAndGetResult(TestConstants.NUM_TRIALS_DEFAULT, observerMethod, exception, true);
	}

	protected <T> T callMethodAndGetResult(Supplier<Single<T>> observerMethod, Class<T> type) throws Throwable {
		return callMethodAndGetResult(TestConstants.NUM_TRIALS_DEFAULT, observerMethod, type, false);
	}

	protected <T> T callMethodAndGetResult(Integer numTrials, Supplier<Single<T>> observerMethod, Class<T> type,
			boolean awaitException) throws Throwable {

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
						String.format("Unable to receive object of type %s, trying again in 1 second (%s of %s)...",
								type.getSimpleName(), doneTrials, numTrials));
				Thread.sleep(1000);
				doneTrials++;
			} else {
				if (!awaitException) {
					result = singleTestObserver.values().get(0);
				} else {
					_logger.warn(String.format("Waiting for exception, trying again in 1 second (%s of %s)...",
							doneTrials, numTrials));
					Thread.sleep(1000);
					doneTrials++;
				}
			}
		} while (result == null);

		return result;
	}
}
