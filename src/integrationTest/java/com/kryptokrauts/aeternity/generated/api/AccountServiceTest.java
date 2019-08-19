package com.kryptokrauts.aeternity.generated.api;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class AccountServiceTest extends BaseTest {

	@Test
	public void testBlockingGetAccount(TestContext context) throws InterruptedException {
		Async async = context.async();
		BaseKeyPair randomKeyPair = this.keyPairService.generateBaseKeyPair();
		rule.vertx().executeBlocking(future -> {
			AccountResult result = this.aeternityServiceNative.accounts
					.blockingGetAccount(Optional.of(randomKeyPair.getPublicKey()));
			System.out.println("------------------ Result is " + (result.getBalance().intValue() > 0));
			Assert.assertTrue(result.getBalance().intValue() > 0);
			future.complete();
		}, success -> async.complete());
	}

	@Test
	public void testBlockingGetAccountWithKPFromConfig(TestContext context) throws InterruptedException {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			AccountResult result = this.accountService.blockingGetAccount(Optional.empty());
			Assert.assertTrue(result.getBalance().intValue() > 0);
			future.complete();
		}, success -> async.complete());
	}

	@Test
	@Ignore
	public void testAsyncGetAccount(TestContext context) {
		Async async = context.async();
		rule.vertx().executeBlocking(future -> {
			try {
				AccountResult result = getAccount(baseKeyPair.getPublicKey());
				Assert.assertTrue(result.getBalance().intValue() > 0);
			} catch (Throwable e) {
				context.fail(e);
			}
			future.complete();
		}, success -> async.complete());
	}
}
