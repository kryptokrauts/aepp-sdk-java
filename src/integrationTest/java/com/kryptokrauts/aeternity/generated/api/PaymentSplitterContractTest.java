package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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

  private String generateMapParam(Map<String, Integer> recipientConditions) {
    Set<String> recipientConditionSet = new HashSet<>();
    recipientConditions.forEach((k, v) -> recipientConditionSet.add("[" + k + "] = " + v));
    System.out.println(
        "{" + recipientConditionSet.stream().collect(Collectors.joining(", ")) + "}");
    return "{" + recipientConditionSet.stream().collect(Collectors.joining(", ")) + "}";
  }

  @Test
  public void a_deployPaymentSplitterTest(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            String byteCode =
                this.aeternityServiceNative
                    .compiler
                    .blockingCompile(paymentSplitterSource, null, null)
                    .getResult();
            String callData =
                this.aeternityServiceNative
                    .compiler
                    .blockingEncodeCalldata(
                        paymentSplitterSource,
                        "init",
                        Arrays.asList(generateMapParam(initialWeights)),
                        null)
                    .getResult();

            _logger.info("contract bytecode: " + byteCode);
            _logger.info("contract calldata: " + callData);

            BigInteger gasPrice = BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE);

            ContractCreateTransactionModel contractCreate =
                ContractCreateTransactionModel.builder()
                    .amount(ZERO)
                    .callData(callData)
                    .contractByteCode(byteCode)
                    .deposit(ZERO)
                    .gas(BigInteger.valueOf(800000))
                    .gasPrice(gasPrice)
                    .nonce(getNextKeypairNonce())
                    .ownerId(keyPair.getAddress())
                    .build();

            String unsignedTx =
                aeternityServiceNative
                    .transactions
                    .blockingCreateUnsignedTransaction(contractCreate)
                    .getResult();
            _logger.info("Unsigned Tx - hash - dryRun: " + unsignedTx);

            DryRunTransactionResults dryRunResults =
                this.aeternityServiceNative.transactions.blockingDryRunTransactions(
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
                    .gas(dryRunResult.getContractCallObject().getGasUsed())
                    .gasPrice(dryRunResult.getContractCallObject().getGasPrice())
                    .build();

            PostTransactionResult result =
                aeternityServiceNative.transactions.blockingPostTransaction(contractCreate);

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
                aeternityServiceNative
                    .compiler
                    .blockingEncodeCalldata(paymentSplitterSource, "payAndSplit", null, null)
                    .getResult();
            _logger.info("Contract ID: " + localDeployedContractId);

            DryRunTransactionResults dryRunResults =
                this.aeternityServiceNative.transactions.blockingDryRunTransactions(
                    DryRunRequest.builder()
                        .build()
                        .account(
                            DryRunAccountModel.builder().publicKey(keyPair.getAddress()).build())
                        .transactionInputItem(
                            ContractCallTransactionModel.builder()
                                .callData(calldata)
                                .gas(BigInteger.valueOf(1000000))
                                .contractId(localDeployedContractId)
                                .gasPrice(BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE))
                                .amount(paymentValue.toBigInteger())
                                .nonce(getNextKeypairNonce())
                                .callerId(keyPair.getAddress())
                                .build()));

            _logger.info("callContractAfterDryRunOnLocalNode: " + dryRunResults.toString());
            context.assertEquals(1, dryRunResults.getResults().size());
            DryRunTransactionResult dryRunResult = dryRunResults.getResults().get(0);
            context.assertEquals("ok", dryRunResult.getResult());

            ContractCallTransactionModel contractAfterDryRun =
                ContractCallTransactionModel.builder()
                    .callData(calldata)
                    .contractId(localDeployedContractId)
                    .gas(dryRunResult.getContractCallObject().getGasUsed())
                    /**
                     * the result delivers the default consensus gasPrice which is to low because
                     * the tx is not added to the mempool, so we set the minimal gas price manually
                     *
                     * <p>.gasPrice(dryRunResult.getContractCallObject().getGasPrice())
                     */
                    .gasPrice(BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE))
                    .nonce(getNextKeypairNonce())
                    .callerId(keyPair.getAddress())
                    .amount(paymentValue.toBigInteger())
                    .build();

            PostTransactionResult postTransactionResult =
                aeternityServiceNative.transactions.blockingPostTransaction(contractAfterDryRun);
            context.assertEquals(
                postTransactionResult.getTxHash(),
                aeternityServiceNative.transactions.computeTxHash(contractAfterDryRun));
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
            String calldata =
                aeternityServiceNative
                    .compiler
                    .blockingEncodeCalldata(
                        paymentSplitterSource, "getTotalAmountSplitted", null, null)
                    .getResult();
            _logger.info("Contract ID: " + localDeployedContractId);

            DryRunTransactionResults dryRunResults =
                this.aeternityServiceNative.transactions.blockingDryRunTransactions(
                    DryRunRequest.builder()
                        .build()
                        .account(
                            DryRunAccountModel.builder().publicKey(keyPair.getAddress()).build())
                        .transactionInputItem(
                            ContractCallTransactionModel.builder()
                                .callData(calldata)
                                .gas(BigInteger.valueOf(1000000))
                                .contractId(localDeployedContractId)
                                .gasPrice(BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE))
                                .amount(paymentValue.toBigInteger())
                                .nonce(getNextKeypairNonce())
                                .callerId(keyPair.getAddress())
                                .build()));

            _logger.info("callContractAfterDryRunOnLocalNode: " + dryRunResults.toString());
            context.assertEquals(1, dryRunResults.getResults().size());
            DryRunTransactionResult dryRunResult = dryRunResults.getResults().get(0);
            context.assertEquals("ok", dryRunResult.getResult());

            Object decodedValue =
                decodeCallResult(
                    paymentSplitterSource,
                    "getTotalAmountSplitted",
                    dryRunResult.getContractCallObject().getReturnType(),
                    dryRunResult.getContractCallObject().getReturnValue());

            System.out.println(decodedValue);
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
            String calldata =
                aeternityServiceNative
                    .compiler
                    .blockingEncodeCalldata(paymentSplitterSource, "getOwner", null, null)
                    .getResult();
            _logger.info("Contract ID: " + localDeployedContractId);

            DryRunTransactionResults dryRunResults =
                this.aeternityServiceNative.transactions.blockingDryRunTransactions(
                    DryRunRequest.builder()
                        .build()
                        .account(
                            DryRunAccountModel.builder().publicKey(keyPair.getAddress()).build())
                        .transactionInputItem(
                            ContractCallTransactionModel.builder()
                                .callData(calldata)
                                .gas(BigInteger.valueOf(1000000))
                                .contractId(localDeployedContractId)
                                .gasPrice(BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE))
                                .amount(paymentValue.toBigInteger())
                                .nonce(getNextKeypairNonce())
                                .callerId(keyPair.getAddress())
                                .build()));

            _logger.info("callContractAfterDryRunOnLocalNode: " + dryRunResults.toString());
            context.assertEquals(1, dryRunResults.getResults().size());
            DryRunTransactionResult dryRunResult = dryRunResults.getResults().get(0);
            context.assertEquals("ok", dryRunResult.getResult());

            Object decodedValue =
                decodeCallResult(
                    paymentSplitterSource,
                    "getOwner",
                    dryRunResult.getContractCallObject().getReturnType(),
                    dryRunResult.getContractCallObject().getReturnValue());

            System.out.println(decodedValue);
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }
}
