package com.kryptokrauts.aeternity.generated.epoch.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.kryptokrauts.aeternity.generated.epoch.model.KeyBlock;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainServiceFactory;

import io.reactivex.Observable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class ChainApiTest extends BaseTest {

    @Test
    public void getCurrentKeyBlockTest( TestContext context ) {
        Async async = context.async();
        Observable<KeyBlock> keyBlockObservable = chainService.getCurrentKeyBlock();
        keyBlockObservable.subscribe( keyBlock -> {
            System.out.println( keyBlock.toString() );
            Assertions.assertTrue( keyBlock.getHeight().longValue() > 0 );
            async.complete();
        }, failure -> {
            failure.printStackTrace();
            context.fail();
        } );
    }

    @Test( expected = NullPointerException.class )
    public void testNullConfig() {
        new ChainServiceFactory().getService( null );
    }

    @Test
    public void getDefaultChainService() {
        Assert.assertNotNull( new ChainServiceFactory().getService() );
    }
}
