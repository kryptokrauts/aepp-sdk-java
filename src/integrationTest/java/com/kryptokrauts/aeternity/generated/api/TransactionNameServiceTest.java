package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.util.Random;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.kryptokrauts.aeternity.sdk.service.domain.account.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.domain.name.NameIdResult;
import com.kryptokrauts.aeternity.sdk.service.domain.transaction.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameClaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NamePreclaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameRevokeTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameUpdateTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

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
		String sender = keyPairService.generateBaseKeyPair().getPublicKey();
		BigInteger salt = CryptoUtils.generateNamespaceSalt();
		BigInteger nonce = BigInteger.valueOf(1);
		BigInteger ttl = BigInteger.valueOf(100);

		NamePreclaimTransactionModel preclaim = NamePreclaimTransactionModel.builder().accountId(sender)
				.name(validDomain).salt(salt).nonce(nonce).ttl(ttl).build();

		String unsignedTxNative = this.aeternityServiceNative.transactions.blockingCreateUnsignedTransaction(preclaim);

		String unsignedTxDebug = this.aeternityServiceDebug.transactions.blockingCreateUnsignedTransaction(preclaim);

		context.assertEquals(unsignedTxDebug, unsignedTxNative);
	}

	/**
	 * @param context
	 * @throws Throwable
	 */
	@Test
	public void postNameClaimTxTest(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				_logger.info("--------------------- postNameClaimTxTest ---------------------");
				AccountResult account = getAccount(baseKeyPair.getPublicKey(), context);
				BigInteger salt = CryptoUtils.generateNamespaceSalt();
				BigInteger nonce = account.getNonce().add(ONE);
				BigInteger ttl = BigInteger.ZERO;

				NamePreclaimTransactionModel namePreclaimTx = NamePreclaimTransactionModel.builder()
						.accountId(baseKeyPair.getPublicKey()).name(validDomain).salt(salt).nonce(nonce).ttl(ttl)
						.build();

				PostTransactionResult result = this.postTx(namePreclaimTx);
				_logger.info("NamePreclaimTx hash: " + result.getTxHash());
				context.assertEquals(result.getTxHash(),
						this.aeternityServiceNative.transactions.computeTxHash(namePreclaimTx));

				NameClaimTransactionModel nameClaimTx = NameClaimTransactionModel.builder()
						.accountId(baseKeyPair.getPublicKey()).name(validDomain).nameSalt(salt).nonce(nonce.add(ONE))
						.ttl(ttl).build();

				_logger.info(this.aeternityServiceNative.transactions.blockingCreateUnsignedTransaction(nameClaimTx));

				result = this.postTx(nameClaimTx);
				_logger.info(String.format("Using namespace %s and salt %s for committmentId %s", validDomain, salt,
						EncodingUtils.generateCommitmentHash(validDomain, salt)));
				_logger.info("NameClaimTx hash: " + result.getTxHash());

				// GenericSignedTx genericSignedTx = getTxByHash(postTxResponse.getTxHash(),
				// 10);
				// context.assertTrue(genericSignedTx.getBlockHeight().intValue() > 0);
				// NameClaimTxJSON typedTx = (NameClaimTxJSON) genericSignedTx.getTx();
				// _logger.info("Successfully claimed aens " + typedTx.getName());
				_logger.info("--------------------- postNameClaimTxTest ---------------------");
			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	/**
	 * @param context
	 * @throws Throwable
	 */
	@Test
	public void postUpdateTxTest(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				_logger.info("--------------------- postUpdateTxTest ---------------------");
				AccountResult account = getAccount(baseKeyPair.getPublicKey(), context);
				BigInteger nonce = account.getNonce().add(ONE);
				BigInteger salt = CryptoUtils.generateNamespaceSalt();
				BigInteger ttl = BigInteger.ZERO;
				String domain = TestConstants.DOMAIN + random.nextInt() + TestConstants.NAMESPACE;

				/** create a new namespace to update later */
				NamePreclaimTransactionModel namePreclaimTx = NamePreclaimTransactionModel.builder()
						.accountId(baseKeyPair.getPublicKey()).name(domain).salt(salt).nonce(nonce).ttl(ttl).build();

				PostTransactionResult namePreclaimResult = this.postTx(namePreclaimTx);
				_logger.info("NamePreclaimTx hash: " + namePreclaimResult.getTxHash());

				context.assertEquals(namePreclaimResult.getTxHash(),
						this.aeternityServiceNative.transactions.computeTxHash(namePreclaimTx));

				NameClaimTransactionModel nameClaimTx = NameClaimTransactionModel.builder()
						.accountId(baseKeyPair.getPublicKey()).name(domain).nameSalt(salt).nonce(nonce.add(ONE))
						.ttl(ttl).build();
				PostTransactionResult nameClaimResult = this.postTx(nameClaimTx);
				_logger.info(String.format("Using namespace %s and salt %s for committmentId %s", domain, salt,
						EncodingUtils.generateCommitmentHash(domain, salt)));
				_logger.info("NameClaimTx hash: " + nameClaimResult.getTxHash());

				NameIdResult nameIdResult = this.aeternityServiceNative.names.blockingGetNameId(domain);
				BigInteger initialTTL = nameIdResult.getTtl();

				_logger.info(
						String.format("Created namespace %s with salt %s and nameEntry %s in tx %s for update test",
								domain, salt, nameIdResult, nameClaimResult.getTxHash()));
				/** finished creating namespace */

				BigInteger nameTtl = BigInteger.valueOf(10000l);
				BigInteger clientTtl = BigInteger.valueOf(50l);
				account = getAccount(baseKeyPair.getPublicKey(), context);
				nonce = account.getNonce().add(ONE);

				NameUpdateTransactionModel nameUpdateTx = NameUpdateTransactionModel.builder()
						.accountId(baseKeyPair.getPublicKey()).nameId(nameIdResult.getId()).nonce(nonce).ttl(ttl)
						.clientTtl(clientTtl).nameTtl(nameTtl).build();

				PostTransactionResult nameUpdateResult = this.postTx(nameUpdateTx);
				context.assertEquals(nameUpdateResult.getTxHash(),
						this.aeternityServiceNative.transactions.computeTxHash(nameUpdateTx));

				nameIdResult = this.aeternityServiceNative.names.blockingGetNameId(domain);
				_logger.info(
						String.format("Updated namespace %s with salt %s and nameEntry %s in tx %s for update test",
								domain, salt, nameIdResult, nameUpdateResult.getTxHash()));

				BigInteger updatedTTL = nameIdResult.getTtl();
				// subtract 40000 because initial default ttl is 50000 and updated ttl was 10000
				int diffTtl = initialTTL.subtract(updatedTTL).intValue();
				context.assertTrue(diffTtl <= 40000);
				if (diffTtl < 40000) {
					_logger.info(String.format("Diff of Ttl is %s, this happens when meanwhile new blocks are mined",
							diffTtl));
				}
				_logger.info("--------------------- postUpdateTxTest ---------------------");
			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}

	/**
	 * @param context
	 * @throws Throwable
	 */
	@Test
	public void postRevokeTxTest(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				_logger.info("--------------------- postRevokeTxTest ---------------------");
				String nameId = this.aeternityServiceNative.names.blockingGetNameId(validDomain).getId();

				AccountResult account = getAccount(baseKeyPair.getPublicKey(), context);
				BigInteger nonce = account.getNonce().add(ONE);
				BigInteger ttl = BigInteger.ZERO;

				NameRevokeTransactionModel nameRevokeTx = NameRevokeTransactionModel.builder()
						.accountId(baseKeyPair.getPublicKey()).nameId(nameId).nonce(nonce).ttl(ttl).build();

				PostTransactionResult nameRevokeResult = this.postTx(nameRevokeTx);
				_logger.info("NameRevokeTx hash: " + nameRevokeResult.getTxHash());

				context.assertEquals(nameRevokeResult.getTxHash(),
						this.aeternityServiceNative.transactions.computeTxHash(nameRevokeTx));

				NameIdResult result = this.aeternityServiceNative.names.blockingGetNameId(validDomain);
				context.assertTrue("{\"reason\":\"Name revoked\"}".contentEquals(result.getRootErrorMessage()));

				_logger.info(String.format("Validated, that namespace %s is revoked", validDomain));

				_logger.info("--------------------- postRevokeTxTest ---------------------");
			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}
}
