package com.kryptokrauts.aeternity.test.integration;

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
          context.assertEquals("6.1.0", statusResult.getNodeVersion());
          context.assertEquals("1.0.8", statusResult.getMdwVersion());
        });
  }
}
