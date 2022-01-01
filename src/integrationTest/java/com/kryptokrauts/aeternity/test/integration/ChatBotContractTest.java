package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaString;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxResult;
import io.vertx.ext.unit.TestContext;
import java.util.List;
import javax.naming.ConfigurationException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ChatBotContractTest extends BaseTest {

  static AeternityService readOnlyService;
  static AeternityService wrongConfiguredReadOnlyService;

  static String contractId;
  static Object readOnlyResult;

  @Override
  public void setupTestEnv(TestContext context) throws ConfigurationException {
    super.setupTestEnv(context);
    readOnlyService =
        new AeternityServiceFactory()
            .getService(
                AeternityServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .network(Network.DEVNET)
                    .vertx(vertx)
                    .compile());
    wrongConfiguredReadOnlyService =
        new AeternityServiceFactory()
            .getService(
                AeternityServiceConfiguration.configure()
                    .baseUrl("http://does-not.exist")
                    .network(Network.DEVNET)
                    .vertx(vertx)
                    .compile());
    this.executeTest(
        context,
        t -> {
          ContractTxResult contractTxResult =
              aeternityService.transactions.blockingContractCreate(chatBotSource);
          contractId = contractTxResult.getCallResult().getContractId();
        });
  }

  @Test
  public void greetKryptokrautsTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          readOnlyResult =
              readOnlyService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "greet",
                  chatBotSource,
                  ContractTxOptions.builder()
                      .params(List.of(new SophiaString("kryptokrauts")))
                      .build());
          context.assertEquals("Hello, kryptokrauts", readOnlyResult);
        });
  }

  @Test
  public void failedDryRunTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          Assertions.assertThrows(
              AException.class,
              () ->
                  wrongConfiguredReadOnlyService.transactions.blockingReadOnlyContractCall(
                      contractId,
                      "greet",
                      chatBotSource,
                      ContractTxOptions.builder()
                          .params(List.of(new SophiaString("kryptokrauts")))
                          .build()));
        });
  }
}
