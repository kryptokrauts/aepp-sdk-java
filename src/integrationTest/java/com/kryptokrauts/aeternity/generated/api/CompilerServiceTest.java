package com.kryptokrauts.aeternity.generated.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.sophia.compiler.generated.model.ACI;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
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
    String byteCode =
        this.aeternityServiceNative.compiler.blockingCompile(
            TestConstants.testContractSourceCode, null, null);
    context.assertEquals(TestConstants.testContractByteCode, byteCode);
  }

  @Test
  public void testCompileContractCall(TestContext context) {
    String calldata =
        this.aeternityServiceNative.compiler.blockingEncodeCalldata(
            TestConstants.testContractSourceCode,
            TestConstants.testContractFunction,
            TestConstants.testContractFunctionParams);
    context.assertEquals(TestConstants.encodedServiceCall, calldata);
  }

  @Test
  public void testDecodeCalldata(TestContext context) {
    Async async = context.async();
    Single<Object> callData =
        this.aeternityServiceNative.compiler.asyncDecodeCalldata(
            TestConstants.encodedServiceCallAnswer, "int");
    callData.subscribe(
        cd -> {
          _logger.info(cd.toString());
          context.assertEquals(
              new ObjectMapper()
                  .readValue(TestConstants.serviceCallAnswerJSON, Map.class)
                  .get("value")
                  .toString(),
              JsonObject.mapFrom(cd).getInteger("value").toString());
          async.complete();
        },
        throwable -> {
          context.fail(throwable);
        });
  }

  @Test
  public void testEncodeCalldata(TestContext context) {
    String calldata =
        this.aeternityServiceNative.compiler.blockingEncodeCalldata(
            TestConstants.testContractSourceCode, "init", null);
    context.assertEquals(TestConstants.testContractCallData, calldata);
  }

  @Test
  public void testGenerateACI(TestContext context) throws IOException {
    final InputStream inputStream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("contracts/PaymentSplitter.aes");
    String paymentSplitterSource = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
    ACI aci = this.aeternityServiceNative.compiler.blockingGenerateACI(paymentSplitterSource);
    _logger.info(aci.getEncodedAci().toString());
    context.assertEquals(TestConstants.paymentSplitterACI, aci.getEncodedAci().toString());
  }
}
