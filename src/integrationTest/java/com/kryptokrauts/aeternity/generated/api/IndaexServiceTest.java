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
          context.assertEquals("5.10.1", statusResult.getNodeVersion());
          context.assertEquals("1.0.3", statusResult.getMdwVersion()); // v1.0.4 returns 1.0.3 :-(
        });
  }
}
