package com.kryptokrauts.aeternity.generated.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternity.sdk.service.compiler.domain.ACIResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class CompilerServiceTest extends BaseTest {

  @Test
  public void testCompileContract(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String byteCode =
              this.aeternityServiceNative.compiler.blockingCompile(
                  TestConstants.testContractSourceCode, null, null);
          context.assertEquals(TestConstants.testContractByteCode, byteCode);
        });
  }

  @Test
  public void testCompileContractCall(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String calldata =
              this.aeternityServiceNative.compiler.blockingEncodeCalldata(
                  TestConstants.testContractSourceCode,
                  TestConstants.testContractFunction,
                  TestConstants.testContractFunctionParams);
          context.assertEquals(TestConstants.encodedServiceCall, calldata);
        });
  }

  @Test
  public void testDecodeCalldata(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            Object callData =
                this.aeternityServiceNative.compiler.blockingDecodeCalldata(
                    TestConstants.encodedServiceCallAnswer, "int");
            _logger.info(callData.toString());
            context.assertEquals(
                new ObjectMapper()
                    .readValue(TestConstants.serviceCallAnswerJSON, Map.class)
                    .get("value")
                    .toString(),
                JsonObject.mapFrom(callData).getInteger("value").toString());
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
              this.aeternityServiceNative.compiler.blockingEncodeCalldata(
                  TestConstants.testContractSourceCode, "init", null);
          context.assertEquals(TestConstants.testContractCallData, calldata);
        });
  }

  @Test
  public void testGenerateACI(TestContext context) throws IOException {
    this.executeTest(
        context,
        t -> {
          try {
            final InputStream inputStream =
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("contracts/PaymentSplitter.aes");
            String paymentSplitterSource =
                IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
            ACIResult aci =
                this.aeternityServiceNative.compiler.blockingGenerateACI(
                    paymentSplitterSource, null, null);
            _logger.info(aci.getEncodedAci().toString());
            context.assertEquals(TestConstants.paymentSplitterACI, aci.getEncodedAci().toString());
          } catch (IOException e) {
            context.fail(e);
          }
        });
  }
}
