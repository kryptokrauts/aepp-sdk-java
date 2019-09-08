package com.kryptokrauts.aeternity.generated.api;

import org.junit.Test;

import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;

import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class InfoServiceTest extends BaseTest {

	@Test
	public void getCurrentKeyBlockTest(TestContext context) {
		Async async = context.async();
		Single<KeyBlockResult> keyBlockObservable = this.aeternityServiceNative.info.asyncGetCurrentKeyBlock();
		keyBlockObservable.subscribe(keyBlock -> {
			context.assertTrue(keyBlock.getHeight().longValue() > 0);
			context.assertFalse(keyBlock.getHeight().longValue() == 0);
			_logger.info("Height:" + keyBlock.getHeight());
			async.complete();
		}, throwable -> {
			context.fail(throwable);
		});
	}
}
