package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.service.indaex.domain.StatusResult;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class IndaexServiceTest extends BaseTest {

  @Test
  public void testStatus(TestContext context) {
    this.executeTest(
        context,
        t -> {
          StatusResult statusResult = this.aeternityServiceNative.indaex.blockingGetStatus();
          _logger.info(statusResult.toString());
          context.assertEquals("5.7.1", statusResult.getNodeVersion());
          context.assertEquals("1.0.2", statusResult.getMdwVersion());
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
