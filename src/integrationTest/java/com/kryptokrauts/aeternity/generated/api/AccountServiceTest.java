package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.Optional;
import org.junit.Test;

public class AccountServiceTest extends BaseTest {

  @Test
  public void testBlockingGetAccount(TestContext context) {
    this.executeTest(
        context,
        t -> {
          AccountResult result =
              this.aeternityServiceNative.accounts.blockingGetAccount(
                  Optional.of(this.baseKeyPair.getPublicKey()));
          context.assertTrue(result.getBalance().compareTo(ZERO) == 1);
        });
  }

  @Test
  public void testAsyncGetAccountReactive(TestContext context) {
    this.executeTest(
        context,
        t -> {
          AccountResult result =
              this.aeternityServiceNative.accounts.blockingGetAccount(Optional.empty());
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
          AccountResult result =
              this.aeternityServiceNative.accounts.blockingGetAccount(Optional.empty());
          context.assertTrue(result.getBalance().compareTo(ZERO) == 1);
        });
  }

  @Test
  public void testBlockingGetAccountNextNonce(TestContext context) throws InterruptedException {
    this.executeTest(
        context,
        t -> {
          AccountResult result =
              this.aeternityServiceNative.accounts.blockingGetAccount(Optional.empty());
          BigInteger nextNonce =
              this.aeternityServiceNative.accounts.blockingGetNextBaseKeypairNonce(
                  Optional.empty());
          context.assertTrue(result.getNonce().add(ONE).intValue() == nextNonce.intValue());
        });
  }
}
