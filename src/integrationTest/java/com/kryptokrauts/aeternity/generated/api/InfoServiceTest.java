package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class InfoServiceTest extends BaseTest {

  @Test
  public void getCurrentKeyBlockTest(TestContext context) {
    Async async = context.async();
    Single<KeyBlock> keyBlockObservable =
        this.aeternityServiceNative.info.asyncGetCurrentKeyBlock();
    keyBlockObservable.subscribe(
        keyBlock -> {
          context.assertTrue(keyBlock.getHeight().longValue() > 0);
          async.complete();
        },
        throwable -> {
          context.fail(throwable);
        });
  }
}
