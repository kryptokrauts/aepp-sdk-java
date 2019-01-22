package com.kryptokrauts.aeternity.generated.epoch.api;

import com.kryptokrauts.aeternity.generated.epoch.model.KeyBlock;
import io.reactivex.Observable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ChainApiTest extends BaseTest {

    @Test
    public void getCurrentKeyBlockTest(TestContext context) {
        Async async = context.async();
        Observable<KeyBlock> keyBlockObservable = chainService.getCurrentKeyBlock();
        keyBlockObservable.subscribe(
                keyBlock -> {
                    System.out.println(keyBlock.toString());
                    Assertions.assertTrue(keyBlock.getHeight().longValue() > 0);
                    async.complete();
                },
                failure -> {
                    failure.printStackTrace();
                    context.fail();
                }
        );
    }
}
