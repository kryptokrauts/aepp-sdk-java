package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxResult;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class StatefulContractTest extends BaseTest {

  @Test
  public void testStatefulConvenienceMethodsWithDifferentCallers(TestContext context) {
    this.executeTest(
        context,
        t -> {
          ContractTxResult txResult =
              aeternityService.transactions.blockingContractCreate(statefulTest);
          _logger.info("Deployment result with default keypair: {}", txResult);
          String contractIdDefaultKeyPair = txResult.getCallResult().getContractId();

          Object lastCaller =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractIdDefaultKeyPair, "get_last_caller", statefulTest);
          // expecting latest caller (contract creator) to be default keypair address
          context.assertEquals(aeternityService.keyPairAddress, lastCaller);

          KeyPair otherKeyPair = otherKeyPairs.get(0);
          txResult =
              aeternityService.transactions.blockingStatefulContractCall(
                  contractIdDefaultKeyPair,
                  "remember_caller",
                  statefulTest,
                  ContractTxOptions.builder().customKeyPair(otherKeyPair).build());
          _logger.info("Call result with other keypair: {}", txResult);
          lastCaller =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractIdDefaultKeyPair, "get_last_caller", statefulTest);
          // expecting latest caller to be address of other keypair
          context.assertEquals(otherKeyPair.getAddress(), lastCaller);

          txResult =
              aeternityService.transactions.blockingContractCreate(
                  statefulTest, ContractTxOptions.builder().customKeyPair(otherKeyPair).build());
          _logger.info("Deployment result with other keypair: {}", txResult);
          String contractIdOtherKeyPair = txResult.getCallResult().getContractId();

          lastCaller =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractIdOtherKeyPair, "get_last_caller", statefulTest);
          // expecting latest caller (contract creator) to be default keypair address
          context.assertEquals(otherKeyPair.getAddress(), lastCaller);

          txResult =
              aeternityService.transactions.blockingStatefulContractCall(
                  contractIdOtherKeyPair, "remember_caller", statefulTest);
          _logger.info("Call result with default keypair: {}", txResult);
          lastCaller =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractIdOtherKeyPair, "get_last_caller", statefulTest);
          // expecting latest caller to be address of default keypair
          context.assertEquals(aeternityService.keyPairAddress, lastCaller);
        });
  }
}
