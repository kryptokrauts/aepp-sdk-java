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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.model.DryRunResult;
import com.kryptokrauts.aeternity.generated.model.DryRunResults;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.domain.account.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.domain.transaction.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.AccountParameter;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;

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
	public void a_deployPaymentSplitterTest(TestContext context) throws Throwable {
		String byteCode = this.aeternityServiceNative.compiler.blockingCompile(paymentSplitterSource, null, null);
		String callData = this.aeternityServiceNative.compiler.blockingEncodeCalldata(paymentSplitterSource, "init",
				Arrays.asList(generateMapParam(initialWeights)));

		_logger.info("contract bytecode: " + byteCode);
		_logger.info("contract calldata: " + callData);

		AccountResult account = this.getAccount(owner.getPublicKey(), context);
		String ownerId = owner.getPublicKey();
		BigInteger abiVersion = BigInteger.ONE;
		BigInteger vmVersion = BigInteger.valueOf(4);
		BigInteger amount = BigInteger.ZERO;
		BigInteger deposit = BigInteger.ZERO;
		BigInteger ttl = BigInteger.ZERO;
		BigInteger gas = BigInteger.valueOf(4800000);
		BigInteger gasPrice = BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE);
		BigInteger nonce = account.getNonce().add(BigInteger.ONE);

		ContractCreateTransactionModel contractCreate = ContractCreateTransactionModel.builder().abiVersion(abiVersion)
				.amount(amount).callData(callData).contractByteCode(byteCode).deposit(deposit).gas(gas)
				.gasPrice(gasPrice).nonce(nonce).ownerId(ownerId).ttl(ttl).vmVersion(vmVersion).build();

		String unsignedTx = aeternityServiceNative.transactions.blockingCreateUnsignedTransaction(contractCreate);
		_logger.info("Unsigned Tx - hash - dryRun: " + unsignedTx);

		DryRunResults dryRunResults = aeternityServiceNative.transactions.blockingDryRunTransactions(
				Arrays.asList(ImmutableMap.of(AccountParameter.PUBLIC_KEY, owner.getPublicKey())), null,
				Arrays.asList(unsignedTx));
		_logger.info("callContractAfterDryRunOnLocalNode: " + dryRunResults.toString());
		context.assertEquals(1, dryRunResults.getResults().size());
		DryRunResult dryRunResult = dryRunResults.getResults().get(0);
		context.assertEquals("ok", dryRunResult.getResult());

		contractCreate.toBuilder().gas(dryRunResult.getCallObj().getGasUsed())
				.gasPrice(dryRunResult.getCallObj().getGasPrice()).build();

		PostTransactionResult result = aeternityServiceNative.transactions.blockingPostTransaction(contractCreate);

		TxInfoObject txInfoObject = waitForTxInfoObject(result.getTxHash());

		localDeployedContractId = txInfoObject.getCallInfo().getContractId();
		_logger.info("Deployed contract - hash " + result.getTxHash() + " - " + txInfoObject);
		if ("revert".equals(txInfoObject.getCallInfo().getReturnType())) {
			context.assertTrue(false,
					"transaction reverted: " + decodeCalldata(txInfoObject.getCallInfo().getReturnValue(), "string"));
		}
	}

	@Test
	public void b_callPayAndSplitMethodTest(TestContext context) throws Throwable {
		AccountResult account = getAccount(owner.getPublicKey(), context);
		BigInteger balanceRecipient1;
		BigInteger balanceRecipient2;
		BigInteger balanceRecipient3;
		// if one of the accounts wasn't active we get an error and know that the
		// accounts don't have any balance
		balanceRecipient1 = Optional.ofNullable(getAccount(initialReceiver1.getPublicKey(), context).getBalance())
				.orElse(BigInteger.ZERO);
		balanceRecipient2 = Optional.ofNullable(getAccount(initialReceiver2.getPublicKey(), context).getBalance())
				.orElse(BigInteger.ZERO);
		balanceRecipient3 = Optional.ofNullable(getAccount(initialReceiver3.getPublicKey(), context).getBalance())
				.orElse(BigInteger.ZERO);

		BigInteger nonce = account.getNonce().add(BigInteger.ONE);
		BigDecimal paymentValue = UnitConversionUtil.toAettos("1", Unit.AE);
		String calldata = aeternityServiceNative.compiler.blockingEncodeCalldata(paymentSplitterSource, "payAndSplit",
				null);
		_logger.info("Contract ID: " + localDeployedContractId);

		DryRunResults dryRunResults = aeternityServiceNative.transactions.blockingDryRunTransactions(
				Arrays.asList(ImmutableMap.of(AccountParameter.PUBLIC_KEY, owner.getPublicKey())), null,
				Arrays.asList(createUnsignedContractCallTx(owner.getPublicKey(), nonce, calldata, null,
						localDeployedContractId, paymentValue.toBigInteger())));
		_logger.info("callContractAfterDryRunOnLocalNode: " + dryRunResults.toString());
		context.assertEquals(1, dryRunResults.getResults().size());
		DryRunResult dryRunResult = dryRunResults.getResults().get(0);
		context.assertEquals("ok", dryRunResult.getResult());

		ContractCallTransactionModel contractAfterDryRun = ContractCallTransactionModel.builder()
				.abiVersion(BigInteger.ONE).callData(calldata).contractId(localDeployedContractId)
				.gas(dryRunResult.getCallObj().getGasUsed()).gasPrice(dryRunResult.getCallObj().getGasPrice())
				.nonce(nonce).callerId(owner.getPublicKey()).ttl(BigInteger.ZERO).amount(paymentValue.toBigInteger())
				.build();

		PostTransactionResult postTransactionResult = aeternityServiceNative.transactions
				.blockingPostTransaction(contractAfterDryRun);
		context.assertEquals(postTransactionResult.getTxHash(),
				aeternityServiceNative.transactions.computeTxHash(contractAfterDryRun));
		_logger.info("CreateContractTx hash: " + postTransactionResult.getTxHash());

		// we wait until the tx is available and the payment should have been splitted
		TxInfoObject txInfoObject = waitForTxInfoObject(postTransactionResult.getTxHash());
		_logger.info("PayAndSplit transaction - hash " + postTransactionResult.getTxHash() + " - " + txInfoObject);
		if ("revert".equals(txInfoObject.getCallInfo().getReturnType())) {
			context.fail(
					"transaction reverted: " + decodeCalldata(txInfoObject.getCallInfo().getReturnValue(), "string"));
		}

		context.assertEquals(balanceRecipient1.add(paymentValue.multiply(BigDecimal.valueOf(0.4)).toBigInteger()),
				getAccount(initialReceiver1.getPublicKey(), context).getBalance());
		context.assertEquals(balanceRecipient2.add(paymentValue.multiply(BigDecimal.valueOf(0.4)).toBigInteger()),
				getAccount(initialReceiver2.getPublicKey(), context).getBalance());
		context.assertEquals(balanceRecipient3.add(paymentValue.multiply(BigDecimal.valueOf(0.2)).toBigInteger()),
				getAccount(initialReceiver3.getPublicKey(), context).getBalance());

	}

	protected String createUnsignedContractCallTx(String callerId, BigInteger nonce, String calldata,
			BigInteger gasPrice, String contractId, BigInteger amount) throws Throwable {
		BigInteger abiVersion = BigInteger.ONE;
		BigInteger ttl = BigInteger.ZERO;
		BigInteger gas = BigInteger.valueOf(1579000);

		ContractCallTransactionModel model = ContractCallTransactionModel.builder().abiVersion(abiVersion)
				.callData(calldata).contractId(contractId).gas(gas).amount(amount)
				.gasPrice(gasPrice != null ? gasPrice : BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE))
				.nonce(nonce).callerId(callerId).ttl(ttl).build();

		return aeternityServiceNative.transactions.blockingCreateUnsignedTransaction(model);
	}
}
