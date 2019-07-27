package com.kryptokrauts.aeternity.generated.api;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.model.DryRunResult;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import com.kryptokrauts.sophia.compiler.generated.model.ByteCode;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentSplitterContractTest extends BaseTest {

	static String paymentSplitterSource;
	static String localDeployedContractId;

	static BaseKeyPair owner;
	static BaseKeyPair initialReceiver1;
	static BaseKeyPair initialReceiver2;
	static BaseKeyPair initialReceiver3;

	static Map<String, Integer> initialWeights = new HashMap<>();

	@Test
	public void a_a_init(TestContext context) throws IOException {
		owner = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
		final InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("contracts/PaymentSplitter.aes");
		paymentSplitterSource = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());

		initialReceiver1 = new KeyPairServiceFactory().getService().generateBaseKeyPair();
		initialReceiver2 = new KeyPairServiceFactory().getService().generateBaseKeyPair();
		initialReceiver3 = new KeyPairServiceFactory().getService().generateBaseKeyPair();
		_logger.info("Initial receiver 1: " + initialReceiver1.getPublicKey());
		_logger.info("Initial receiver 2: " + initialReceiver2.getPublicKey());
		_logger.info("Initial receiver 3: " + initialReceiver3.getPublicKey());

		initialWeights.put(initialReceiver1.getPublicKey(), 40);
		initialWeights.put(initialReceiver2.getPublicKey(), 40);
		initialWeights.put(initialReceiver3.getPublicKey(), 20);
		context.assertEquals(3, initialWeights.size());
	}

	private String generateMapParam(Map<String, Integer> recipientConditions) {
		Set<String> recipientConditionSet = new HashSet<>();
		recipientConditions.forEach((k, v) -> recipientConditionSet.add("[" + k + "] = " + v));
		return "{" + recipientConditionSet.stream().collect(Collectors.joining(", ")) + "}";
	}

	@Test
	public void a_deployPaymentSplitterTest(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				Single<ByteCode> byteCodeSingle = this.sophiaCompilerService.compile(paymentSplitterSource, null, null);
				TestObserver<ByteCode> byteCodeTestObserver = byteCodeSingle.test();
				byteCodeTestObserver.awaitTerminalEvent();
				ByteCode byteCode = byteCodeTestObserver.values().get(0);

				Calldata calldata = encodeCalldata(paymentSplitterSource, "init",
						Arrays.asList(generateMapParam(initialWeights)));

				_logger.info("contract bytecode: " + byteCode.getBytecode());
				_logger.info("contract calldata: " + calldata.getCalldata());

				Single<AccountResult> accountSingle = accountService.asyncGetAccount(owner.getPublicKey());
				TestObserver<AccountResult> accountTestObserver = accountSingle.test();
				accountTestObserver.awaitTerminalEvent();
				AccountResult account = accountTestObserver.values().get(0);
				String ownerId = owner.getPublicKey();
				BigInteger abiVersion = BigInteger.ONE;
				BigInteger vmVersion = BigInteger.valueOf(4);
				BigInteger amount = BigInteger.ZERO;
				BigInteger deposit = BigInteger.ZERO;
				BigInteger ttl = BigInteger.ZERO;
				BigInteger gas = BigInteger.valueOf(4800000);
				BigInteger gasPrice = BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE);
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);

				AbstractTransaction<?> contractTx = transactionServiceNative.getTransactionFactory()
						.createContractCreateTransaction(abiVersion, amount, calldata.getCalldata(),
								byteCode.getBytecode(), deposit, gas, gasPrice, nonce, ownerId, ttl, vmVersion);

				UnsignedTx unsignedTx = transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();
				_logger.info("Unsigned Tx - hash - dryRun: " + unsignedTx.getTx());

				// DryRunResults dryRunResults =
				// performDryRunTransactions(
				// Arrays.asList(
				// ImmutableMap.of(AccountParameter.PUBLIC_KEY,
				// owner.getPublicKey())),
				// null,
				// Arrays.asList(unsignedTx));
				// _logger.info("callContractAfterDryRunOnLocalNode: " +
				// dryRunResults.toString());
				// context.assertEquals(1, dryRunResults.getResults().size());
				// DryRunResult dryRunResult = dryRunResults.getResults().get(0);
				// context.assertEquals("ok", dryRunResult.getResult());
				//
				// contractTx =
				// transactionServiceNative
				// .getTransactionFactory()
				// .createContractCreateTransaction(
				// abiVersion,
				// amount,
				// calldata.getCalldata(),
				// byteCode.getBytecode(),
				// deposit,
				// dryRunResult.getCallObj().getGasUsed(),
				// dryRunResult.getCallObj().getGasPrice(),
				// nonce,
				// ownerId,
				// ttl,
				// vmVersion);
				//
				// unsignedTx =
				//
				// transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();

				Tx signedTxNative = transactionServiceNative.signTransaction(unsignedTx, owner.getPrivateKey());
				_logger.info("CreateContractTx hash (native signed): " + signedTxNative);

				PostTxResponse postTxResponse = postTx(signedTxNative);
				TxInfoObject txInfoObject = waitForTxInfoObject(postTxResponse.getTxHash());
				localDeployedContractId = txInfoObject.getCallInfo().getContractId();
				_logger.info("Deployed contract - hash " + postTxResponse.getTxHash() + " - " + txInfoObject);
				if ("revert".equals(txInfoObject.getCallInfo().getReturnType())) {
					context.fail("transaction reverted: "
							+ decodeCalldata(txInfoObject.getCallInfo().getReturnValue(), "string"));
				}
			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	@Test
	public void b_callPayAndSplitMethodTest(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = getAccount(owner.getPublicKey());
				BigInteger balanceRecipient1;
				BigInteger balanceRecipient2;
				BigInteger balanceRecipient3;
				try {
					balanceRecipient1 = getAccount(initialReceiver1.getPublicKey()).getBalance();
					balanceRecipient2 = getAccount(initialReceiver2.getPublicKey()).getBalance();
					balanceRecipient3 = getAccount(initialReceiver3.getPublicKey()).getBalance();
					// if one of the accounts wasn't active we get an error and know that the
					// accounts
					// don't have any balance
				} catch (Exception e) {
					balanceRecipient1 = BigInteger.ZERO;
					balanceRecipient2 = BigInteger.ZERO;
					balanceRecipient3 = BigInteger.ZERO;
				}
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);
				BigDecimal paymentValue = UnitConversionUtil.toAettos("1", Unit.AE);
				Calldata calldata = encodeCalldata(paymentSplitterSource, "payAndSplit", null);
				_logger.info("Contract ID: " + localDeployedContractId);

				DryRunResults dryRunResults = performDryRunTransactions(
						Arrays.asList(ImmutableMap.of(AccountParameter.PUBLIC_KEY, owner.getPublicKey())), null,
						Arrays.asList(createUnsignedContractCallTx(owner.getPublicKey(), nonce, calldata.getCalldata(),
								null, localDeployedContractId, paymentValue.toBigInteger())));
				_logger.info("callContractAfterDryRunOnLocalNode: " + dryRunResults.toString());
				context.assertEquals(1, dryRunResults.getResults().size());
				DryRunResult dryRunResult = dryRunResults.getResults().get(0);
				context.assertEquals("ok", dryRunResult.getResult());

				AbstractTransaction<?> contractAfterDryRunTx = transactionServiceNative.getTransactionFactory()
						.createContractCallTransaction(BigInteger.ONE, calldata.getCalldata(), localDeployedContractId,
								dryRunResult.getCallObj().getGasUsed(), dryRunResult.getCallObj().getGasPrice(), nonce,
								owner.getPublicKey(), BigInteger.ZERO);
				((ContractCallTransaction) contractAfterDryRunTx).setAmount(paymentValue.toBigInteger());

				UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(contractAfterDryRunTx)
						.blockingGet();

				Tx signedTxNative = transactionServiceNative.signTransaction(unsignedTxNative, owner.getPrivateKey());

				// post the signed contract call tx
				PostTxResponse postTxResponse = postTx(signedTxNative);
				context.assertEquals(postTxResponse.getTxHash(),
						transactionServiceNative.computeTxHash(signedTxNative.getTx()));
				_logger.info("CreateContractTx hash: " + postTxResponse.getTxHash());

				// we wait until the tx is available and the payment should have been splitted
				TxInfoObject txInfoObject = waitForTxInfoObject(postTxResponse.getTxHash());
				_logger.info("PayAndSplit transaction - hash " + postTxResponse.getTxHash() + " - " + txInfoObject);
				if ("revert".equals(txInfoObject.getCallInfo().getReturnType())) {
					context.fail("transaction reverted: "
							+ decodeCalldata(txInfoObject.getCallInfo().getReturnValue(), "string"));
				}

				context.assertEquals(
						balanceRecipient1.add(paymentValue.multiply(BigDecimal.valueOf(0.4)).toBigInteger()),
						getAccount(initialReceiver1.getPublicKey()).getBalance());
				context.assertEquals(
						balanceRecipient2.add(paymentValue.multiply(BigDecimal.valueOf(0.4)).toBigInteger()),
						getAccount(initialReceiver2.getPublicKey()).getBalance());
				context.assertEquals(
						balanceRecipient3.add(paymentValue.multiply(BigDecimal.valueOf(0.2)).toBigInteger()),
						getAccount(initialReceiver3.getPublicKey()).getBalance());

			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}
}
