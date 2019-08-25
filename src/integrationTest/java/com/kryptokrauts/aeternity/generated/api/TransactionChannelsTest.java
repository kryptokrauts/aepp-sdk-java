package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.domain.transaction.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;

import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionChannelsTest extends BaseTest {

	BaseKeyPair initiator;
	BaseKeyPair responder;

	@Before
	public void initBeforeTest() {
		initiator = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
		responder = keyPairService.generateBaseKeyPair();
	}

	@Test
	public void aFundResponderAccount(TestContext context) throws TransactionCreateException {
		Async async = context.async();

		BigInteger amount = UnitConversionUtil.toAettos("10", Unit.AE).toBigInteger();
		BigInteger nonce = getAccount(initiator.getPublicKey(), context).getNonce().add(BigInteger.ONE);

		SpendTransactionModel spendTx = SpendTransactionModel.builder().sender(initiator.getPublicKey())
				.recipient(responder.getPublicKey()).amount(amount).payload("").ttl(BigInteger.ZERO).nonce(nonce)
				.build();

		Single<PostTransactionResult> txResponse = aeternityServiceNative.transactions.asyncPostTransaction(spendTx);

		txResponse.subscribe(resultObject -> {
			context.assertNotNull(resultObject);
			async.complete();
		});
		async.awaitSuccess(TEST_CASE_TIMEOUT_MILLIS);
	}

	@Test
	public void channelCreateTest(TestContext context) throws TransactionCreateException {
		Async async = context.async();

		BigInteger amount = UnitConversionUtil.toAettos("2", Unit.AE).toBigInteger();
		BigInteger nonce = getAccount(initiator.getPublicKey(), context).getNonce();

		ChannelCreateTransactionModel model = ChannelCreateTransactionModel.builder()
				.initiator(initiator.getPublicKey()).initiatorAmount(amount).responder(responder.getPublicKey())
				.responderAmount(amount).channelReserve(BigInteger.ZERO).lockPeriod(BigInteger.ZERO)
				.ttl(BigInteger.ZERO).stateHash("").nonce(nonce).build();

		Single<String> txResponse = aeternityServiceNative.transactions.asyncCreateUnsignedTransaction(model);

		txResponse.subscribe(resultObject -> {
			context.assertNotNull(resultObject);
			System.out.println(resultObject);
			async.complete();
		});
		async.awaitSuccess(TEST_CASE_TIMEOUT_MILLIS);
	}
}
