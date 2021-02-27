package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunAccountModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.GeneralizedAccountsAttachTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.unit.UnitConversionService;
import com.kryptokrauts.aeternity.sdk.service.unit.impl.DefaultUnitConversionServiceImpl;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.vertx.ext.unit.TestContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;

public class TransactionGeneralizedAccountsTest extends BaseTest {

  static BaseKeyPair gaTestKeyPair;
  static UnitConversionService unitConversionService = new DefaultUnitConversionServiceImpl();

  @Test
  public void testGaAttach(TestContext context) {
    this.executeTest(
        context,
        t -> {
          gaTestKeyPair = keyPairService.generateBaseKeyPair();
          AccountResult account =
              this.aeternityServiceNative.accounts.blockingGetAccount(Optional.empty());
          String recipient = gaTestKeyPair.getPublicKey();
          BigInteger amount = unitConversionService.toSmallestUnit(BigDecimal.TEN);
          BigInteger nonce = account.getNonce().add(ONE);
          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(recipient)
                  .amount(amount)
                  .payload("")
                  .ttl(ZERO)
                  .nonce(nonce)
                  .build();
          aeternityServiceNative.transactions.blockingPostTransaction(spendTx);
          AccountResult gaTestAccount =
              this.aeternityServiceNative.accounts.blockingGetAccount(
                  Optional.of(gaTestKeyPair.getPublicKey()));
          _logger.info("account: {}", gaTestAccount);
          context.assertEquals("basic", gaTestAccount.getKind());

          StringResultWrapper resultWrapper =
              this.aeternityServiceNative.compiler.blockingCompile(
                  TestConstants.testGABlindAuthContract, null, null);
          String code = resultWrapper.getResult();
          resultWrapper =
              this.aeternityServiceNative.compiler.blockingEncodeCalldata(
                  TestConstants.testGABlindAuthContract,
                  "init",
                  Arrays.asList(gaTestAccount.getPublicKey()),
                  Collections.emptyMap());
          String callData = resultWrapper.getResult();

          BigInteger gas = BigInteger.valueOf(4800000);
          BigInteger gasPrice = BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE);
          GeneralizedAccountsAttachTransactionModel gaAttachTx =
              GeneralizedAccountsAttachTransactionModel.builder()
                  .authFun(EncodingUtils.generateAuthFunHash("authorize"))
                  .callData(callData)
                  .code(code)
                  .gas(gas)
                  .gasPrice(gasPrice)
                  .nonce(gaTestAccount.getNonce().add(ONE))
                  .ownerId(gaTestAccount.getPublicKey())
                  .ttl(ZERO)
                  .virtualMachine(targetVM)
                  .build();

          String unsignedTx =
              aeternityServiceNative
                  .transactions
                  .blockingCreateUnsignedTransaction(gaAttachTx)
                  .getResult();
          _logger.info("Unsigned Tx - hash - dryRun: " + unsignedTx);

          DryRunTransactionResults dryRunResults =
              this.aeternityServiceNative.transactions.blockingDryRunTransactions(
                  DryRunRequest.builder()
                      .build()
                      .account(
                          DryRunAccountModel.builder()
                              .publicKey(gaTestAccount.getPublicKey())
                              .build())
                      .transactionInputItem(unsignedTx));

          _logger.info("GaAttachTxAfterDryRunOnLocalNode: " + dryRunResults.toString());
          context.assertEquals(1, dryRunResults.getResults().size());
          DryRunTransactionResult dryRunResult = dryRunResults.getResults().get(0);
          context.assertEquals("ok", dryRunResult.getResult());
        });
  }
}
