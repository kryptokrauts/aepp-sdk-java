package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaBytes;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaHash;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaTypeTransformer;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunAccountModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.GeneralizedAccountsAttachTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.GeneralizedAccountsMetaTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.vertx.ext.unit.TestContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class TransactionGeneralizedAccountsTest extends BaseTest {

  static KeyPair gaAccountKeyPair;

  @Test
  public void gaBlindAuthContract(TestContext context) {
    this.executeTest(
        context,
        t -> {
          setAndFundGaAccountKeyPair();
          AccountResult gaTestAccount =
              this.aeternityService.accounts.blockingGetAccount(gaAccountKeyPair.getAddress());
          context.assertEquals("basic", gaTestAccount.getKind());

          StringResultWrapper resultWrapper =
              this.aeternityService.compiler.blockingCompile(gaBlindAuthSource, null);
          String code = resultWrapper.getResult();
          resultWrapper =
              this.aeternityService.compiler.blockingEncodeCalldata(
                  gaBlindAuthSource, "init", Arrays.asList(gaTestAccount.getPublicKey()), null);
          String callData = resultWrapper.getResult();

          GeneralizedAccountsAttachTransactionModel gaAttachTx =
              GeneralizedAccountsAttachTransactionModel.builder()
                  .authFun(EncodingUtils.generateAuthFunHash("authorize"))
                  .callData(callData)
                  .code(code)
                  .nonce(gaTestAccount.getNonce().add(ONE))
                  .ownerId(gaTestAccount.getPublicKey())
                  .build();

          String unsignedTx =
              aeternityService
                  .transactions
                  .blockingCreateUnsignedTransaction(gaAttachTx)
                  .getResult();
          _logger.info("Unsigned Tx - hash - dryRun: " + unsignedTx);

          DryRunTransactionResults dryRunResults =
              this.aeternityService.transactions.blockingDryRunTransactions(
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
                  .gasLimit(dryRunResult.getContractCallObject().getGasUsed())
                  .gasPrice(dryRunResult.getContractCallObject().getGasPrice())
                  .nonce(gaTestAccount.getNonce().add(ONE))
                  .ownerId(gaTestAccount.getPublicKey())
                  .build();
          PostTransactionResult result =
              this.aeternityService.transactions.blockingPostTransaction(
                  gaAttachTx, gaAccountKeyPair.getEncodedPrivateKey());
          _logger.info("gaAttachTx result: {}", result);

          gaTestAccount =
              this.aeternityService.accounts.blockingGetAccount(gaAccountKeyPair.getAddress());
          _logger.info("account: {}", gaTestAccount);
          context.assertEquals("generalized", gaTestAccount.getKind());

          BigInteger amount = new BigInteger("1000000000000000000");

          KeyPair otherRecipient = keyPairService.generateKeyPair();
          SpendTransactionModel gaInnerSpendTx =
              SpendTransactionModel.builder()
                  .sender(gaAccountKeyPair.getAddress())
                  .recipient(otherRecipient.getAddress())
                  .amount(amount)
                  .payload("spent using a generalized account =)")
                  .nonce(ZERO) // GA inner tx requires 0 as nonce
                  .build();

          String authData =
              encodeCalldata(
                  gaBlindAuthSource,
                  "authorize",
                  Arrays.asList(String.valueOf(new Random().nextInt())),
                  null);

          GeneralizedAccountsMetaTransactionModel gaMetaTx =
              GeneralizedAccountsMetaTransactionModel.builder()
                  .gaId(gaAccountKeyPair.getAddress())
                  .authData(authData)
                  .innerTxModel(gaInnerSpendTx)
                  .build();
          result = this.aeternityService.transactions.blockingPostTransaction(gaMetaTx);
          _logger.info("gaMetaTx result: {}", result);

          AccountResult otherRecipientAcc =
              this.aeternityService
                  .accounts
                  .asyncGetAccount(otherRecipient.getAddress())
                  .blockingGet();
          _logger.info("otherRecipientAcc : {}", otherRecipientAcc);
          context.assertEquals(amount, otherRecipientAcc.getBalance());
        });
  }

  @Test
  public void ecdsaAuthTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          setAndFundGaAccountKeyPair();
          AccountResult gaTestAccount =
              this.aeternityService.accounts.blockingGetAccount(gaAccountKeyPair.getAddress());
          _logger.info(gaTestAccount.toString());
          context.assertEquals("basic", gaTestAccount.getKind());

          // Ethereum address as bytes(20) in Sophia
          String expectedEthereumAddress = ethereumAddress.replace("0x", "#");
          _logger.info(expectedEthereumAddress);

          StringResultWrapper resultWrapper =
              this.aeternityService.compiler.blockingEncodeCalldata(
                  ecdsaAuthSource,
                  "init",
                  SophiaTypeTransformer.toCompilerInput(
                      List.of(new SophiaBytes(expectedEthereumAddress, 20))),
                  null);
          String callData = resultWrapper.getResult();

          resultWrapper = this.aeternityService.compiler.blockingCompile(ecdsaAuthSource, null);
          String code = resultWrapper.getResult();

          GeneralizedAccountsAttachTransactionModel gaAttachTx =
              GeneralizedAccountsAttachTransactionModel.builder()
                  .authFun(EncodingUtils.generateAuthFunHash("authorize"))
                  .callData(callData)
                  .code(code)
                  .nonce(gaTestAccount.getNonce().add(ONE))
                  .ownerId(gaTestAccount.getPublicKey())
                  .build();

          PostTransactionResult result =
              this.aeternityService.transactions.blockingPostTransaction(
                  gaAttachTx, gaAccountKeyPair.getEncodedPrivateKey());
          _logger.info("gaAttachTx result: {}", result);

          gaTestAccount =
              this.aeternityService.accounts.blockingGetAccount(gaAccountKeyPair.getAddress());
          _logger.info("account: {}", gaTestAccount);
          context.assertEquals("generalized", gaTestAccount.getKind());

          BigInteger amountToSend = unitConversionService.toSmallestUnit("1");
          KeyPair otherRecipient = keyPairService.generateKeyPair();
          SpendTransactionModel gaInnerSpendTx =
              SpendTransactionModel.builder()
                  .sender(gaAccountKeyPair.getAddress())
                  .recipient(otherRecipient.getAddress())
                  .amount(amountToSend)
                  .payload("spent using a generalized account with Ethereum signature =)")
                  .nonce(ZERO) // GA inner tx requires 0 as nonce
                  .build();
          String txHash = aeternityService.transactions.computeGAInnerTxHash(gaInnerSpendTx);
          _logger.info(txHash);

          for (int i = 1; i <= 3; i++) {
            Object currentNonceResult =
                aeternityService.transactions.blockingReadOnlyContractCall(
                    gaTestAccount.getGaContractId(), "get_nonce", ecdsaAuthSource);
            context.assertEquals(i, currentNonceResult);

            Object toSignResult =
                aeternityService.transactions.blockingReadOnlyContractCall(
                    gaTestAccount.getGaContractId(),
                    "to_sign",
                    ecdsaAuthSource,
                    ContractTxOptions.builder().params(List.of(new SophiaHash(txHash), i)).build());
            _logger.info(toSignResult.toString());

            byte[] toSign = Hex.decode(toSignResult.toString().substring(1));
            byte[] signedTxHashWithNonce = web3jSignMessage(toSign, credentials.getEcKeyPair());
            _logger.info(Hex.toHexString(signedTxHashWithNonce));

            String authData =
                this.aeternityService
                    .compiler
                    .blockingEncodeCalldata(
                        ecdsaAuthSource,
                        "authorize",
                        SophiaTypeTransformer.toCompilerInput(
                            List.of(
                                i, new SophiaBytes(Hex.toHexString(signedTxHashWithNonce), 65))),
                        null)
                    .getResult();

            GeneralizedAccountsMetaTransactionModel gaMetaTx =
                GeneralizedAccountsMetaTransactionModel.builder()
                    .gaId(gaAccountKeyPair.getAddress())
                    .authData(authData)
                    .innerTxModel(gaInnerSpendTx)
                    .build();
            result = this.aeternityService.transactions.blockingPostTransaction(gaMetaTx);
            _logger.info("gaMetaTx result: {}", result);

            AccountResult otherRecipientAcc =
                this.aeternityService
                    .accounts
                    .asyncGetAccount(otherRecipient.getAddress())
                    .blockingGet();
            _logger.info("otherRecipientAcc : {}", otherRecipientAcc);
            context.assertEquals(
                amountToSend.multiply(BigInteger.valueOf(i)), otherRecipientAcc.getBalance());

            currentNonceResult =
                aeternityService.transactions.blockingReadOnlyContractCall(
                    gaTestAccount.getGaContractId(), "get_nonce", ecdsaAuthSource);
            context.assertEquals(i + 1, currentNonceResult);
          }
        });
  }

  private void setAndFundGaAccountKeyPair() {
    gaAccountKeyPair = keyPairService.generateKeyPair();
    BigInteger amount = unitConversionService.toSmallestUnit(BigDecimal.TEN);
    SpendTransactionModel spendTx =
        SpendTransactionModel.builder()
            .sender(aeternityService.keyPairAddress)
            .recipient(gaAccountKeyPair.getAddress())
            .amount(amount)
            .nonce(aeternityService.accounts.blockingGetNextNonce())
            .build();
    aeternityService.transactions.blockingPostTransaction(spendTx);
  }
}
