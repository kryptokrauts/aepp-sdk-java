package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceFactory;
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
import java.util.Arrays;
import javax.naming.ConfigurationException;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionContractsTest extends BaseTest {

  static String localDeployedContractId;

  @Test
  public void buildCreateContractTransactionFailTest(TestContext context) {
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
                  .gasLimit(gas)
                  .gasPrice(gasPrice)
                  .nonce(getNextKeypairNonce())
                  .ownerId(ownerId)
                  .ttl(ttl)
                  .virtualMachine(targetVM)
                  .build();

          try {
            aeternityService.transactions.blockingCreateUnsignedTransaction(contractTx).getResult();
          } catch (Exception e) {
            context.assertEquals(e.getClass().getName(), InvalidParameterException.class.getName());
            context.assertTrue(
                e.getMessage()
                    .contains(
                        "Deposit for creation contract should be 0 otherwise deposit will be locked forever"));
          }
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
              this.aeternityService.transactions.blockingDryRunTransactions(
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
              this.aeternityService.transactions.blockingDryRunTransactions(
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
              this.aeternityService.transactions.blockingDryRunTransactions(
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
                    .gasLimit(result.getContractCallObject().getGasUsed())
                    .gasPrice(result.getContractCallObject().getGasPrice())
                    .nonce(getNextKeypairNonce())
                    .callerId(keyPair.getAddress())
                    .build();

            PostTransactionResult response =
                this.aeternityService.transactions.blockingPostTransaction(callTx);

            context.assertEquals(
                response.getTxHash(), this.aeternityService.transactions.computeTxHash(callTx));
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
    return this.aeternityService
        .transactions
        .blockingCreateUnsignedTransaction(createCallContractModel(nonce, calldata, gasPrice))
        .getResult();
  }

  private ContractCallTransactionModel createCallContractModel(
      BigInteger nonce, String calldata, BigInteger gasPrice) {
    String callerId = keyPair.getAddress();
    ContractCallTransactionModel model =
        ContractCallTransactionModel.builder()
            .callData(calldata)
            .contractId(localDeployedContractId)
            .gasPrice(gasPrice != null ? gasPrice : BaseConstants.MINIMAL_GAS_PRICE)
            .nonce(nonce)
            .callerId(callerId)
            .virtualMachine(targetVM)
            .build();
    return model;
  }

  @Test
  public void aDeployContractNativeOnLocalNode(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          ContractCreateTransactionModel contractTx =
              ContractCreateTransactionModel.builder()
                  .callData(TestConstants.testContractCallData)
                  .contractByteCode(TestConstants.testContractByteCode)
                  .nonce(getNextKeypairNonce())
                  .ownerId(keyPair.getAddress())
                  .build();

          PostTransactionResult result =
              this.aeternityService.transactions.blockingPostTransaction(contractTx);
          try {
            TransactionInfoResult txInfoObject = waitForTxInfoObject(result.getTxHash());
            localDeployedContractId = txInfoObject.getCallInfo().getContractId();
            _logger.info("Deployed contract - hash " + result.getTxHash() + " - " + txInfoObject);
            _logger.info("ContractId: {}", localDeployedContractId);
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
              this.aeternityService
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
              this.aeternityService.transactions.blockingPostTransaction(
                  createCallContractModel(nonceToVerifyHash, callData, null));
          context.assertEquals(
              result.getTxHash(),
              this.aeternityService.transactions.computeTxHash(
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
  public void chatBotTestContract(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String sourceCode =
              "@compiler >= 6\ninclude \"String.aes\"\ncontract ChatBot =\n    datatype event = Greeting(string)\n    entrypoint greet(name: string) : string =\n        Chain.event(Greeting(name))\n        String.concat(\"Hello, \", name)";
          String byteCode = aeternityService.compiler.blockingCompile(sourceCode, null).getResult();
          context.assertEquals(
              "cb_+QECRgOgDItCecy4LjX1HPPJyJDbPPSszAMNIR74Sf/AQuUrSfPAuNW4kv4FLNoIADcBd3cMAQBE/BMCAAICAxFlpeAPDwJvgibPDAEADAMdSGVsbG8sIAQDEauIMtH+RNZEHwA3ADcAGg6CPwEDP/5lpeAPAjcBhwE3AXc3AEY2AAAAYQ4AnwGBKqgAJCNem2S/XWUMHZW/mRgT6P3fxzcxrmpAFqv6dEMBAz/+q4gy0QI3And3dzoUAAIAuDwvBBEFLNoIFWdyZWV0EUTWRB8RaW5pdBFlpeAPLUNoYWluLmV2ZW50EauIMtE5LlN0cmluZy5jb25jYXSCLwCFNi4xLjAAvOW1NA==",
              byteCode);

          ContractCreateTransactionModel contractCreate =
              ContractCreateTransactionModel.builder()
                  .callData(BaseConstants.CONTRACT_EMPTY_INIT_CALLDATA)
                  .contractByteCode(byteCode)
                  .nonce(aeternityService.accounts.blockingGetNextNonce())
                  .ownerId(aeternityService.keyPairAddress)
                  .build();

          PostTransactionResult createTxResult =
              aeternityService.transactions.blockingPostTransaction(contractCreate);
          TransactionInfoResult createTxInfoResult =
              aeternityService.info.blockingGetTransactionInfoByHash(createTxResult.getTxHash());
          String contractId = createTxInfoResult.getCallInfo().getContractId();

          AeternityService readOnlyService = null;
          try {
            // initializing a service without providing a keyPair for read-only calls only
            readOnlyService =
                new AeternityServiceFactory()
                    .getService(
                        AeternityServiceConfiguration.configure()
                            .baseUrl(getAeternityBaseUrl())
                            .network(Network.DEVNET)
                            .compile());
          } catch (ConfigurationException e) {
            context.fail("Error loading aeternity base url.");
          }
          String callData =
              aeternityService
                  .compiler
                  .blockingEncodeCalldata(
                      sourceCode, "greet", Arrays.asList("\"kryptokrauts\""), null)
                  .getResult();
          ContractCallTransactionModel contractCall =
              ContractCallTransactionModel.builder()
                  .contractId(contractId)
                  .callData(callData)
                  .build();

          DryRunTransactionResult dryRunResult =
              readOnlyService.transactions.blockingDryRunContractCall(contractCall, true);
          _logger.info(dryRunResult.toString());
          context.assertEquals("ok", dryRunResult.getResult());

          ObjectResultWrapper resultWrapper =
              aeternityService.compiler.blockingDecodeCallResult(
                  sourceCode,
                  "greet",
                  dryRunResult.getContractCallObject().getReturnType(),
                  dryRunResult.getContractCallObject().getReturnValue(),
                  null);
          context.assertEquals("Hello, kryptokrauts", resultWrapper.getResult());
        });
  }

  @Test
  public void dryRunFailTest(TestContext context) throws TransactionCreateException {
    this.executeTest(
        context,
        t -> {
          try {
            String sourceCode =
                "@compiler >= 6\ninclude \"String.aes\"\ncontract ChatBot =\n    datatype event = Greeting(string)\n    entrypoint greet(name: string) : string =\n        Chain.event(Greeting(name))\n        String.concat(\"Hello, \", name)";
            String byteCode =
                aeternityService.compiler.blockingCompile(sourceCode, null).getResult();
            context.assertEquals(
                "cb_+QECRgOgDItCecy4LjX1HPPJyJDbPPSszAMNIR74Sf/AQuUrSfPAuNW4kv4FLNoIADcBd3cMAQBE/BMCAAICAxFlpeAPDwJvgibPDAEADAMdSGVsbG8sIAQDEauIMtH+RNZEHwA3ADcAGg6CPwEDP/5lpeAPAjcBhwE3AXc3AEY2AAAAYQ4AnwGBKqgAJCNem2S/XWUMHZW/mRgT6P3fxzcxrmpAFqv6dEMBAz/+q4gy0QI3And3dzoUAAIAuDwvBBEFLNoIFWdyZWV0EUTWRB8RaW5pdBFlpeAPLUNoYWluLmV2ZW50EauIMtE5LlN0cmluZy5jb25jYXSCLwCFNi4xLjAAvOW1NA==",
                byteCode);

            ContractCreateTransactionModel contractCreate =
                ContractCreateTransactionModel.builder()
                    .callData(BaseConstants.CONTRACT_EMPTY_INIT_CALLDATA)
                    .contractByteCode(byteCode)
                    .nonce(aeternityService.accounts.blockingGetNextNonce())
                    .ownerId(aeternityService.keyPairAddress)
                    .build();

            PostTransactionResult createTxResult =
                aeternityService.transactions.blockingPostTransaction(contractCreate);
            TransactionInfoResult createTxInfoResult =
                aeternityService.info.blockingGetTransactionInfoByHash(createTxResult.getTxHash());
            String contractId = createTxInfoResult.getCallInfo().getContractId();

            AeternityService wrongConfigured =
                new AeternityServiceFactory()
                    .getService(
                        AeternityServiceConfiguration.configure()
                            .baseUrl("http://does-not.exist")
                            .compile());

            String callData =
                aeternityService
                    .compiler
                    .blockingEncodeCalldata(
                        sourceCode, "greet", Arrays.asList("\"kryptokrauts\""), null)
                    .getResult();
            ContractCallTransactionModel contractCall =
                ContractCallTransactionModel.builder()
                    .contractId(contractId)
                    .callData(callData)
                    .build();

            Assertions.assertThrows(
                AException.class,
                () -> wrongConfigured.transactions.blockingDryRunContractCall(contractCall, true));

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
                .baseUrl(BaseConstants.DEFAULT_TESTNET_URL)
                .network(Network.TESTNET)
                .vertx(rule.vertx())
                .compile());

    String ownerId = keyPair.getAddress();
    BigInteger nonce = getNextKeypairNonce();

    ContractCreateTransactionModel testnetCreateTx =
        ContractCreateTransactionModel.builder()
            .callData(TestConstants.testContractCallData)
            .contractByteCode(TestConstants.testContractByteCode)
            .nonce(nonce)
            .ownerId(ownerId)
            .build();

    PostTransactionResult result =
        testnetService.transactions.blockingPostTransaction(testnetCreateTx);

    context.assertEquals(
        result.getTxHash(), testnetService.transactions.computeTxHash(testnetCreateTx));
  }
}
