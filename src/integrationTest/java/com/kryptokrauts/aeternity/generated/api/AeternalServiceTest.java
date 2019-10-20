package com.kryptokrauts.aeternity.generated.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.ext.unit.TestContext;
import java.util.Optional;
import org.junit.Ignore;
import org.junit.Test;

public class AeternalServiceTest extends BaseTest {

  @Test
  public void testStatus(TestContext context) {
    this.executeTest(
        context,
        t -> {
          Object result = this.aeternityServiceNative.aeternal.blockingGetStatus();
          try {
            _logger.info("aeternal status: {}", objectMapper.writeValueAsString(result));
          } catch (JsonProcessingException e) {
            context.fail(e);
          }
        });
  }

  @Test
  @Ignore // can be activated after switching to new aeternal release
  public void testActiveAuctions(TestContext context) {
    this.executeTest(
        context,
        t -> {
          Object result =
              this.aeternityServiceNative.aeternal.blockingGetNameAuctionsActive(
                  Optional.empty(),
                  Optional.empty(),
                  Optional.empty(),
                  Optional.empty(),
                  Optional.empty());
          try {
            _logger.info("active auctions: {}", objectMapper.writeValueAsString(result));
          } catch (JsonProcessingException e) {
            context.fail(e);
          }
        });
  }
}
