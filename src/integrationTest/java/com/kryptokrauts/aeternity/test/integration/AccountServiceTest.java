package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import org.junit.Test;

public class AccountServiceTest extends BaseTest {

  @Test
  public void testBlockingGetAccount(TestContext context) {
    this.executeTest(
        context,
        t -> {
          AccountResult result =
              this.aeternityService.accounts.blockingGetAccount(this.keyPair.getAddress());
          context.assertTrue(result.getBalance().compareTo(ZERO) == 1);
        });
  }

  @Test
  public void testAsyncGetAccountReactive(TestContext context) {
    this.executeTest(
        context,
        t -> {
          AccountResult result = this.aeternityService.accounts.blockingGetAccount();
          context.assertTrue(result.getBalance().compareTo(ZERO) == 1);
        });
  }

  @Test
  public void testAsyncGetAccountProcedural(TestContext context) {
    this.executeTest(
        context,
        t -> {
          AccountResult account = getAccount(null);
          context.assertTrue(account.getBalance().compareTo(ZERO) == 1);
        });
  }

  @Test
  public void testBlockingGetAccountWithKPFromConfig(TestContext context)
      throws InterruptedException {
    this.executeTest(
        context,
        t -> {
          AccountResult result = this.aeternityService.accounts.blockingGetAccount();
          context.assertTrue(result.getBalance().compareTo(ZERO) == 1);
        });
  }

  @Test
  public void testBlockingGetAccountNextNonce(TestContext context) throws InterruptedException {
    this.executeTest(
        context,
        t -> {
          AccountResult result = this.aeternityService.accounts.blockingGetAccount();
          BigInteger nextNonce = this.aeternityService.accounts.blockingGetNextNonce();
          context.assertTrue(result.getNonce().add(ONE).intValue() == nextNonce.intValue());
        });
  }
}
