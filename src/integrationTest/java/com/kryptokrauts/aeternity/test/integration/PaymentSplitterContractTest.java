package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaTypeTransformer;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunAccountModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import io.vertx.ext.unit.TestContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentSplitterContractTest extends BaseTest {

  static String paymentSplitterSource;
  static String localDeployedContractId;

  static KeyPair initialReceiver1;
  static KeyPair initialReceiver2;
  static KeyPair initialReceiver3;

  static Map<String, Integer> initialWeights = new HashMap<>();

  @Test
  public void a_a_init(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            final InputStream inputStream =
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("contracts/PaymentSplitter.aes");
            paymentSplitterSource =
                IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());

            initialReceiver1 = new KeyPairServiceFactory().getService().generateKeyPair();
            initialReceiver2 = new KeyPairServiceFactory().getService().generateKeyPair();
            initialReceiver3 = new KeyPairServiceFactory().getService().generateKeyPair();
            _logger.info("Initial receiver 1: " + initialReceiver1.getAddress());
            _logger.info("Initial receiver 2: " + initialReceiver2.getAddress());
            _logger.info("Initial receiver 3: " + initialReceiver3.getAddress());

            initialWeights.put(initialReceiver1.getAddress(), 40);
            initialWeights.put(initialReceiver2.getAddress(), 40);
            initialWeights.put(initialReceiver3.getAddress(), 20);
            context.assertEquals(3, initialWeights.size());
          } catch (IOException e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void a_deployPaymentSplitterTest(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            String byteCode =
                this.aeternityService
                    .compiler
                    .blockingCompile(paymentSplitterSource, null)
                    .getResult();
            String callData =
                this.aeternityService
                    .compiler
                    .blockingEncodeCalldata(
                        paymentSplitterSource,
                        "init",
                        SophiaTypeTransformer.toCompilerInput(List.of(initialWeights)),
                        null)
                    .getResult();

            _logger.info("contract bytecode: " + byteCode);
            _logger.info("contract calldata: " + callData);

            ContractCreateTransactionModel contractCreate =
                ContractCreateTransactionModel.builder()
                    .callData(callData)
                    .contractByteCode(byteCode)
                    .nonce(getNextKeypairNonce())
                    .ownerId(keyPair.getAddress())
                    .build();

            String unsignedTx =
                aeternityService
                    .transactions
                    .blockingCreateUnsignedTransaction(contractCreate)
                    .getResult();
            _logger.info("Unsigned Tx - hash - dryRun: " + unsignedTx);

            DryRunTransactionResults dryRunResults =
                this.aeternityService.transactions.blockingDryRunTransactions(
                    DryRunRequest.builder()
                        .build()
                        .account(
                            DryRunAccountModel.builder().publicKey(keyPair.getAddress()).build())
                        .transactionInputItem(unsignedTx));

            _logger.info("callContractAfterDryRunOnLocalNode: " + dryRunResults.toString());
            context.assertEquals(1, dryRunResults.getResults().size());
            DryRunTransactionResult dryRunResult = dryRunResults.getResults().get(0);
            context.assertEquals("ok", dryRunResult.getResult());

            contractCreate =
                contractCreate
                    .toBuilder()
                    .gasLimit(dryRunResult.getContractCallObject().getGasUsed())
                    .gasPrice(dryRunResult.getContractCallObject().getGasPrice())
                    .build();

            PostTransactionResult result =
                aeternityService.transactions.blockingPostTransaction(contractCreate);

            TransactionInfoResult txInfoObject = waitForTxInfoObject(result.getTxHash());

            localDeployedContractId = txInfoObject.getCallInfo().getContractId();
            _logger.info("Deployed contract - hash " + result.getTxHash() + " - " + txInfoObject);
            if ("revert".equals(txInfoObject.getCallInfo().getReturnType())) {
              context.assertTrue(
                  false,
                  "transaction reverted: "
                      + decodeCalldata(txInfoObject.getCallInfo().getReturnValue(), "string"));
            }
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void b_callPayAndSplitMethodTest(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            BigInteger balanceRecipient1;
            BigInteger balanceRecipient2;
            BigInteger balanceRecipient3;
            // if one of the accounts wasn't active we get an error and know
            // that the
            // accounts don't have any balance
            balanceRecipient1 =
                Optional.ofNullable(getAccount(initialReceiver1.getAddress()).getBalance())
                    .orElse(ZERO);
            balanceRecipient2 =
                Optional.ofNullable(getAccount(initialReceiver2.getAddress()).getBalance())
                    .orElse(ZERO);
            balanceRecipient3 =
                Optional.ofNullable(getAccount(initialReceiver3.getAddress()).getBalance())
                    .orElse(ZERO);

            BigDecimal paymentValue = UnitConversionUtil.toAettos("1", Unit.AE);
            String calldata =
                aeternityService
                    .compiler
                    .blockingEncodeCalldata(paymentSplitterSource, "payAndSplit", null, null)
                    .getResult();
            _logger.info("Contract ID: " + localDeployedContractId);

            ContractCallTransactionModel contractCallTransactionModel =
                ContractCallTransactionModel.builder()
                    .callData(calldata)
                    .contractId(localDeployedContractId)
                    .amount(paymentValue.toBigInteger())
                    .nonce(getNextKeypairNonce())
                    .callerId(keyPair.getAddress())
                    .build();

            PostTransactionResult postTransactionResult =
                aeternityService.transactions.blockingPostTransaction(contractCallTransactionModel);
            context.assertEquals(
                postTransactionResult.getTxHash(),
                aeternityService.transactions.computeTxHash(contractCallTransactionModel));
            _logger.info("CreateContractTx hash: " + postTransactionResult.getTxHash());

            // we wait until the tx is available and the payment should have
            // been splitted
            TransactionInfoResult txInfoObject =
                waitForTxInfoObject(postTransactionResult.getTxHash());
            _logger.info(
                "PayAndSplit transaction - hash "
                    + postTransactionResult.getTxHash()
                    + " - "
                    + txInfoObject);
            if ("revert".equals(txInfoObject.getCallInfo().getReturnType())) {
              context.fail(
                  "transaction reverted: "
                      + decodeCalldata(txInfoObject.getCallInfo().getReturnValue(), "string"));
            }

            context.assertEquals(
                balanceRecipient1.add(
                    paymentValue.multiply(BigDecimal.valueOf(0.4)).toBigInteger()),
                getAccount(initialReceiver1.getAddress()).getBalance());
            context.assertEquals(
                balanceRecipient2.add(
                    paymentValue.multiply(BigDecimal.valueOf(0.4)).toBigInteger()),
                getAccount(initialReceiver2.getAddress()).getBalance());
            context.assertEquals(
                balanceRecipient3.add(
                    paymentValue.multiply(BigDecimal.valueOf(0.2)).toBigInteger()),
                getAccount(initialReceiver3.getAddress()).getBalance());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void c_callGetTotalAmountSplitted(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            BigDecimal paymentValue = UnitConversionUtil.toAettos("0", Unit.AE);
            Object decodedValue =
                aeternityService.transactions.blockingReadOnlyContractCall(
                    paymentSplitterSource,
                    null,
                    localDeployedContractId,
                    "getTotalAmountSplitted",
                    null);
            _logger.info(decodedValue.toString());
            context.assertEquals(1000000000000000000l, decodedValue);
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void d_callGetOwner(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            BigDecimal paymentValue = UnitConversionUtil.toAettos("0", Unit.AE);
            Object decodedValue =
                aeternityService.transactions.blockingReadOnlyContractCall(
                    paymentSplitterSource, null, localDeployedContractId, "getOwner", null);
            _logger.info(decodedValue.toString());
            context.assertEquals(
                "ak_twR4h7dEcUtc2iSEDv8kB7UFJJDGiEDQCXr85C3fYF8FdVdyo", decodedValue);
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }
}
