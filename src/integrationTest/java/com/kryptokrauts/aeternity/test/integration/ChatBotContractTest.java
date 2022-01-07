package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaString;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import io.vertx.ext.unit.TestContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
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
  public void greetTest(TestContext context) {
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
  public void greetAndRememberConvenienceTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          ContractTxResult contractTxResult =
              aeternityService.transactions.blockingStatefulContractCall(
                  contractId,
                  "greet_and_remember",
                  chatBotSource,
                  ContractTxOptions.builder()
                      .params(List.of(new SophiaString("kryptokrauts")))
                      .build());
          _logger.info(contractTxResult.toString());
          context.assertEquals("Hello, kryptokrauts", contractTxResult.getDecodedValue());
        });
  }

  @Test
  public void greetAndRememberExplicitTest(TestContext context) {
    this.executeTest(
        context,
        t -> {

          // obtain the calldata by calling the http compiler
          String callData =
              aeternityService
                  .compiler
                  .blockingEncodeCalldata(
                      chatBotSource, "greet_and_remember", Arrays.asList("\"kryptokrauts\""), null)
                  .getResult();

          // build the contract call tx model
          ContractCallTransactionModel contractCall =
              ContractCallTransactionModel.builder()
                  .callerId(aeternityService.keyPairAddress)
                  .contractId(contractId)
                  .callData(callData)
                  .nonce(aeternityService.accounts.blockingGetNextNonce())
                  .build();

          /**
           * optional: if you know that the default of 25000 is sufficient you don't need a dry-run
           * at all
           */
          DryRunTransactionResult dryRunResult =
              aeternityService.transactions.blockingDryRunContractTx(contractCall, false);
          /**
           * determine gasUsed via dry-run and add a margin to make sure the tx gets mined. ideally
           * you implement this as a one-time action and monitor gas usage over time. the margin is
           * not required but recommended. if the provided gasLimit is insufficient the tx will fail
           * and consumed gas will be payed anyway. so you can lose funds
           */
          BigInteger gasLimitWithMargin =
              new BigDecimal(dryRunResult.getContractCallObject().getGasUsed())
                  .multiply(new BigDecimal(1.5f))
                  .toBigInteger();
          // set the gasLimitWithMargin before broadcasting the transaction
          contractCall = contractCall.toBuilder().gasLimit(gasLimitWithMargin).build();

          // broadcast the tx
          PostTransactionResult txResult =
              aeternityService.transactions.blockingPostTransaction(contractCall);

          // obtain the tx-info
          TransactionInfoResult infoResult =
              aeternityService.info.blockingGetTransactionInfoByHash(txResult.getTxHash());

          // decode the return value by calling the http compiler
          ObjectResultWrapper resultWrapper =
              aeternityService.compiler.blockingDecodeCallResult(
                  chatBotSource,
                  "greet_and_remember",
                  infoResult.getCallInfo().getReturnType(),
                  infoResult.getCallInfo().getReturnValue(),
                  null);

          _logger.info(resultWrapper.getResult().toString()); // "Hello, kryptokrauts"

          context.assertEquals("Hello, kryptokrauts", resultWrapper.getResult());
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
