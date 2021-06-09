package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunAccountModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.GeneralizedAccountsAttachTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.GeneralizedAccountsMetaTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.unit.UnitConversionService;
import com.kryptokrauts.aeternity.sdk.service.unit.impl.DefaultUnitConversionServiceImpl;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.vertx.ext.unit.TestContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.junit.Test;

public class TransactionGeneralizedAccountsTest extends BaseTest {

  static KeyPair gaAccountKeyPair;
  static UnitConversionService unitConversionService = new DefaultUnitConversionServiceImpl();

  @Test
  public void testGaBlindAuthContract(TestContext context) {
    this.executeTest(
        context,
        t -> {
          gaAccountKeyPair = keyPairService.generateKeyPair();
          AccountResult account = this.aeternityServiceNative.accounts.blockingGetAccount();
          BigInteger amount = unitConversionService.toSmallestUnit(BigDecimal.TEN);
          BigInteger nonce = account.getNonce().add(ONE);
          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(gaAccountKeyPair.getAddress())
                  .amount(amount)
                  .nonce(nonce)
                  .build();
          aeternityServiceNative.transactions.blockingPostTransaction(spendTx);
          AccountResult gaTestAccount =
              this.aeternityServiceNative.accounts.blockingGetAccount(
                  gaAccountKeyPair.getAddress());
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

          gaAttachTx =
              GeneralizedAccountsAttachTransactionModel.builder()
                  .authFun(EncodingUtils.generateAuthFunHash("authorize"))
                  .callData(callData)
                  .code(code)
                  .gas(dryRunResult.getContractCallObject().getGasUsed())
                  .gasPrice(dryRunResult.getContractCallObject().getGasPrice())
                  .nonce(gaTestAccount.getNonce().add(ONE))
                  .ownerId(gaTestAccount.getPublicKey())
                  .build();
          PostTransactionResult result =
              this.aeternityServiceNative.transactions.blockingPostTransaction(
                  gaAttachTx, gaAccountKeyPair.getEncodedPrivateKey());
          _logger.info("gaAttachTx result: {}", result);

          gaTestAccount =
              this.aeternityServiceNative.accounts.blockingGetAccount(
                  gaAccountKeyPair.getAddress());
          _logger.info("account: {}", gaTestAccount);
          context.assertEquals("generalized", gaTestAccount.getKind());

          amount = new BigInteger("1000000000000000000");

          KeyPair otherRecipient = keyPairService.generateKeyPair();
          SpendTransactionModel gaInnerSpendTx =
              SpendTransactionModel.builder()
                  .sender(gaAccountKeyPair.getAddress())
                  .recipient(otherRecipient.getAddress())
                  .amount(amount)
                  .payload("spent using a generalized account =)")
                  .nonce(ZERO) // GA
                  // inner
                  // tx
                  // required
                  // 0
                  // as
                  // nonce
                  .build();

          String authData =
              encodeCalldata(
                  TestConstants.testGABlindAuthContract,
                  "authorize",
                  Arrays.asList(String.valueOf(new Random().nextInt())),
                  null);

          GeneralizedAccountsMetaTransactionModel gaMetaTx =
              GeneralizedAccountsMetaTransactionModel.builder()
                  .gaId(gaAccountKeyPair.getAddress())
                  .authData(authData)
                  // .tx(encodedInnerTx)
                  .innerTxModel(gaInnerSpendTx)
                  .build();
          result = this.aeternityServiceNative.transactions.blockingPostTransaction(gaMetaTx);
          _logger.info("gaMetaTx result: {}", result);

          AccountResult otherRecipientAcc =
              this.aeternityServiceNative
                  .accounts
                  .asyncGetAccount(otherRecipient.getAddress())
                  .blockingGet();
          _logger.info("otherRecipientAcc : {}", otherRecipientAcc);
          context.assertEquals(amount, otherRecipientAcc.getBalance());
        });
  }
}
