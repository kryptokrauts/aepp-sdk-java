package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.service.mdw.domain.StatusResult;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class MiddlewareServiceTest extends BaseTest {

  @Test
  public void testStatus(TestContext context) {
    this.executeTest(
        context,
        t -> {
          StatusResult statusResult = this.aeternityService.mdw.blockingGetStatus();
          _logger.info(statusResult.toString());
          context.assertEquals("6.3.0", statusResult.getNodeVersion());
          context.assertEquals("1.3.0", statusResult.getMdwVersion());
        });
  }
}
