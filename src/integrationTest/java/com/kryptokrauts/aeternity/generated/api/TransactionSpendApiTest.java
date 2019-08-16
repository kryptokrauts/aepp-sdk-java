package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;

import org.bouncycastle.crypto.CryptoException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;

import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionSpendApiTest extends BaseTest {
//
//	BaseKeyPair baseKeyPair;
//
//	@Before
//	public void initBeforeTest() {
//		baseKeyPair = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
//	}

	/**
	 * create an unsigned native spend transaction
	 *
	 * @param context
	 */
	@Test
	public void buildNativeSpendTransactionTest(TestContext context) {
		Async async = context.async();

		String sender = keyPairService.generateBaseKeyPair().getPublicKey();
		String recipient = keyPairService.generateBaseKeyPair().getPublicKey();
		BigInteger amount = BigInteger.valueOf(1000);
		String payload = "payload";
		BigInteger ttl = BigInteger.valueOf(100);
		BigInteger nonce = BigInteger.valueOf(5);

		// sender, recipient, amount, payload, ttl, nonce

		SpendTransactionModel model = SpendTransactionModel.setParameters().sender(sender).recipient(recipient)
				.amount(amount).payload(payload).ttl(ttl).nonce(nonce).wrap();

		SpendTransaction spendTx = aeternityServiceNative.models().createSpendTransaction(model);
		UnsignedTx unsignedTxNative = aeternityServiceNative.transactions.createUnsignedTransaction(spendTx)
				.blockingGet();

		Single<UnsignedTx> unsignedTx = aeternityServiceNative.transactions.createUnsignedTransaction(spendTx);
		unsignedTx.subscribe(it -> {
			context.assertEquals(it, unsignedTxNative);
			async.complete();
		}, throwable -> {
			context.fail(throwable);
		});
	}

	@Test
	public void postSpendSelfSignTxTest(TestContext context) {
		Async async = context.async();

		BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);

		// get the currents accounts nonce in case a transaction is already
		// created and increase it by one
		Single<AccountResult> acc = this.aeternityServiceNative.accounts.asyncGetAccount(keyPair.getPublicKey());
		acc.subscribe(account -> {
			BaseKeyPair kp = keyPairService.generateBaseKeyPair();
			String recipient = kp.getPublicKey();
			BigInteger amount = new BigInteger("1000000000000000000");
			String payload = "payload";
			BigInteger ttl = BigInteger.ZERO;
			BigInteger nonce = account.getNonce().add(BigInteger.ONE);

			/**
			 * models als eigene klasse -> beschaffung über service quatsch ab hier dritter
			 * test - zusammenfassen, keypair für sign aus der config
			 * 
			 */
			SpendTransactionModel model = SpendTransactionModel.setParameters().sender(account.getPublicKey())
					.recipient(recipient).amount(amount).payload(payload).ttl(ttl).nonce(nonce).wrap();

			AbstractTransaction<?> spendTx = aeternityServiceNative.models().createSpendTransaction(model);
			UnsignedTx unsignedTxNative = aeternityServiceNative.transactions.createUnsignedTransaction(spendTx)
					.blockingGet();
			Tx signedTx = aeternityServiceNative.transactions.signTransaction(unsignedTxNative,
					keyPair.getPrivateKey());

			Single<PostTxResponse> txResponse = aeternityServiceNative.transactions.postTransaction(signedTx);
			txResponse.subscribe(it -> {
				_logger.info("SpendTx hash: " + it.getTxHash());
				context.assertEquals(it.getTxHash(), aeternityServiceNative.transactions.computeTxHash(spendTx));
				async.complete();
			}, throwable -> {
				context.fail();
			});
		}, throwable -> {
			context.fail();
		});
	}

	@Test
	public void postSpendSelfSignTxTestWithModel(TestContext context) throws CryptoException {
		Async async = context.async();

		AccountResult acc = this.aeternityServiceNative.accounts.blockingGetAccount(baseKeyPair.getPublicKey());

		BaseKeyPair recipient = keyPairService.generateBaseKeyPair();

		SpendTransaction spendTx = aeternityServiceNative.models()
				.createSpendTransaction(SpendTransactionModel.setParameters().sender(acc.getPublicKey())
						.recipient(recipient.getPublicKey()).amount(new BigInteger("1000000000000000000"))
						.payload("donation").ttl(BigInteger.ZERO).nonce(acc.getNonce().add(BigInteger.ONE)).wrap());

		Single<PostTxResponse> txResponse = aeternityServiceNative.transactions.postTransaction(spendTx);

		txResponse.subscribe(it -> {
			_logger.info("SpendTx hash: " + it.getTxHash());
			context.assertEquals(it.getTxHash(), aeternityServiceNative.transactions.computeTxHash(spendTx));
			async.complete();
		}, throwable -> {
			context.fail();
		});
	}
}
