package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.PayingForTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.unit.UnitConversionService;
import com.kryptokrauts.aeternity.sdk.service.unit.impl.DefaultUnitConversionServiceImpl;
import io.vertx.ext.unit.TestContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;

public class TransactionPayingForTest extends BaseTest {

  static KeyPair delegationKeypair;
  static UnitConversionService unitConversionService = new DefaultUnitConversionServiceImpl();

  @Test
  public void testPayingForTxWithSpendTx(TestContext testContext) {
    this.executeTest(
        testContext,
        t -> {
          // funding delegation account for testing
          delegationKeypair = keyPairService.generateKeyPair();
          AccountResult account = this.aeternityService.accounts.blockingGetAccount();
          BigInteger amount = unitConversionService.toSmallestUnit(BigDecimal.valueOf(2));
          BigInteger nonce = account.getNonce().add(ONE);
          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(delegationKeypair.getAddress())
                  .amount(amount)
                  .nonce(nonce)
                  .build();
          aeternityService.transactions.blockingPostTransaction(spendTx);
          AccountResult delegationTestAccount =
              this.aeternityService.accounts.blockingGetAccount(delegationKeypair.getAddress());
          _logger.info("delegationTestAccount: {}", delegationTestAccount);
          testContext.assertEquals(amount, delegationTestAccount.getBalance());

          // execute PayingForTx on behalf of delegation account
          KeyPair anotherKeypair = keyPairService.generateKeyPair();
          spendTx =
              SpendTransactionModel.builder()
                  .sender(delegationKeypair.getAddress())
                  .recipient(anotherKeypair.getAddress())
                  .amount(amount)
                  .payload("yeah, somebody else payed the tx fee for this transaction =)")
                  .nonce(delegationTestAccount.getNonce().add(ONE))
                  .build();

          PayingForTransactionModel payingForTx =
              PayingForTransactionModel.builder()
                  .payerId(account.getPublicKey())
                  .nonce(this.aeternityService.accounts.blockingGetAccount().getNonce().add(ONE))
                  .innerTx(
                      aeternityService.transactions.signPayingForInnerTransaction(
                          spendTx, delegationKeypair.getEncodedPrivateKey()))
                  .build();
          PostTransactionResult payingForTxResult =
              aeternityService.transactions.blockingPostTransaction(payingForTx);
          _logger.info("PayingForTx-Result: {}", payingForTxResult);
          delegationTestAccount =
              aeternityService.accounts.blockingGetAccount(delegationKeypair.getAddress());
          _logger.info("delegationTestAccount: {}", delegationTestAccount);
          testContext.assertEquals(ZERO, delegationTestAccount.getBalance());
        });
  }

  @Test
  public void testFailWithInnerTxPayingFor(TestContext testContext) {
    this.executeTest(
        testContext,
        t -> {
          delegationKeypair = keyPairService.generateKeyPair();
          AccountResult account = this.aeternityService.accounts.blockingGetAccount();
          PayingForTransactionModel payingForAsInnerTx =
              PayingForTransactionModel.builder()
                  .payerId(account.getPublicKey())
                  .nonce(this.aeternityService.accounts.blockingGetAccount().getNonce().add(ONE))
                  .build();
          try {
            aeternityService.transactions.signPayingForInnerTransaction(
                payingForAsInnerTx, delegationKeypair.getEncodedPrivateKey());
          } catch (Exception e) {
            testContext.assertEquals(e.getClass(), TransactionCreateException.class);
            testContext.assertTrue(
                e.getCause()
                    .getMessage()
                    .contains("Inner transaction of payingFor cannot be of type payingFor!"));
          }
        });
  }
}
