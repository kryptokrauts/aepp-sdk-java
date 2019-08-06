package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerServiceFactory;
import com.kryptokrauts.sophia.compiler.generated.model.ACI;
import com.kryptokrauts.sophia.compiler.generated.model.ByteCode;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.naming.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class CompilerServiceTest extends BaseTest {

  protected CompilerService sophiaCompilerService;

  @Before
  public void initCompilerService() throws ConfigurationException {
    if (sophiaCompilerService == null) {
      Vertx vertx = rule.vertx();
      sophiaCompilerService =
          new CompilerServiceFactory()
              .getService(
                  ServiceConfiguration.configure()
                      .contractBaseUrl(getCompilerBaseUrl())
                      .vertx(vertx)
                      .compile());
    }
  }

  @Test
  public void testCompileContract(TestContext context) {
    Async async = context.async();
    Single<ByteCode> byteCode =
        this.sophiaCompilerService.compile(TestConstants.testContractSourceCode, null, null);
    byteCode.subscribe(
        bc -> {
          _logger.info(bc.getBytecode());
          context.assertEquals(TestConstants.testContractByteCode, bc.getBytecode());
          async.complete();
        },
        throwable -> {
          context.fail(throwable);
        });
  }

  @Test
  public void testCompileContractCall(TestContext context) {
    Async async = context.async();
    Single<Calldata> calldata =
        this.sophiaCompilerService.encodeCalldata(
            TestConstants.testContractSourceCode,
            TestConstants.testContractFunction,
            TestConstants.testContractFunctionParams);
    calldata.subscribe(
        cd -> {
          context.assertEquals(TestConstants.encodedServiceCall, cd.getCalldata());
          async.complete();
        },
        throwable -> {
          context.fail(throwable);
        });
  }

  @Test
  public void testDecodeCalldata(TestContext context) {
    Async async = context.async();
    Single<SophiaJsonData> callData =
        this.sophiaCompilerService.decodeCalldata(TestConstants.encodedServiceCallAnswer, "int");
    callData.subscribe(
        cd -> {
          _logger.info(cd.getData().toString());
          context.assertEquals(TestConstants.serviceCallAnswerJSON, cd.getData().toString());
          async.complete();
        },
        throwable -> {
          context.fail(throwable);
        });
  }

  @Test
  public void testEncodeCalldata(TestContext context) {
    Async async = context.async();
    Single<Calldata> callData =
        this.sophiaCompilerService.encodeCalldata(
            TestConstants.testContractSourceCode, "init", null);
    callData.subscribe(
        cd -> {
          context.assertEquals(TestConstants.testContractCallData, cd.getCalldata());
          async.complete();
        },
        throwable -> {
          context.fail(throwable);
        });
  }

  @Test
  public void testGenerateACI(TestContext context) throws IOException {
    Async async = context.async();
    final InputStream inputStream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("contracts/PaymentSplitter.aes");
    String paymentSplitterSource = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
    Single<ACI> aci = this.sophiaCompilerService.generateACI(paymentSplitterSource);
    aci.subscribe(
        res -> {
          _logger.info(res.getEncodedAci().toString());
          context.assertEquals(TestConstants.paymentSplitterACI, res.getEncodedAci().toString());
          async.complete();
        },
        throwable -> {
          context.fail(throwable);
        });
  }
}
