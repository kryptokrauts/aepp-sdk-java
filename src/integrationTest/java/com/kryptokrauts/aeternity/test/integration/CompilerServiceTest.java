package com.kryptokrauts.aeternity.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.service.compiler.domain.ACIResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

public class CompilerServiceTest extends BaseTest {

  @Test
  public void testCompileContractCall(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String calldata =
              this.aeternityService
                  .compiler
                  .blockingEncodeCalldata(
                      TestConstants.testContractSourceCode,
                      TestConstants.testContractFunction,
                      TestConstants.testContractFunctionParams,
                      null)
                  .getResult();
          context.assertEquals(TestConstants.encodedServiceCall, calldata);
        });
  }

  @Test
  public void testDecodeCalldata(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            ObjectResultWrapper callData =
                this.aeternityService.compiler.blockingDecodeCalldata(
                    TestConstants.encodedServiceCallAnswer, "int");
            _logger.info(callData.getResult().toString());
            context.assertEquals(
                new ObjectMapper()
                    .readValue(TestConstants.serviceCallAnswerJSON, Map.class)
                    .get("value")
                    .toString(),
                JsonObject.mapFrom(callData.getResult()).getInteger("value").toString());
          } catch (IOException e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void testEncodeCalldata(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String calldata =
              this.aeternityService
                  .compiler
                  .blockingEncodeCalldata(TestConstants.testContractSourceCode, "init", null, null)
                  .getResult();
          context.assertEquals(TestConstants.testContractCallData, calldata);
        });
  }

  @Test
  public void testGenerateACI(TestContext context) throws IOException {
    this.executeTest(
        context,
        t -> {
          ACIResult aci =
              this.aeternityService.compiler.blockingGenerateACI(paymentSplitterSource, null, null);
          _logger.info(aci.getEncodedAci().toString());
          context.assertEquals(TestConstants.paymentSplitterACI, aci.getEncodedAci().toString());
        });
  }
}
