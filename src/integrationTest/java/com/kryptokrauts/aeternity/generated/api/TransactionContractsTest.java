package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.model.DryRunResult;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionContractsTest extends BaseTest {

	BaseKeyPair baseKeyPair;

	static String localDeployedContractId;

	@Before
	public void initBeforeTest() {
		baseKeyPair = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
	}

	/**
	 * create an unsigned native CreateContract transaction
	 *
	 * @param context
	 */
	@Test
	public void buildCreateContractTransactionTest(TestContext context) {
		Async async = context.async();

		String ownerId = baseKeyPair.getPublicKey();
		BigInteger abiVersion = BigInteger.ONE;
		BigInteger vmVersion = BigInteger.valueOf(4);
		BigInteger amount = BigInteger.valueOf(100);
		BigInteger deposit = BigInteger.valueOf(100);
		BigInteger ttl = BigInteger.valueOf(20000l);
		BigInteger gas = BigInteger.valueOf(1000);
		BigInteger gasPrice = BigInteger.valueOf(1100000000l);
		BigInteger nonce = BigInteger.ONE;
		BigInteger fee = BigInteger.valueOf(1098660000000000l);

		ContractCreateTransactionModel contractTx = ContractCreateTransactionModel.builder().abiVersion(abiVersion)
				.amount(amount).callData(TestConstants.testContractCallData)
				.contractByteCode(TestConstants.testContractByteCode).deposit(deposit).fee(fee).gas(gas)
				.gasPrice(gasPrice).nonce(nonce).ownerId(ownerId).ttl(ttl).vmVersion(vmVersion).build();

		UnsignedTx unsignedTxNative = aeternityServiceNative.transactions.asyncCreateUnsignedTransaction(contractTx)
				.blockingGet();

		Single<UnsignedTx> unsignedTxDebugSingle = aeternityServiceDebug.transactions
				.asyncCreateUnsignedTransaction(contractTx);
		unsignedTxDebugSingle.subscribe(it -> {
			context.assertEquals(it.getTx(), unsignedTxNative.getTx());
			async.complete();
		}, throwable -> context.fail(throwable));
	}

	@Test
	public void buildCallContractTransactionTest(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = getAccount(baseKeyPair.getPublicKey());
				String callerId = baseKeyPair.getPublicKey();
				BigInteger abiVersion = BigInteger.ONE;
				BigInteger ttl = BigInteger.valueOf(20000);
				BigInteger gas = BigInteger.valueOf(1000);
				BigInteger gasPrice = BigInteger.valueOf(1000000000);
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);
				String callContractCalldata = TestConstants.encodedServiceCall;

				AbstractTransaction<?> contractTx = transactionServiceNative.getTransactionFactory()
						.createContractCallTransaction(abiVersion, callContractCalldata, localDeployedContractId, gas,
								gasPrice, nonce, callerId, ttl);
				contractTx.setFee(BigInteger.valueOf(1454500000000000l));

				UnsignedTx unsignedTxNative = transactionServiceNative.asyncCreateUnsignedTransaction(contractTx)
						.blockingGet();
				_logger.info("CreateContractTx hash (native unsigned): " + unsignedTxNative.getTx());

				UnsignedTx unsignedTxDebug = callMethodAndGetResult(
						() -> transactionServiceDebug.asyncCreateUnsignedTransaction(contractTx), UnsignedTx.class);
				_logger.debug("CreateContractTx hash (debug unsigned): " + unsignedTxDebug.getTx());

				context.assertEquals(unsignedTxNative.getTx(), unsignedTxDebug.getTx());
			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	@Test
	@Ignore
	public void staticCallContractOnLocalNode(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = getAccount(baseKeyPair.getPublicKey());
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);

				// Compile the call contract
				Calldata calldata = encodeCalldata(TestConstants.testContractSourceCode,
						TestConstants.testContractFunction, TestConstants.testContractFunctionParams);

				DryRunResults results = callMethodAndGetResult(() -> this.transactionServiceNative.dryRunTransactions(
						Arrays.asList(ImmutableMap.of(AccountParameter.PUBLIC_KEY, baseKeyPair.getPublicKey()),
								ImmutableMap.of(AccountParameter.PUBLIC_KEY, baseKeyPair.getPublicKey())),
						null,
						Arrays.asList(createUnsignedContractCallTx(context, nonce, calldata.getCalldata(), null),
								createUnsignedContractCallTx(context, nonce.add(BigInteger.ONE), calldata.getCalldata(),
										null))),
						DryRunResults.class);
				_logger.info(results.toString());
				for (DryRunResult result : results.getResults()) {
					context.assertEquals("ok", result.getResult());
				}

			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	@Test
	@Ignore
	public void staticCallContractFailOnLocalNode(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				// Compile the call contract
				Calldata calldata = encodeCalldata(TestConstants.testContractSourceCode,
						TestConstants.testContractFunction, TestConstants.testContractFunctionParams);

				DryRunResults results = callMethodAndGetResult(() -> this.transactionServiceNative.dryRunTransactions(
						Arrays.asList(ImmutableMap.of(AccountParameter.PUBLIC_KEY, baseKeyPair.getPublicKey())), null,
						Arrays.asList(
								createUnsignedContractCallTx(context, BigInteger.ONE, calldata.getCalldata(), null))),
						DryRunResults.class);
				_logger.info(results.toString());
				for (DryRunResult result : results.getResults()) {
					context.assertEquals("error", result.getResult());
				}

			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	/**
	 * min gas is not sufficient, validate this!
	 *
	 * @param context
	 */
	@Test
	@Ignore
	public void callContractAfterDryRunOnLocalNode(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = getAccount(baseKeyPair.getPublicKey());
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);

				// Compile the call contract
				Calldata calldata = encodeCalldata(TestConstants.testContractSourceCode,
						TestConstants.testContractFunction, TestConstants.testContractFunctionParams);

				DryRunResults results = performDryRunTransactions(
						Arrays.asList(ImmutableMap.of(AccountParameter.PUBLIC_KEY, baseKeyPair.getPublicKey())), null,
						Arrays.asList(createUnsignedContractCallTx(context, nonce, calldata.getCalldata(), null)));
				_logger.info("callContractAfterDryRunOnLocalNode: " + results.toString());
				for (DryRunResult result : results.getResults()) {
					context.assertEquals("ok", result.getResult());

					AbstractTransaction<?> contractAfterDryRunTx = transactionServiceNative.getTransactionFactory()
							.createContractCallTransaction(BigInteger.ONE, calldata.getCalldata(),
									localDeployedContractId, result.getCallObj().getGasUsed(),
									result.getCallObj().getGasPrice(), nonce, baseKeyPair.getPublicKey(),
									BigInteger.ZERO);

					UnsignedTx unsignedTxNative = transactionServiceNative
							.asyncCreateUnsignedTransaction(contractAfterDryRunTx).blockingGet();

					Tx signedTxNative = transactionServiceNative.signTransaction(unsignedTxNative,
							baseKeyPair.getPrivateKey());

					// post the signed contract call tx
					PostTxResponse postTxResponse = postTx(signedTxNative);
					context.assertEquals(postTxResponse.getTxHash(),
							transactionServiceNative.computeTxHash(signedTxNative.getTx()));
					_logger.info("CreateContractTx hash: " + postTxResponse.getTxHash());

					// get the tx info object to resolve the result
					TxInfoObject txInfoObject = waitForTxInfoObject(postTxResponse.getTxHash());

					// decode the result to json
					JsonObject json = decodeCalldata(txInfoObject.getCallInfo().getReturnValue(),
							TestConstants.testContractFunctionSophiaType);
					context.assertEquals(TestConstants.testContractFuntionParam, json.getValue("value").toString());
				}

			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	private UnsignedTx createUnsignedContractCallTx(TestContext context, BigInteger nonce, String calldata,
			BigInteger gasPrice) {
//		String callerId = baseKeyPair.getPublicKey();
//		BigInteger abiVersion = BigInteger.ONE;
//		BigInteger ttl = BigInteger.ZERO;
//		BigInteger gas = BigInteger.valueOf(1579000);
//
//		AbstractTransaction<?> contractTx = transactionServiceNative.getTransactionFactory()
//				.createContractCallTransaction(abiVersion, calldata, localDeployedContractId, gas,
//						gasPrice != null ? gasPrice : BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE), nonce,
//						callerId, ttl);
//
//		UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();
//		return unsignedTxNative;
		return null;
	}

	@Test
	@Ignore
	public void aDeployContractNativeOnLocalNode(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = getAccount(baseKeyPair.getPublicKey());
				String ownerId = baseKeyPair.getPublicKey();
				BigInteger abiVersion = BigInteger.ONE;
				BigInteger vmVersion = BigInteger.valueOf(4);
				BigInteger amount = BigInteger.ZERO;
				BigInteger deposit = BigInteger.ZERO;
				BigInteger ttl = BigInteger.ZERO;
				BigInteger gas = BigInteger.valueOf(1000000);
				BigInteger gasPrice = BigInteger.valueOf(2000000000);
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);

				AbstractTransaction<?> contractTx = transactionServiceNative.getTransactionFactory()
						.createContractCreateTransaction(abiVersion, amount, TestConstants.testContractCallData,
								TestConstants.testContractByteCode, deposit, gas, gasPrice, nonce, ownerId, ttl,
								vmVersion);

				UnsignedTx unsignedTxNative = transactionServiceNative.asyncCreateUnsignedTransaction(contractTx)
						.blockingGet();

				Tx signedTxNative = transactionServiceNative.signTransaction(unsignedTxNative,
						baseKeyPair.getPrivateKey());
				_logger.info("CreateContractTx hash (native signed): " + signedTxNative);

				PostTxResponse postTxResponse = postTx(signedTxNative);
				TxInfoObject txInfoObject = waitForTxInfoObject(postTxResponse.getTxHash());
				localDeployedContractId = txInfoObject.getCallInfo().getContractId();
				_logger.info("Deployed contract - hash " + postTxResponse.getTxHash() + " - " + txInfoObject);

			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	@Test
	@Ignore
	public void callContractOnLocalNodeTest(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = getAccount(baseKeyPair.getPublicKey());
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);

				Single<Calldata> callDataSingle = this.sophiaCompilerService.encodeCalldata(
						TestConstants.testContractSourceCode, TestConstants.testContractFunction,
						TestConstants.testContractFunctionParams);
				TestObserver<Calldata> calldataTestObserver = callDataSingle.test();
				calldataTestObserver.awaitTerminalEvent();
				Calldata callData = calldataTestObserver.values().get(0);

				UnsignedTx unsignedTxNative = this.createUnsignedContractCallTx(context, nonce, callData.getCalldata(),
						null);
				Tx signedTxNative = transactionServiceNative.signTransaction(unsignedTxNative,
						baseKeyPair.getPrivateKey());

				// post the signed contract call tx
				PostTxResponse postTxResponse = postTx(signedTxNative);
				context.assertEquals(postTxResponse.getTxHash(),
						transactionServiceNative.computeTxHash(signedTxNative.getTx()));
				_logger.info("CreateContractTx hash: " + postTxResponse.getTxHash());

				// get the tx info object to resolve the result
				TxInfoObject txInfoObject = waitForTxInfoObject(postTxResponse.getTxHash());

				// decode the result to json
				JsonObject json = decodeCalldata(txInfoObject.getCallInfo().getReturnValue(),
						TestConstants.testContractFunctionSophiaType);
				context.assertEquals(TestConstants.testContractFuntionParam, json.getValue("value").toString());
			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	@Test
	@Ignore // specific testcase we don't want to run each time
	public void deployContractNativeOnTestNetworkTest(TestContext context) {
		Async async = context.async();

		baseKeyPair = keyPairService.generateBaseKeyPairFromSecret(TestConstants.testnetAccountPrivateKey);

		AccountService testnetAccountService = new AccountServiceFactory().getService();
		TransactionService testnetTransactionService = new TransactionServiceFactory()
				.getService(TransactionServiceConfiguration.configure().baseUrl(TestConstants.testnetURL)
						.network(Network.TESTNET).vertx(rule.vertx()).compile());

		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = getAccount(baseKeyPair.getPublicKey());
				String ownerId = baseKeyPair.getPublicKey();
				BigInteger abiVersion = BigInteger.ONE;
				BigInteger vmVersion = BigInteger.valueOf(4);
				BigInteger amount = BigInteger.ZERO;
				BigInteger deposit = BigInteger.ZERO;
				BigInteger ttl = BigInteger.ZERO;
				BigInteger gas = BigInteger.valueOf(1000);
				BigInteger gasPrice = BigInteger.valueOf(1100000000);
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);

				AbstractTransaction<?> contractTx = testnetTransactionService.getTransactionFactory()
						.createContractCreateTransaction(abiVersion, amount, TestConstants.testContractCallData,
								TestConstants.testContractByteCode, deposit, gas, gasPrice, nonce, ownerId, ttl,
								vmVersion);

				UnsignedTx unsignedTxNative = testnetTransactionService.asyncCreateUnsignedTransaction(contractTx)
						.blockingGet();

				Tx signedTxNative = testnetTransactionService.signTransaction(unsignedTxNative,
						baseKeyPair.getPrivateKey());

				Single<PostTxResponse> postTxResponseSingle = testnetTransactionService.asyncPostTransaction(signedTxNative);
				TestObserver<PostTxResponse> postTxResponseTestObserver = postTxResponseSingle.test();
				postTxResponseTestObserver.awaitTerminalEvent();
				PostTxResponse postTxResponse = postTxResponseTestObserver.values().get(0);
				context.assertEquals(postTxResponse.getTxHash(),
						testnetTransactionService.computeTxHash(signedTxNative.getTx()));
			} catch (Exception e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}
}
