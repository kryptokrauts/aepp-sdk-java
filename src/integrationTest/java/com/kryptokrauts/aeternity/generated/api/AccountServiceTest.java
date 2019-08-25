package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Test;

import com.kryptokrauts.aeternity.sdk.service.domain.account.AccountResult;

import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class AccountServiceTest extends BaseTest {

	@Test
	public void testBlockingGetAccount(TestContext context) {
		AccountResult result = this.aeternityServiceNative.accounts
				.blockingGetAccount(Optional.of(this.baseKeyPair.getPublicKey()));
		context.assertTrue(result.getBalance().compareTo(BigInteger.ZERO) == 1);
	}

	@Test
	public void testAsyncGetAccountReactive(TestContext context) {
		Async async = context.async();
		Single<AccountResult> result = this.aeternityServiceNative.accounts.asyncGetAccount(Optional.empty());
		result.subscribe(resultObject -> {
			context.assertTrue(resultObject.getBalance().compareTo(BigInteger.ZERO) == 1);
			async.complete();
		});
		async.awaitSuccess(TEST_CASE_TIMEOUT_MILLIS);
	}

	@Test
	public void testAsyncGetAccountProcedural(TestContext context) {
		AccountResult account = getAccount(null, context);
		context.assertTrue(account.getBalance().compareTo(BigInteger.ZERO) == 1);
	}

	@Test
	public void testBlockingGetAccountWithKPFromConfig(TestContext context) throws InterruptedException {
		AccountResult result = this.aeternityServiceNative.accounts.blockingGetAccount(Optional.empty());
		context.assertTrue(result.getBalance().compareTo(BigInteger.ZERO) == 1);
	}
}
