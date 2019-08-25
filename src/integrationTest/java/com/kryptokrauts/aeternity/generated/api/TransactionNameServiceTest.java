package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Random;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.kryptokrauts.aeternity.generated.ApiException;
import com.kryptokrauts.aeternity.generated.model.NameEntry;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.domain.account.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionNameServiceTest extends BaseTest {

	static Random random = new Random();

	static String invalidDomain = TestConstants.DOMAIN + random.nextInt();
	static String validDomain = invalidDomain + TestConstants.NAMESPACE;

	/**
	 * create an unsigned native namepreclaim transaction
	 *
	 * @param context
	 */
	@Test
	public void buildNativeNamePreclaimTransactionTest(TestContext context) {
		Async async = context.async();

		String sender = keyPairService.generateBaseKeyPair().getPublicKey();
		BigInteger salt = CryptoUtils.generateNamespaceSalt();
		BigInteger nonce = BigInteger.valueOf(1);
		BigInteger ttl = BigInteger.valueOf(100);

		AbstractTransaction<?> namePreclaimTx = transactionServiceNative.getTransactionFactory()
				.createNamePreclaimTransaction(sender, validDomain, salt, nonce, ttl);
		UnsignedTx unsignedTxNative = transactionServiceNative.asyncCreateUnsignedTransaction(namePreclaimTx)
				.blockingGet();

		Single<UnsignedTx> unsignedTx = transactionServiceDebug.asyncCreateUnsignedTransaction(namePreclaimTx);
		unsignedTx.subscribe(it -> {
			context.assertEquals(it, unsignedTxNative);
			async.complete();
		}, throwable -> context.fail(throwable));
	}

	/** @param context */
	@Test
	public void postNameClaimTxTest(TestContext context) {
		Async async = context.async();
		BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = callMethodAndGetResult(
						() -> accountService.asyncGetAccount(keyPair.getPublicKey()), AccountResult.class);
				BigInteger salt = CryptoUtils.generateNamespaceSalt();
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);
				BigInteger ttl = BigInteger.ZERO;

				AbstractTransaction<?> namePreclaimTx = transactionServiceNative.getTransactionFactory()
						.createNamePreclaimTransaction(keyPair.getPublicKey(), validDomain, salt, nonce, ttl);
				UnsignedTx unsignedTx = transactionServiceNative.asyncCreateUnsignedTransaction(namePreclaimTx)
						.blockingGet();
				Tx signedTx = transactionServiceNative.signTransaction(unsignedTx, keyPair.getPrivateKey());
				_logger.info("Signed NamePreclaimTx: " + signedTx.getTx());

				PostTxResponse postTxResponse = postTx(signedTx);
				_logger.info("NamePreclaimTx hash: " + postTxResponse.getTxHash());
				context.assertEquals(postTxResponse.getTxHash(),
						transactionServiceNative.computeTxHash(signedTx.getTx()));

				AbstractTransaction<?> nameClaimTx = transactionServiceNative.getTransactionFactory()
						.createNameClaimTransaction(keyPair.getPublicKey(), validDomain, salt,
								nonce.add(BigInteger.ONE), ttl);
				UnsignedTx unsignedClaimTx = transactionServiceNative.asyncCreateUnsignedTransaction(nameClaimTx)
						.blockingGet();
				Tx signedClaimTx = transactionServiceNative.signTransaction(unsignedClaimTx, keyPair.getPrivateKey());
				_logger.info("Signed NameClaimTx: " + signedClaimTx.getTx());
				postTxResponse = postTx(signedClaimTx);
				_logger.info(String.format("Using namespace %s and salt %s for committmentId %s", validDomain, salt,
						EncodingUtils.generateCommitmentHash(validDomain, salt)));
				_logger.info("NameClaimTx hash: " + postTxResponse.getTxHash());

				// GenericSignedTx genericSignedTx = getTxByHash(postTxResponse.getTxHash(),
				// 10);
				// context.assertTrue(genericSignedTx.getBlockHeight().intValue() > 0);
				// NameClaimTxJSON typedTx = (NameClaimTxJSON) genericSignedTx.getTx();
				// _logger.info("Successfully claimed aens " + typedTx.getName());

			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	/** @param context */
	@Test
	public void postUpdateTxTest(TestContext context) {
		Async async = context.async();
		BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult account = callMethodAndGetResult(
						() -> accountService.asyncGetAccount(keyPair.getPublicKey()), AccountResult.class);
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);
				BigInteger salt = CryptoUtils.generateNamespaceSalt();
				BigInteger ttl = BigInteger.ZERO;
				String domain = TestConstants.DOMAIN + random.nextInt() + TestConstants.NAMESPACE;

				/** create a new namespace to update later */
				AbstractTransaction<?> namePreclaimTx = transactionServiceNative.getTransactionFactory()
						.createNamePreclaimTransaction(keyPair.getPublicKey(), domain, salt, nonce, ttl);
				UnsignedTx unsignedTx = transactionServiceNative.asyncCreateUnsignedTransaction(namePreclaimTx)
						.blockingGet();
				Tx signedTx = transactionServiceNative.signTransaction(unsignedTx, keyPair.getPrivateKey());
				PostTxResponse postTxResponse = postTx(signedTx);
				context.assertEquals(postTxResponse.getTxHash(),
						transactionServiceNative.computeTxHash(signedTx.getTx()));
				AbstractTransaction<?> nameClaimTx = transactionServiceNative.getTransactionFactory()
						.createNameClaimTransaction(keyPair.getPublicKey(), domain, salt, nonce.add(BigInteger.ONE),
								ttl);
				UnsignedTx unsignedClaimTx = transactionServiceNative.asyncCreateUnsignedTransaction(nameClaimTx)
						.blockingGet();
				Tx signedClaimTx = transactionServiceNative.signTransaction(unsignedClaimTx, keyPair.getPrivateKey());
				PostTxResponse postClaimTxResponse = postTx(signedClaimTx);
				NameEntry nameEntry = callMethodAndGetResult(() -> this.nameService.getNameId(domain), NameEntry.class);
				BigInteger initialTTL = nameEntry.getTtl();
				_logger.info(
						String.format("Created namespace %s with salt %s and nameEntry %s in tx %s for update test",
								domain, salt, nameEntry, postClaimTxResponse.getTxHash()));
				/** finished creating namespace */
				BigInteger nameTtl = BigInteger.valueOf(10000l);
				BigInteger clientTtl = BigInteger.valueOf(50l);
				account = callMethodAndGetResult(() -> accountService.asyncGetAccount(keyPair.getPublicKey()),
						AccountResult.class);
				nonce = account.getNonce().add(BigInteger.ONE);

				AbstractTransaction<?> nameUpdateTx = transactionServiceNative.getTransactionFactory()
						.createNameUpdateTransaction(keyPair.getPublicKey(), nameEntry.getId(), nonce, ttl, clientTtl,
								nameTtl, new LinkedList<>());
				UnsignedTx unsignedUpdateTx = transactionServiceNative.asyncCreateUnsignedTransaction(nameUpdateTx)
						.blockingGet();
				Tx signedUpdateTx = transactionServiceNative.signTransaction(unsignedUpdateTx, keyPair.getPrivateKey());

				PostTxResponse postUpdateTxResponse = postTx(signedUpdateTx);
				context.assertEquals(postUpdateTxResponse.getTxHash(),
						transactionServiceNative.computeTxHash(signedUpdateTx.getTx()));

				nameEntry = callMethodAndGetResult(() -> this.nameService.getNameId(domain), NameEntry.class);
				_logger.info(
						String.format("Updated namespace %s with salt %s and nameEntry %s in tx %s for update test",
								domain, salt, nameEntry, postClaimTxResponse.getTxHash()));

				BigInteger updatedTTL = nameEntry.getTtl();
				// subtract 40000 because initial default ttl is 50000 and updated ttl was 10000
				int diffTtl = initialTTL.subtract(updatedTTL).intValue();
				context.assertTrue(diffTtl <= 40000);
				if (diffTtl < 40000) {
					_logger.info(String.format("Diff of Ttl is %s, this happens when meanwhile new blocks are mined",
							diffTtl));
				}

			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	/** @param context */
	@Test
	public void postRevokeTxTest(TestContext context) {
		Async async = context.async();
		BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
		rule.vertx().executeBlocking(future -> {
			try {
				String nameId = callMethodAndGetResult(() -> this.nameService.getNameId(validDomain), NameEntry.class)
						.getId();

				AccountResult account = callMethodAndGetResult(
						() -> accountService.asyncGetAccount(keyPair.getPublicKey()), AccountResult.class);
				BigInteger nonce = account.getNonce().add(BigInteger.ONE);
				BigInteger ttl = BigInteger.ZERO;

				AbstractTransaction<?> nameRevokeTx = transactionServiceNative.getTransactionFactory()
						.createNameRevokeTransaction(keyPair.getPublicKey(), nameId, nonce, ttl);
				UnsignedTx unsignedTx = transactionServiceNative.asyncCreateUnsignedTransaction(nameRevokeTx)
						.blockingGet();
				Tx signedTx = transactionServiceNative.signTransaction(unsignedTx, keyPair.getPrivateKey());
				_logger.info("Signed NameRevokeTx: " + signedTx.getTx());

				PostTxResponse postTxResponse = postTx(signedTx);
				_logger.info("NameRevokeTx hash: " + postTxResponse.getTxHash());
				context.assertEquals(postTxResponse.getTxHash(),
						transactionServiceNative.computeTxHash(signedTx.getTx()));

				try {
					callMethodAndAwaitException(() -> this.nameService.getNameId(validDomain), NameEntry.class);
				} catch (Throwable t) {
					context.assertEquals(ApiException.class, t.getClass());
					context.assertEquals("Not Found", t.getMessage());
					_logger.info(String.format("Validated, that namespace %s is revoked", validDomain));
				}

			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}
}
