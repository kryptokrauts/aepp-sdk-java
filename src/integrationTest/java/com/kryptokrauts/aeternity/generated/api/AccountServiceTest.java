package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Test;

import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
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
		Single<AccountResult> accountSingle = aeternityServiceNative.accounts.asyncGetAccount(Optional.empty());
		TestObserver<AccountResult> accountTestObserver = accountSingle.test();
		accountTestObserver.awaitTerminalEvent();
		if (accountTestObserver.errorCount() > 0) {
			context.fail("Failed due to Exception " + accountTestObserver.errors().get(0));
		}
		AccountResult account = accountTestObserver.values().get(0);
		context.assertTrue(account.getBalance().compareTo(BigInteger.ZERO) == 1);
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
