package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaBytes;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaHash;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaString;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionInfoResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import io.vertx.ext.unit.TestContext;
import java.util.List;
import javax.naming.ConfigurationException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class EthereumSignatureContractTest extends BaseTest {

  static String contractId;
  static Object readOnlyResult;

  static String sophiaEcrecoverResult;

  static final SophiaString sophiaTestString = new SophiaString("test");

  @Override
  public void setupTestEnv(TestContext context) throws ConfigurationException {
    super.setupTestEnv(context);
    this.executeTest(
        context,
        t -> {
          String byteCode =
              aeternityService.compiler.blockingCompile(ethereumSignaturesSource, null).getResult();
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
          contractId = createTxInfoResult.getCallInfo().getContractId();
        });
  }

  @Test
  public void withoutPrefixTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "keccak256",
                  ethereumSignaturesSource,
                  ContractTxOptions.builder().params(List.of(sophiaTestString)).build());
          SophiaHash hashFromContract = new SophiaHash(readOnlyResult.toString());
          byte[] hashedMessage = web3jKeccak256("test", false);
          context.assertEquals(
              hashFromContract.getCompilerValue().substring(1), Hex.toHexString(hashedMessage));

          byte[] signature = web3jSignMessage(hashedMessage, credentials.getEcKeyPair());
          SophiaBytes sophiaBytes65Signature = new SophiaBytes(Hex.toHexString(signature), 65);

          _logger.info(hashFromContract.getCompilerValue());
          _logger.info(sophiaBytes65Signature.getCompilerValue());

          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "ecrecover_secp256k1",
                  ethereumSignaturesSource,
                  ContractTxOptions.builder()
                      .params(List.of(hashFromContract, sophiaBytes65Signature))
                      .build());

          sophiaEcrecoverResult = readOnlyResult.toString();
          _logger.info(sophiaEcrecoverResult);
          context.assertTrue(sophiaEcrecoverResult.contains(ethereumAddress.substring(2)));
        });
  }

  @Test
  public void withPrefixTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "ethereum_prefixed_hash",
                  ethereumSignaturesSource,
                  ContractTxOptions.builder().params(List.of(sophiaTestString)).build());

          SophiaHash ethereumPrefixedHashFromContract = new SophiaHash(readOnlyResult.toString());
          byte[] ethereumPrefixedHash = web3jKeccak256("test", true);
          context.assertEquals(
              ethereumPrefixedHashFromContract.getCompilerValue().substring(1),
              Hex.toHexString(ethereumPrefixedHash));

          context.assertEquals(ethereumAddress, credentials.getAddress());

          byte[] signatureWithPrefix =
              web3jSignMessage(ethereumPrefixedHash, credentials.getEcKeyPair());
          SophiaBytes sophiaBytes65SignatureWithPrefix =
              new SophiaBytes(Hex.toHexString(signatureWithPrefix), 65);

          _logger.info(ethereumPrefixedHashFromContract.getCompilerValue());
          _logger.info(sophiaBytes65SignatureWithPrefix.getCompilerValue());

          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "ecrecover_secp256k1",
                  ethereumSignaturesSource,
                  ContractTxOptions.builder()
                      .params(
                          List.of(
                              ethereumPrefixedHashFromContract, sophiaBytes65SignatureWithPrefix))
                      .build());

          sophiaEcrecoverResult = readOnlyResult.toString();
          _logger.info(sophiaEcrecoverResult);
          context.assertTrue(sophiaEcrecoverResult.contains(ethereumAddress.substring(2)));
        });
  }
}
