package com.kryptokrauts.aeternity.generated.api;

import javax.naming.ConfigurationException;

import org.junit.Before;
import org.junit.Test;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerServiceFactory;
import com.kryptokrauts.sophia.compiler.generated.model.ByteCode;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;

import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class CompilerServiceTest extends BaseTest {

	protected CompilerService sophiaCompilerService;

	@Before
	public void initCompilerService() throws ConfigurationException {
		if (sophiaCompilerService == null) {
			Vertx vertx = rule.vertx();
			sophiaCompilerService = new CompilerServiceFactory().getService(
					ServiceConfiguration.configure().contractBaseUrl(getCompilerBaseUrl()).vertx(vertx).compile());
		}
	}

	@Test
	public void testCompileContract(TestContext context) {
		Async async = context.async();
		Single<ByteCode> byteCode = this.sophiaCompilerService.compile(TestConstants.testContractSourceCode, null,
				null);
		byteCode.subscribe(bc -> {
			context.assertEquals(bc.getBytecode(), TestConstants.testContractByteCode);
			async.complete();
		}, throwable -> {
			_logger.error(TestConstants.errorOccured, throwable);
			context.fail();
		});
	}

	@Test
	public void testCompileContractCall(TestContext context) {
		Async async = context.async();
		Single<Calldata> calldata = this.sophiaCompilerService.encodeCalldata(TestConstants.testContractSourceCode,
				TestConstants.testContractFunction, TestConstants.testContractFunctionParams);
		calldata.subscribe(cd -> {
			context.assertEquals(cd.getCalldata(), TestConstants.encodedServiceCall);
			async.complete();
		}, throwable -> {
			_logger.error(TestConstants.errorOccured, throwable);
			context.fail();
		});
	}

	@Test
	public void testDecodeCalldata(TestContext context) {
		Async async = context.async();
		Single<SophiaJsonData> callData = this.sophiaCompilerService
				.decodeCalldata(TestConstants.encodedServiceCallAnswer, "int");
		callData.subscribe(cd -> {
			_logger.info(cd.getData().toString());
			context.assertEquals(cd.getData().toString(), TestConstants.serviceCallAnswerJSON);
			async.complete();
		}, throwable -> {
			_logger.error(TestConstants.errorOccured, throwable);
			context.fail();
		});
	}

	@Test
	public void testEncodeCalldata(TestContext context) {
		Async async = context.async();
		Single<Calldata> callData = this.sophiaCompilerService.encodeCalldata(TestConstants.testContractSourceCode,
				"init", null);
		callData.subscribe(cd -> {
			context.assertEquals(cd.getCalldata(), TestConstants.testContractCallData);
			async.complete();
		}, throwable -> {
			_logger.error(TestConstants.errorOccured, throwable);
			context.fail();
		});
	}
}
