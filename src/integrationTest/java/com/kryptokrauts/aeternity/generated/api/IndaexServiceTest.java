package com.kryptokrauts.aeternity.generated.api;

import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class IndaexServiceTest extends BaseTest {

  @Test
  public void testStatus(TestContext context) {
    this.executeTest(
        context,
        t -> {
          //          Object result =
          // this.aeternityServiceNative.aeternal.blockingGetMdwStatus().getResult();
          //          try {
          //            _logger.info("aeternal status: {}",
          // objectMapper.writeValueAsString(result));
          //          } catch (JsonProcessingException e) {
          //            context.fail(e);
          //          }
        });
  }

  @Test
  public void testActiveAuctions(TestContext context) {
    this.executeTest(
        context,
        t -> {
          //          ActiveNameAuctionsResult result =
          //              this.aeternityServiceNative.aeternal.blockingGetActiveNameAuctions();
          //          _logger.info("active auctions: {}", result);
        });
  }
}
