package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunAccountModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunRequest;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResults;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionContractsTest extends BaseTest {

  static String localDeployedContractId;

  /**
   * create an unsigned native CreateContract transaction
   *
   * @param context
   */
  @Test
  public void buildCreateContractTransactionTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String ownerId = keyPair.getAddress();
          BigInteger amount = BigInteger.valueOf(100);
          BigInteger deposit = BigInteger.valueOf(100);
          BigInteger ttl = BigInteger.valueOf(20000l);
          BigInteger gas = BigInteger.valueOf(1000);
          BigInteger gasPrice = BigInteger.valueOf(1100000000l);
          BigInteger fee = BigInteger.valueOf(1098660000000000l);

          ContractCreateTransactionModel contractTx =
              ContractCreateTransactionModel.builder()
                  .amount(amount)
                  .callData(TestConstants.testContractCallData)
                  .contractByteCode(TestConstants.testContractByteCode)
                  .deposit(deposit)
                  .fee(fee)
                  .gas(gas)
                  .gasPrice(gasPrice)
                  .nonce(getNextKeypairNonce())
                  .ownerId(ownerId)
                  .ttl(ttl)
                  .virtualMachine(targetVM)
                  .build();

          String unsignedTxNative =
              aeternityServiceNative
                  .transactions
                  .blockingCreateUnsignedTransaction(contractTx)
                  .getResult();

          String unsignedTxDebug =
              this.aeternityServiceDebug
                  .transactions
                  .blockingCreateUnsignedTransaction(contractTx)
                  .getResult();

          _logger.debug("Call contract tx hash (debug unsigned): " + unsignedTxDebug);

          context.assertEquals(unsignedTxDebug, unsignedTxNative);
        });
  }

  @Test
  public void buildCallContractTransactionTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String callerId = keyPair.getAddress();
          BigInteger ttl = BigInteger.valueOf(20000);
          BigInteger gas = BigInteger.valueOf(1000);
          BigInteger gasPrice = BigInteger.valueOf(1000000000);
          BigInteger nonce = getNextKeypairNonce();
          String callContractCalldata = TestConstants.encodedServiceCall;

          ContractCallTransactionModel callTx =
              ContractCallTransactionModel.builder()
                  .callData(callContractCalldata)
                  .contractId(localDeployedContractId)
                  .gas(gas)
                  .gasPrice(gasPrice)
                  .nonce(nonce)
                  .callerId(callerId)
                  .ttl(ttl)
                  .fee(BigInteger.valueOf(1454500000000000l))
                  .virtualMachine(targetVM)
                  .build();

          String unsignedTxNative =
              this.aeternityServiceNative
                  .transactions
                  .blockingCreateUnsignedTransaction(callTx)
                  .getResult();

          _logger.info("Call contract tx hash (native unsigned): " + unsignedTxNative);

          String unsignedTxDebug =
              this.aeternityServiceDebug
                  .transactions
                  .blockingCreateUnsignedTransaction(callTx)
                  .getResult();

          _logger.info("Call contract tx hash (debug unsigned): " + unsignedTxDebug);

          context.assertEquals(unsignedTxDebug, unsignedTxNative);
        });
  }

  @Test
  public void staticCallContractOnLocalNode(TestContext context) {
    this.executeTest(
        context,
        t -> {
          BigInteger nonce = getNextKeypairNonce();

          // Compile the call contract
          String calldata =
              encodeCalldata(
                  TestConstants.testContractSourceCode,
                  TestConstants.testContractFunction,
                  TestConstants.testContractFunctionParams,
                  null);

          DryRunTransactionResults results =
              this.aeternityServiceNative.transactions.blockingDryRunTransactions(
                  DryRunRequest.builder()
                      .build()
                      .account(DryRunAccountModel.builder().publicKey(keyPair.getAddress()).build())
                      .account(DryRunAccountModel.builder().publicKey(keyPair.getAddress()).build())
                      .transactionInputItem(
                          createUnsignedContractCallTx(context, nonce, calldata, null))
                      .transactionInputItem(
                          createUnsignedContractCallTx(context, nonce.add(ONE), calldata, null)));

          _logger.info(results.toString());
          for (DryRunTransactionResult result : results.getResults()) {
            context.assertEquals("ok", result.getResult());
          }
        });
  }

  @Test
  public void staticCallContractFailOnLocalNode(TestContext context) {
    this.executeTest(
        context,
        t -> {
          // Compile the call contract
          String calldata =
              encodeCalldata(
                  TestConstants.testContractSourceCode,
                  TestConstants.testContractFunction,
                  TestConstants.testContractFunctionParams,
                  null);

          DryRunTransactionResults results =
              this.aeternityServiceNative.transactions.blockingDryRunTransactions(
                  DryRunRequest.builder()
                      .build()
                      .account(DryRunAccountModel.builder().publicKey(keyPair.getAddress()).build())
                      .transactionInputItem(
                          createUnsignedContractCallTx(context, ONE, calldata, null)));

          _logger.info("DryRunResult when expecting error:\n" + results.toString());
          for (DryRunTransactionResult result : results.getResults()) {
            context.assertEquals("error", result.getResult());
          }
        });
  }

  /**
   * @param context
   * @throws Throwable
   */
  @Test
  public void callContractAfterDryRunOnLocalNode(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          // Compile the call contract
          String calldata =
              encodeCalldata(
                  TestConstants.testContractSourceCode,
                  TestConstants.testContractFunction,
                  TestConstants.testContractFunctionParams,
                  null);

          DryRunTransactionResults results =
              this.aeternityServiceNative.transactions.blockingDryRunTransactions(
                  DryRunRequest.builder()
                      .build()
                      .account(DryRunAccountModel.builder().publicKey(keyPair.getAddress()).build())
                      .transactionInputItem(
                          createUnsignedContractCallTx(
                              context, getNextKeypairNonce(), calldata, null)));

          _logger.info("callContractAfterDryRunOnLocalNode: " + results.toString());

          for (DryRunTransactionResult result : results.getResults()) {
            context.assertEquals("ok", result.getResult());

            ContractCallTransactionModel callTx =
                ContractCallTransactionModel.builder()
                    .callData(calldata)
                    .contractId(localDeployedContractId)
                    .gas(result.getContractCallObject().getGasUsed())
                    .gasPrice(result.getContractCallObject().getGasPrice())
                    .nonce(getNextKeypairNonce())
                    .callerId(keyPair.getAddress())
                    .ttl(ZERO)
                    .virtualMachine(targetVM)
                    .build();

            PostTransactionResult response =
                this.aeternityServiceNative.transactions.blockingPostTransaction(callTx);

            context.assertEquals(
                response.getTxHash(),
                this.aeternityServiceNative.transactions.computeTxHash(callTx));
            _logger.info("Call contract tx hash: " + response.getTxHash());

            // get the tx info object to resolve the result
            try {
              TransactionInfoResult txInfoObject = waitForTxInfoObject(response.getTxHash());
              ObjectResultWrapper decodedValue =
                  decodeCallResult(
                      TestConstants.testContractSourceCode,
                      TestConstants.testContractFunction,
                      txInfoObject.getCallInfo().getReturnType(),
                      txInfoObject.getCallInfo().getReturnValue());
              context.assertTrue(decodedValue.getResult() instanceof Integer);
              context.assertEquals(
                  TestConstants.testContractFuntionParam, decodedValue.getResult().toString());
            } catch (Throwable e) {
              context.fail(e);
            }
          }
        });
  }

  private String createUnsignedContractCallTx(
      TestContext context, BigInteger nonce, String calldata, BigInteger gasPrice) {
    return this.aeternityServiceNative
        .transactions
        .blockingCreateUnsignedTransaction(createCallContractModel(nonce, calldata, gasPrice))
        .getResult();
  }

  private ContractCallTransactionModel createCallContractModel(
      BigInteger nonce, String calldata, BigInteger gasPrice) {
    String callerId = keyPair.getAddress();
    BigInteger ttl = BigInteger.ZERO;
    BigInteger gas = BigInteger.valueOf(1579000);
    ContractCallTransactionModel model =
        ContractCallTransactionModel.builder()
            .callData(calldata)
            .contractId(localDeployedContractId)
            .gas(gas)
            .gasPrice(
                gasPrice != null ? gasPrice : BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE))
            .nonce(nonce)
            .callerId(callerId)
            .ttl(ttl)
            .virtualMachine(targetVM)
            .build();
    return model;
  }

  @Test
  public void aDeployContractNativeOnLocalNode(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          BigInteger gas = BigInteger.valueOf(1000000);
          BigInteger gasPrice = BigInteger.valueOf(2000000000);

          ContractCreateTransactionModel contractTx =
              ContractCreateTransactionModel.builder()
                  .amount(ZERO)
                  .callData(TestConstants.testContractCallData)
                  .contractByteCode(TestConstants.testContractByteCode)
                  .deposit(ZERO)
                  .gas(gas)
                  .gasPrice(gasPrice)
                  .nonce(getNextKeypairNonce())
                  .ownerId(keyPair.getAddress())
                  .ttl(ZERO)
                  .virtualMachine(targetVM)
                  .build();

          PostTransactionResult result =
              this.aeternityServiceNative.transactions.blockingPostTransaction(contractTx);
          try {
            TransactionInfoResult txInfoObject = waitForTxInfoObject(result.getTxHash());
            localDeployedContractId = txInfoObject.getCallInfo().getContractId();
            _logger.info("Deployed contract - hash " + result.getTxHash() + " - " + txInfoObject);
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void callContractOnLocalNodeTest(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          String callData =
              this.aeternityServiceNative
                  .compiler
                  .blockingEncodeCalldata(
                      TestConstants.testContractSourceCode,
                      TestConstants.testContractFunction,
                      TestConstants.testContractFunctionParams,
                      null)
                  .getResult();

          // post the signed contract call tx
          BigInteger nonceToVerifyHash = getNextKeypairNonce();
          PostTransactionResult result =
              this.aeternityServiceNative.transactions.blockingPostTransaction(
                  createCallContractModel(nonceToVerifyHash, callData, null));
          context.assertEquals(
              result.getTxHash(),
              this.aeternityServiceNative.transactions.computeTxHash(
                  createCallContractModel(nonceToVerifyHash, callData, null)));
          _logger.info("CreateContractTx hash: " + result.getTxHash());

          // get the tx info object to resolve the result
          try {
            TransactionInfoResult txInfoObject = waitForTxInfoObject(result.getTxHash());
            ObjectResultWrapper decodedValue =
                decodeCallResult(
                    TestConstants.testContractSourceCode,
                    TestConstants.testContractFunction,
                    txInfoObject.getCallInfo().getReturnType(),
                    txInfoObject.getCallInfo().getReturnValue());
            context.assertTrue(decodedValue.getResult() instanceof Integer);
            context.assertEquals(
                TestConstants.testContractFuntionParam, decodedValue.getResult().toString());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  @Ignore // specific testcase we don't want to run each time
  public void deployContractNativeOnTestNetworkTest(TestContext context)
      throws TransactionCreateException {
    // needs to be set before executing the test
    String privateKey = "";
    keyPair = keyPairService.recoverKeyPair(privateKey);

    AeternityService testnetService =
        new AeternityService(
            AeternityServiceConfiguration.configure()
                .baseUrl(TestConstants.testnetURL)
                .network(Network.TESTNET)
                .vertx(rule.vertx())
                .compile());

    String ownerId = keyPair.getAddress();
    BigInteger amount = BigInteger.ZERO;
    BigInteger deposit = BigInteger.ZERO;
    BigInteger ttl = BigInteger.ZERO;
    BigInteger gas = BigInteger.valueOf(1000);
    BigInteger gasPrice = BigInteger.valueOf(1100000000);
    BigInteger nonce = getNextKeypairNonce();

    ContractCreateTransactionModel testnetCreateTx =
        ContractCreateTransactionModel.builder()
            .amount(amount)
            .callData(TestConstants.testContractCallData)
            .contractByteCode(TestConstants.testContractByteCode)
            .deposit(deposit)
            .gas(gas)
            .gasPrice(gasPrice)
            .nonce(nonce)
            .ownerId(ownerId)
            .ttl(ttl)
            .virtualMachine(targetVM)
            .build();

    PostTransactionResult result =
        testnetService.transactions.blockingPostTransaction(testnetCreateTx);

    context.assertEquals(
        result.getTxHash(), testnetService.transactions.computeTxHash(testnetCreateTx));
  }
}
