package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerServiceFactory;
import com.kryptokrauts.sophia.compiler.generated.model.ByteCode;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import javax.naming.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

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
          Assertions.assertEquals(bc.getBytecode(), TestConstants.testContractByteCode);
          async.complete();
        },
        throwable -> {
          _logger.error(TestConstants.errorOccured, throwable);
          context.fail();
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
          Assertions.assertEquals(cd.getCalldata(), TestConstants.testContractCallData);
          async.complete();
        },
        throwable -> {
          _logger.error(TestConstants.errorOccured, throwable);
          context.fail();
        });
  }
}
