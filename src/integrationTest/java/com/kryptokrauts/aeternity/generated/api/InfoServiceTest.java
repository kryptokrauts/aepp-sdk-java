package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class InfoServiceTest extends BaseTest {

  @Test
  public void getCurrentKeyBlockTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          KeyBlockResult keyBlock = this.aeternityServiceNative.info.blockingGetCurrentKeyBlock();
          context.assertTrue(keyBlock.getHeight().longValue() > 0);
          context.assertFalse(keyBlock.getHeight().longValue() == 0);
          _logger.info("Height:" + keyBlock.getHeight());
        });
  }
}
