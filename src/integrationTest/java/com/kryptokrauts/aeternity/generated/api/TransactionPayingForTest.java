package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.PayingForTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.unit.UnitConversionService;
import com.kryptokrauts.aeternity.sdk.service.unit.impl.DefaultUnitConversionServiceImpl;
import io.vertx.ext.unit.TestContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Ignore;
import org.junit.Test;

public class TransactionPayingForTest extends BaseTest {

  static BaseKeyPair delegationKeypair;
  static UnitConversionService unitConversionService = new DefaultUnitConversionServiceImpl();

  @Test
  @Ignore // currently failing due to invalid signature check
  public void testPayingForTx(TestContext testContext) {
    this.executeTest(
        testContext,
        t -> {
          // funding delegation account for testng
          delegationKeypair = keyPairService.generateBaseKeyPair();
          AccountResult account = this.aeternityServiceNative.accounts.blockingGetAccount();
          BigInteger amount = unitConversionService.toSmallestUnit(BigDecimal.valueOf(2));
          BigInteger nonce = account.getNonce().add(ONE);
          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(delegationKeypair.getPublicKey())
                  .amount(amount)
                  .ttl(ZERO)
                  .nonce(nonce)
                  .build();
          aeternityServiceNative.transactions.blockingPostTransaction(spendTx);
          AccountResult delegationTestAccount =
              this.aeternityServiceNative.accounts.blockingGetAccount(
                  delegationKeypair.getPublicKey());
          _logger.info("account: {}", delegationTestAccount);

          // execute PayingForTx on behalf of delegation account
          BaseKeyPair anotherKeypair = keyPairService.generateBaseKeyPair();
          spendTx =
              SpendTransactionModel.builder()
                  .sender(delegationKeypair.getPublicKey())
                  .recipient(anotherKeypair.getPublicKey())
                  .amount(amount)
                  .ttl(ZERO)
                  .nonce(delegationTestAccount.getNonce().add(BigInteger.ONE))
                  .build();
          String unsignedInnerTx =
              aeternityServiceNative
                  .transactions
                  .blockingCreateUnsignedTransaction(spendTx)
                  .getResult();
          String signedInnerTx =
              aeternityServiceNative.transactions.signTransaction(
                  unsignedInnerTx, delegationKeypair.getPrivateKey());
          PayingForTransactionModel payingForTx =
              PayingForTransactionModel.builder()
                  .payerId(account.getPublicKey())
                  .nonce(
                      this.aeternityServiceNative
                          .accounts
                          .blockingGetAccount()
                          .getNonce()
                          .add(BigInteger.ONE))
                  .tx(signedInnerTx)
                  .build();
          PostTransactionResult payingForTxResult =
              aeternityServiceNative.transactions.blockingPostTransaction(payingForTx);
          _logger.info("PayingForTx-Result: {}", payingForTxResult);
        });
  }
}
