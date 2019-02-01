package com.kryptokrauts.aeternity.generated.api.ignore;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ChainApiTestJunit5 extends BaseTestJunit5 {

    @Test
    public void getCurrentKeyBlockTest(VertxTestContext testContext) {
        getChainApi().getCurrentKeyBlock(testContext.succeeding(keyBlock -> {
            testContext.verify(() -> {
                System.out.println(keyBlock.toString());
                assertTrue(keyBlock.getHeight().longValue() > 0);
                testContext.completeNow();
            });
        }));
    }

    @Test
    public void getCurrentKeyBlockTest2(Vertx vertx, VertxTestContext context) {
        context.completeNow();
        getChainApi().getCurrentKeyBlock(res -> {
            if (res.failed()) {
                fail();
            }
            System.out.println(res.result().toString());
            assertTrue(res.result().getHeight().longValue() > 0);
        });
    }
}
