package com.kryptokrauts.aeternity.test.integration.ignore;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class ChainApiTestJunit5 extends BaseTestJunit5 {

  @Test
  public void getCurrentKeyBlockTest(VertxTestContext testContext) {
    getChainApi()
        .getCurrentKeyBlock(
            false,
            testContext.succeeding(
                keyBlock -> {
                  testContext.verify(
                      () -> {
                        _logger.info(keyBlock.toString());
                        assertTrue(keyBlock.getHeight().longValue() > 0);
                        testContext.completeNow();
                      });
                }));
  }

  @Test
  public void getCurrentKeyBlockTest2(Vertx vertx, VertxTestContext context) {
    context.completeNow();
    getChainApi()
        .getCurrentKeyBlock(
            false,
            res -> {
              if (res.failed()) {
                fail();
              }
              _logger.info(res.result().toString());
              assertTrue(res.result().getHeight().longValue() > 0);
            });
  }
}
