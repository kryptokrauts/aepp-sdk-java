package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runners.MethodSorters;
import org.opentest4j.AssertionFailedError;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.model.DryRunResult;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.AccountServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
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

	@Test
	public void decodeRLPArrayTest(TestContext context) {
		try {
			Bytes value = Bytes.fromHexString(TestConstants.binaryTxDevnet);
			RLP.decodeList(value, rlpReader -> {
				Assertions.assertEquals(SerializationTags.OBJECT_TAG_CONTRACT_CREATE_TRANSACTION, rlpReader.readInt());
				Assertions.assertEquals(SerializationTags.VSN, rlpReader.readInt());
				Assertions.assertArrayEquals(rlpReader.readByteArray(),
						EncodingUtils.decodeCheckAndTag(baseKeyPair.getPublicKey(), SerializationTags.ID_TAG_ACCOUNT));
				Assertions.assertEquals(BigInteger.ONE, rlpReader.readBigInteger());
				Assertions.assertArrayEquals(
						EncodingUtils.decodeCheckWithIdentifier(TestConstants.testContractByteCode),
						rlpReader.readByteArray());
				Assertions.assertEquals(BigInteger.valueOf(262145), new BigInteger(rlpReader.readByteArray()));
				Assertions.assertEquals(BigInteger.valueOf(1098660000000000l),
						new BigInteger(rlpReader.readByteArray()));
				Assertions.assertEquals(20000, new BigInteger(rlpReader.readByteArray()).intValue());
				Assertions.assertEquals(0, new BigInteger(rlpReader.readByteArray()).intValue());
				Assertions.assertEquals(0, new BigInteger(rlpReader.readByteArray()).intValue());
				Assertions.assertEquals(1000, new BigInteger(rlpReader.readByteArray()).intValue());
				Assertions.assertEquals(1100000000, new BigInteger(rlpReader.readByteArray()).intValue());
				Assertions.assertArrayEquals(
						EncodingUtils.decodeCheckWithIdentifier(TestConstants.testContractCallData),
						rlpReader.readByteArray());
				return "Validation successful";
			});
		} catch (AssertionFailedError afe) {
			_logger.error("Error decoding RLP array");
			context.fail(afe);
		}
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

		AbstractTransaction<?> contractTx = transactionServiceNative.getTransactionFactory()
				.createContractCreateTransaction(abiVersion, amount, TestConstants.testContractCallData,
						TestConstants.testContractByteCode, deposit, gas, gasPrice, nonce, ownerId, ttl, vmVersion);
		contractTx.setFee(BigInteger.valueOf(1098660000000000l));

		UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();

		Single<UnsignedTx> unsignedTxDebugSingle = transactionServiceDebug.createUnsignedTransaction(contractTx);
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

				UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(contractTx)
						.blockingGet();
				_logger.info("CreateContractTx hash (native unsigned): " + unsignedTxNative.getTx());

				UnsignedTx unsignedTxDebug = callMethodAndGetResult(
						() -> transactionServiceDebug.createUnsignedTransaction(contractTx), UnsignedTx.class);
				_logger.debug("CreateContractTx hash (debug unsigned): " + unsignedTxDebug.getTx());

				context.assertEquals(unsignedTxNative.getTx(), unsignedTxDebug.getTx());
			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	@Test
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
							.createUnsignedTransaction(contractAfterDryRunTx).blockingGet();

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
		String callerId = baseKeyPair.getPublicKey();
		BigInteger abiVersion = BigInteger.ONE;
		BigInteger ttl = BigInteger.ZERO;
		BigInteger gas = BigInteger.valueOf(1579000);

		AbstractTransaction<?> contractTx = transactionServiceNative.getTransactionFactory()
				.createContractCallTransaction(abiVersion, calldata, localDeployedContractId, gas,
						gasPrice != null ? gasPrice : BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE), nonce,
						callerId, ttl);

		UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();
		return unsignedTxNative;
	}

	@Test
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

				UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(contractTx)
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

				UnsignedTx unsignedTxNative = testnetTransactionService.createUnsignedTransaction(contractTx)
						.blockingGet();

				Tx signedTxNative = testnetTransactionService.signTransaction(unsignedTxNative,
						baseKeyPair.getPrivateKey());

				Single<PostTxResponse> postTxResponseSingle = testnetTransactionService.postTransaction(signedTxNative);
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
