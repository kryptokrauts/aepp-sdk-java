package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.crypto.CryptoException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.spongycastle.util.encoders.Hex;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;

import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class TransactionApiTest extends BaseTest {

	@Test
	@Ignore
	public void buildNativeTransactionTest(TestContext context) throws ExecutionException, InterruptedException {
		Async async = context.async();

		String sender = keyPairService.generateBaseKeyPair().getPublicKey();
		String recipient = keyPairService.generateBaseKeyPair().getPublicKey();
		BigInteger amount = BigInteger.valueOf(1000);
		String payload = "payload";
		BigInteger ttl = BigInteger.valueOf(100);
		BigInteger nonce = BigInteger.valueOf(5);

		AbstractTransaction<?> spendTx = transactionServiceNative.getTransactionFactory().createSpendTransaction(sender,
				recipient, amount, payload, null, ttl, nonce);
		UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(spendTx).toFuture().get();

		Single<UnsignedTx> unsignedTx = transactionServiceDebug.createUnsignedTransaction(spendTx);
		unsignedTx.subscribe(it -> {
			Assertions.assertEquals(it, unsignedTxNative);
			async.complete();
		}, throwable -> {
			throwable.printStackTrace();
			context.fail();
		});
	}

	@Test
	public void buildContractTxTestNative(TestContext context) throws ExecutionException, InterruptedException {
		Async async = context.async();

		String contractCode = "contract Identity =\\n  type state = ()\\n  function main(z : int) = z";

		BaseKeyPair kp = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);

		// BaseKeyPair kp = keyPairService.generateBaseKeyPairFromSecret(
		//
		// "a7a695f999b1872acb13d5b63a830a8ee060ba688a478a08c6e65dfad8a01cd70bb4ed7927f97b51e1bcb5e1340d12335b2a2b12c8bc5221d63c4bcb39d41e61");

		// String contractByteCode =
		// "cb_+QP1RgKgeN05+tJcdqKtrzpqKaGf7e7wSc3ARZ/hNSgeuHcoXLn5Avv5ASqgaPJnYzj/UIg5q6R3Se/6i+h+8oTyB/s9mZhwHNU4h8WEbWFpbrjAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKD//////////////////////////////////////////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAuEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+QHLoLnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqhGluaXS4YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7kBQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEA//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///////////////////////////////////////////uMxiAABkYgAAhJGAgIBRf7nJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqFGIAAMBXUIBRf2jyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFFGIAAK9XUGABGVEAW2AAGVlgIAGQgVJgIJADYAOBUpBZYABRWVJgAFJgAPNbYACAUmAA81tZWWAgAZCBUmAgkANgABlZYCABkIFSYCCQA2ADgVKBUpBWW2AgAVFRWVCAkVBQgJBQkFZbUFCCkVBQYgAAjFaFMi4xLjCUisYH";

		// String contractByteCode =
		// "cb_+QPvRgGgeN05+tJcdqKtrzpqKaGf7e7wSc3ARZ/hNSgeuHcoXLn5Avv5ASqgaPJnYzj/UIg5q6R3Se/6i+h+8oTyB/s9mZhwHNU4h8WEbWFpbrjAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKD//////////////////////////////////////////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAuEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+QHLoLnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqhGluaXS4YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7kBQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEA//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///////////////////////////////////////////uMxiAABkYgAAhJGAgIBRf7nJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqFGIAAMBXUIBRf2jyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFFGIAAK9XUGABGVEAW2AAGVlgIAGQgVJgIJADYAOBUpBZYABRWVJgAFJgAPNbYACAUmAA81tZWWAgAZCBUmAgkANgABlZYCABkIFSYCCQA2ADgVKBUpBWW2AgAVFRWVCAkVBQgJBQkFZbUFCCkVBQYgAAjFaqo6kicb_+QPvRgGgeN05+tJcdqKtrzpqKaGf7e7wSc3ARZ/hNSgeuHcoXLn5Avv5ASqgaPJnYzj/UIg5q6R3Se/6i+h+8oTyB/s9mZhwHNU4h8WEbWFpbrjAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKD//////////////////////////////////////////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAuEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+QHLoLnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqhGluaXS4YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7kBQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEA//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///////////////////////////////////////////uMxiAABkYgAAhJGAgIBRf7nJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqFGIAAMBXUIBRf2jyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFFGIAAK9XUGABGVEAW2AAGVlgIAGQgVJgIJADYAOBUpBZYABRWVJgAFJgAPNbYACAUmAA81tZWWAgAZCBUmAgkANgABlZYCABkIFSYCCQA2ADgVKBUpBWW2AgAVFRWVCAkVBQgJBQkFZbUFCCkVBQYgAAjFaqo6ki";

		String contractByteCode = "cb_+QP1RgKgpVq1Ib2r2ug+UktHvfWSQ8P35HJQHM6qikqBu1DwgtT5Avv5ASqgaPJnYzj/UIg5q6R3Se/6i+h+8oTyB/s9mZhwHNU4h8WEbWFpbrjAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKD//////////////////////////////////////////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAuEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+QHLoLnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqhGluaXS4YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7kBQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEA//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///////////////////////////////////////////uMxiAABkYgAAhJGAgIBRf7nJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqFGIAAMBXUIBRf2jyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFFGIAAK9XUGABGVEAW2AAGVlgIAGQgVJgIJADYAOBUpBZYABRWVJgAFJgAPNbYACAUmAA81tZWWAgAZCBUmAgkANgABlZYCABkIFSYCCQA2ADgVKBUpBWW2AgAVFRWVCAkVBQgJBQkFZbUFCCkVBQYgAAjFaFMy4wLjDZUhWH";

		// String contractCallData =
		// "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACC5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAnHQYrA==";

//		String contractCallData = "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACC5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAnHQYrA==";

		String contractCallData = "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACC5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAnHQYrA==";

		// get the currents accounts nonce in case a transaction is already
		// created and increase it by one
		Single<Account> acc = accountService.getAccount(kp.getPublicKey());

		acc.subscribe(account -> {
			String ownerId = kp.getPublicKey();

			BigInteger nonce = account.getNonce().add(BigInteger.ONE);
			System.out.println("Next nonce:			" + nonce);
			AbstractTransaction<?> contractTx = transactionServiceNative.getTransactionFactory()
					.createContractCreateTransaction(BigInteger.ONE, BigInteger.ZERO, contractCallData,
							contractByteCode, BigInteger.ZERO, new BigInteger("1000"), new BigInteger("1100000000"),
							nonce, ownerId, new BigInteger("20000"), new BigInteger("4"));

			System.out.println("ContractTx:			" + contractTx);
			System.out.println("Native Service Params:		" + transactionServiceNative);
			System.out.println("Debug Service Params:		" + transactionServiceDebug);

			UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(contractTx).toFuture()
					.get();

			Single<UnsignedTx> unsignedTxDebug = transactionServiceNative.createUnsignedTransaction(contractTx);
			unsignedTxDebug.subscribe(it -> {
				System.out.println("UnsignedNative: 	" + unsignedTxNative);
				System.out.println("UnsignedDebug: 	" + it);

				Assertions.assertEquals(it, unsignedTxNative);

				// async.complete();

				Tx signedTxNative = transactionServiceNative.signTransaction(unsignedTxNative, kp.getPrivateKey());
				System.out.println("SignedNative: 		" + signedTxNative);

				Tx signedTxDebug = transactionServiceDebug.signTransaction(it, kp.getPrivateKey());
				System.out.println("SignedDebug: 		" + signedTxDebug);

				byte[] networkData = Network.DEVNET.getId().getBytes(StandardCharsets.UTF_8);
				byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier(unsignedTxNative.getTx());
				byte[] txAndNetwork = ByteUtils.concatenate(networkData, binaryTx);
				byte[] sig = SigningUtil.sign(txAndNetwork, kp.getPrivateKey());

				System.out.println("Using signature: " + EncodingUtils.encodeCheck(sig, ApiIdentifiers.SIGNATURE));

				boolean verified = SigningUtil.verify(txAndNetwork, sig,
						Hex.toHexString(EncodingUtils.decodeCheckWithIdentifier(kp.getPublicKey())));
				System.out.println("Verify of transaction: " + verified);

				Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTxDebug);
				txResponse.subscribe(it2 -> {
					System.out.println("done");
					async.complete();
				}, throwable -> {
					System.out.println("error occured:");
					throwable.printStackTrace();
					context.fail();
				});
			}, throwable -> {
				throwable.printStackTrace();
				context.fail();
			});
		});
	}

	@Test
	@Ignore
	public void postSpendTxTest(TestContext context) throws ExecutionException, InterruptedException, CryptoException {
		Async async = context.async();

		BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);

		// get the currents accounts nonce in case a transaction is already
		// created and increase it by one
		Single<Account> acc = accountService.getAccount(keyPair.getPublicKey());
		acc.subscribe(account -> {
			BaseKeyPair kp = keyPairService.generateBaseKeyPair();
			String recipient = kp.getPublicKey();
			BigInteger amount = new BigInteger("1000000000000000000");
			String payload = "payload";
			BigInteger ttl = BigInteger.valueOf(20000);
			BigInteger nonce = account.getNonce().add(BigInteger.ONE);
			System.out.println("Next nonce" + nonce);
			AbstractTransaction<?> spendTx = transactionServiceNative.getTransactionFactory()
					.createSpendTransaction(keyPair.getPublicKey(), recipient, amount, payload, null, ttl, nonce);
			UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(spendTx).toFuture().get();
			Tx signedTx = transactionServiceNative.signTransaction(unsignedTxNative, keyPair.getPrivateKey());
			System.out.println(signedTx);

			byte[] networkData = Network.DEVNET.getId().getBytes(StandardCharsets.UTF_8);
			byte[] binaryTx = EncodingUtils.decodeCheckWithIdentifier(unsignedTxNative.getTx());
			byte[] txAndNetwork = ByteUtils.concatenate(networkData, binaryTx);
			byte[] sig = SigningUtil.sign(txAndNetwork, keyPair.getPrivateKey());

			System.out.println(EncodingUtils.encodeCheck(sig, ApiIdentifiers.SIGNATURE));

			boolean verified = SigningUtil.verify(txAndNetwork, sig,
					Hex.toHexString(EncodingUtils.decodeCheckWithIdentifier(keyPair.getPublicKey())));
			System.out.println("ver " + verified);

			Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTx);
			txResponse.subscribe(it -> {
				Assertions.assertEquals(it.getTxHash(), transactionServiceNative.computeTxHash(signedTx.getTx()));

				async.complete();
			}, throwable -> {
				context.fail();
			});
		}, throwable -> {
			context.fail();
		});
	}

	@Test
	@Ignore
	public void postContractTxTest(TestContext context)
			throws ExecutionException, InterruptedException, CryptoException {
		Async async = context.async();

		BaseKeyPair kp = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);

		String contractByteCode = "cb_+QP1RgKgeN05+tJcdqKtrzpqKaGf7e7wSc3ARZ/hNSgeuHcoXLn5Avv5ASqgaPJnYzj/UIg5q6R3Se/6i+h+8oTyB/s9mZhwHNU4h8WEbWFpbrjAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKD//////////////////////////////////////////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAuEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+QHLoLnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqhGluaXS4YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7kBQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEA//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///////////////////////////////////////////uMxiAABkYgAAhJGAgIBRf7nJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqFGIAAMBXUIBRf2jyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFFGIAAK9XUGABGVEAW2AAGVlgIAGQgVJgIJADYAOBUpBZYABRWVJgAFJgAPNbYACAUmAA81tZWWAgAZCBUmAgkANgABlZYCABkIFSYCCQA2ADgVKBUpBWW2AgAVFRWVCAkVBQgJBQkFZbUFCCkVBQYgAAjFaFMi4xLjCUisYH";

		String contractCallData = "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACC5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAnHQYrA==";

		// String pk =
		// "a7a695f999b1872acb13d5b63a830a8ee060ba688a478a08c6e65dfad8a01cd70bb4ed7927f97b51e1bcb5e1340d12335b2a2b12c8bc5221d63c4bcb39d41e61";

		// String pubK = "ak_6A2vcm1Sz6aqJezkLCssUXcyZTX7X8D5UwbuS2fRJr9KkYpRU";

		// get the currents accounts nonce in case a transaction is already
		// created and increase it by one
		Single<Account> acc = accountService.getAccount(kp.getPublicKey());
		System.out.println("get acc");
		// Single<Account> acc = accountService.getAccount(pk);
		acc.subscribe(account -> {
			System.out.println("Einstieg");
			String ownerId = kp.getPublicKey();
			// String ownerId = pubK;
			BigInteger amount = BigInteger.valueOf(1);
			BigInteger ttl = BigInteger.valueOf(20000);

			BigInteger nonce = account.getNonce().add(BigInteger.ONE);
			// BigInteger nonce = new BigInteger("11807");
			System.out.println("Next nonce" + nonce);
			AbstractTransaction<?> contractTx = null;
			// AbstractTransaction<?> contractTx =
			// transactionServiceNative
			// .getTransactionFactory()
			// .createContractCreateTransaction(
			// 1,
			// 0,
			// contractCallData,
			// contractByteCode,
			// 0,
			// 500,
			// 50,
			// nonce.intValue(),
			// ownerId,
			// 0,
			// 3);

			System.out.println(contractTx);
			System.out.println(transactionServiceNative);

			UnsignedTx unsignedTxNative = transactionServiceNative.createUnsignedTransaction(contractTx).toFuture()
					.get();
			System.out.println(unsignedTxNative);
			Tx signedTx = transactionServiceNative.signTransaction(unsignedTxNative, kp.getPrivateKey());
			// Tx signedTx = transactionServiceNative.signTransaction(unsignedTxNative, pk);

			// signedTx.setTx(
			//
			// "tx_+QTiCwH4QrhAishbFS9zXWJ/oESFpT83K0q8nYJFWaQrf7vaPxGPLq9VSLktCVircLa55sx/iEwg0jWidVSea/9kGO5odG3DB7kEmfkElioBoQELtO15J/l7UeG8teE0DRIzWyorEsi8UiHWPEvLOdQeYYIi3rkD8vkD70YBoHjdOfrSXHaira86aimhn+3u8EnNwEWf4TUoHrh3KFy5+QL7+QEqoGjyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFhG1haW64wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACg//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPkBy6C5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6oRpbml0uGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//////////////////////////////////////////+5AUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAP//////////////////////////////////////////AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7jMYgAAZGIAAISRgICAUX+5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6hRiAADAV1CAUX9o8mdjOP9QiDmrpHdJ7/qL6H7yhPIH+z2ZmHAc1TiHxRRiAACvV1BgARlRAFtgABlZYCABkIFSYCCQA2ADgVKQWWAAUVlSYABSYADzW2AAgFJgAPNbWVlgIAGQgVJgIJADYAAZWWAgAZCBUmAgkANgA4FSgVKQVltgIAFRUVlQgJFQUICQUJBWW1BQgpFQUGIAAIxWgwMAAYcD5x3GeLgAAAAAgw9CQIQ7msoAuGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAILnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADRRFqR");
			// signedTx = new Tx();
			// signedTx.setTx(
			//
			// "tx_+QToCwH4QrhARJzzqUAyyLx5Igq0gmSLJLNqPgPnbGCu7sRZva62/qDyP6zVnplueLIkHStkOOcXjVZO7bYc8hvsm5EAJhQHDLkEn/kEnCoBoQELtO15J/l7UeG8teE0DRIzWyorEsi8UiHWPEvLOdQeYYIuHrkD+PkD9UYCoHjdOfrSXHaira86aimhn+3u8EnNwEWf4TUoHrh3KFy5+QL7+QEqoGjyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFhG1haW64wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACg//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPkBy6C5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6oRpbml0uGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//////////////////////////////////////////+5AUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAP//////////////////////////////////////////AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7jMYgAAZGIAAISRgICAUX+5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6hRiAADAV1CAUX9o8mdjOP9QiDmrpHdJ7/qL6H7yhPIH+z2ZmHAc1TiHxRRiAACvV1BgARlRAFtgABlZYCABkIFSYCCQA2ADgVKQWWAAUVlSYABSYADzW2AAgFJgAPNbWVlgIAGQgVJgIJADYAAZWWAgAZCBUmAgkANgA4FSgVKQVltgIAFRUVlQgJFQUICQUJBWW1BQgpFQUGIAAIxWhTIuMS4wgwQAAYcD5zm3B2gAAAAAgw9CQIQ7msoAuGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAILnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABC3oKa");
			System.out.println(signedTx);
			Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTx);
			txResponse.subscribe(it -> {
				System.out.println(it.getTxHash());

				async.complete();
			}, throwable -> {
				throwable.printStackTrace();
				context.fail();
			});
		}, throwable -> {
			throwable.printStackTrace();
			context.fail();
		});
	}
}
