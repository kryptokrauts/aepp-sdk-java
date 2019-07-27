package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Assert;
import org.junit.Test;

public class AccountServiceTest extends BaseTest {

  @Test
  public void testBlockingGetAccount(TestContext context) throws InterruptedException {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              AccountResult result =
                  this.accountService.blockingGetAccount(baseKeyPair.getPublicKey());
              Assert.assertTrue(result.getBalance().intValue() > 0);
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void testAsyncGetAccount(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                AccountResult result = getAccount(baseKeyPair.getPublicKey());
                Assert.assertTrue(result.getBalance().intValue() > 0);
              } catch (Throwable e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }
}
