package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class InfoServiceTest extends BaseTest {

  @Test
  public void getCurrentKeyBlockTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          KeyBlockResult keyBlock = this.aeternityService.info.blockingGetCurrentKeyBlock();
          context.assertTrue(keyBlock.getHeight().longValue() > 0);
          context.assertFalse(keyBlock.getHeight().longValue() == 0);
          _logger.info("Height:" + keyBlock.getHeight());
        });
  }
}
