package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
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
  public void testPayingForTx(TestContext testContext) {
    this.executeTest(
        testContext,
        t -> {
          // funding delegation account for testng
          delegationKeypair = keyPairService.generateKeyPair();
          AccountResult account = this.aeternityServiceNative.accounts.blockingGetAccount();
          BigInteger amount = unitConversionService.toSmallestUnit(BigDecimal.valueOf(2));
          BigInteger nonce = account.getNonce().add(ONE);
          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(delegationKeypair.getAddress())
                  .amount(amount)
                  .nonce(nonce)
                  .build();
          aeternityServiceNative.transactions.blockingPostTransaction(spendTx);
          AccountResult delegationTestAccount =
              this.aeternityServiceNative.accounts.blockingGetAccount(
                  delegationKeypair.getAddress());
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
          // String unsignedInnerTx = aeternityServiceNative.transactions
          // .blockingCreateUnsignedTransaction(spendTx).getResult();
          // String signedInnerTx = aeternityServiceNative.transactions
          // .signInnerTransaction(unsignedInnerTx, delegationKeypair.getEncodedPrivateKey());
          PayingForTransactionModel payingForTx =
              PayingForTransactionModel.builder()
                  .payerId(account.getPublicKey())
                  .nonce(
                      this.aeternityServiceNative.accounts.blockingGetAccount().getNonce().add(ONE))
                  // .tx(signedInnerTx)
                  .innerTxModel(spendTx)
                  .privateKeyToSignerInnerTx(delegationKeypair.getEncodedPrivateKey())
                  .build();
          PostTransactionResult payingForTxResult =
              aeternityServiceNative.transactions.blockingPostTransaction(payingForTx);
          _logger.info("PayingForTx-Result: {}", payingForTxResult);
          delegationTestAccount =
              aeternityServiceNative.accounts.blockingGetAccount(delegationKeypair.getAddress());
          _logger.info("delegationTestAccount: {}", delegationTestAccount);
          testContext.assertEquals(ZERO, delegationTestAccount.getBalance());
        });
  }
}
