package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaAENSName;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaAENSPointee;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaAENSPointee.Type;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaBytes;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaChainTTL;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaHash;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaSignature;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaString;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaTypeTransformer;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxResult;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.List;
import javax.naming.ConfigurationException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class SophiaTypesContractTest extends BaseTest {

  static String contractId;
  static Object readOnlyResult;

  static KeyPair testKeyPair;

  @Override
  public void setupTestEnv(TestContext context) throws ConfigurationException {
    super.setupTestEnv(context);
    testKeyPair = keyPairService.generateKeyPair();
    this.executeTest(
        context,
        t -> {
          // deploy contract
          ContractTxResult txResult =
              aeternityService.transactions.blockingContractCreate(sophiaTypesSource);
          contractId = txResult.getCallResult().getContractId();
        });
  }

  @Test
  public void testString(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String testStringParam = "kryptokrauts";
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testString",
                  sophiaTypesSource,
                  ContractTxOptions.builder()
                      .params(List.of(new SophiaString(testStringParam)))
                      .build());
          context.assertEquals(testStringParam, readOnlyResult);
        });
  }

  @Test
  public void testInt(TestContext context) {
    this.executeTest(
        context,
        t -> {
          BigInteger testIntParam = BigInteger.valueOf(Long.MAX_VALUE);
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testInt",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testIntParam)).build());
          // readOnlyResult is of type Long in this case. for values that don't exceed
          // Integer.MAX_VALUE it will be of type Integer
          context.assertEquals(testIntParam, BigInteger.valueOf((Long) readOnlyResult));
        });
  }

  @Test
  public void testBool(TestContext context) {
    this.executeTest(
        context,
        t -> {
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testBool",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(true)).build());
          context.assertEquals(true, readOnlyResult);
        });
  }

  @Test
  public void testHash(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String testHashParam = EncodingUtils.generateAuthFunHash("auth");
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testHash",
                  sophiaTypesSource,
                  ContractTxOptions.builder()
                      .params(List.of(new SophiaHash(testHashParam)))
                      .build());
          context.assertEquals("#" + testHashParam, readOnlyResult);
        });
  }

  @Test
  public void testBytes(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String test4BytesParam = Hex.toHexString("1337".getBytes());
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "test4Bytes",
                  sophiaTypesSource,
                  ContractTxOptions.builder()
                      .params(List.of(new SophiaBytes(test4BytesParam, 4)))
                      .build());
          context.assertEquals("#" + test4BytesParam, readOnlyResult);

          String test12BytesParam = Hex.toHexString("kryptokrauts".getBytes());
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "test12Bytes",
                  sophiaTypesSource,
                  ContractTxOptions.builder()
                      .params(List.of(new SophiaBytes(test12BytesParam, 12)))
                      .build());
          context.assertEquals("#" + test12BytesParam, readOnlyResult);
        });
  }

  @Test
  public void testAddress(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String testAddressParam = testKeyPair.getAddress();
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testAddress",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testAddressParam)).build());
          context.assertEquals(testAddressParam, readOnlyResult);
        });
  }

  @Test
  public void testSignature(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String testSignatureParam = null;
          try {
            testSignatureParam =
                Hex.toHexString(
                    SigningUtil.signMessage("testSignature", testKeyPair.getEncodedPrivateKey()));
          } catch (CryptoException e) {
            context.fail(e);
          }
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testSignature",
                  sophiaTypesSource,
                  ContractTxOptions.builder()
                      .params(List.of(new SophiaSignature(testSignatureParam)))
                      .build());
          context.assertEquals("#" + testSignatureParam, readOnlyResult);
        });
  }

  @Test
  public void testOracle(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String testOracleParam = testKeyPair.getOracleAddress();
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testOracle",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testOracleParam)).build());
          context.assertEquals(testOracleParam, readOnlyResult);
        });
  }

  @Test
  public void testPointee(TestContext context) {
    this.executeTest(
        context,
        t -> {
          SophiaAENSPointee testPointeeParam =
              new SophiaAENSPointee(testKeyPair.getContractAddress());
          context.assertEquals(Type.ContractPt, testPointeeParam.getType());
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testPointee",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testPointeeParam)).build());
          JsonObject expectedPointeeResult =
              new JsonObject().put("AENS.ContractPt", List.of(testKeyPair.getAddress()));
          context.assertEquals(expectedPointeeResult, readOnlyResult);
        });
  }

  @Test
  public void testPointeeList(TestContext context) {
    this.executeTest(
        context,
        t -> {
          SophiaAENSPointee accountPointee = new SophiaAENSPointee(testKeyPair.getAddress());
          context.assertEquals(Type.AccountPt, accountPointee.getType());
          SophiaAENSPointee channelPointee =
              new SophiaAENSPointee(testKeyPair.getAddress().replace("ak_", "ch_"));
          context.assertEquals(Type.ChannelPt, channelPointee.getType());
          SophiaAENSPointee contractPointee =
              new SophiaAENSPointee(testKeyPair.getContractAddress());
          context.assertEquals(Type.ContractPt, contractPointee.getType());
          SophiaAENSPointee oraclePointee = new SophiaAENSPointee(testKeyPair.getOracleAddress());
          context.assertEquals(Type.OraclePt, oraclePointee.getType());

          List<SophiaAENSPointee> testPointeeListParam =
              List.of(accountPointee, channelPointee, contractPointee, oraclePointee);
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testPointeeList",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testPointeeListParam)).build());

          JsonArray expectedPointeeListResult =
              new JsonArray()
                  .add(new JsonObject().put("AENS.AccountPt", List.of(testKeyPair.getAddress())))
                  .add(new JsonObject().put("AENS.ChannelPt", List.of(testKeyPair.getAddress())))
                  .add(new JsonObject().put("AENS.ContractPt", List.of(testKeyPair.getAddress())))
                  .add(new JsonObject().put("AENS.OraclePt", List.of(testKeyPair.getAddress())));
          context.assertEquals(expectedPointeeListResult, readOnlyResult);
        });
  }

  @Test
  public void testAensName(TestContext context) {
    this.executeTest(
        context,
        t -> {
          SophiaChainTTL chainTTL =
              new SophiaChainTTL(BigInteger.valueOf(1337L), SophiaChainTTL.Type.FixedTTL);
          SophiaAENSName aensName = new SophiaAENSName(testKeyPair.getAddress(), chainTTL, null);
          SophiaAENSPointee accountPointee = new SophiaAENSPointee(testKeyPair.getAddress());
          SophiaAENSPointee contractPointee =
              new SophiaAENSPointee(testKeyPair.getContractAddress());
          aensName.addPointer("account_test", accountPointee);
          aensName.addPointer("contract_test", contractPointee);
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testAensName",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(aensName)).build());
          SophiaAENSName mappedResult =
              SophiaTypeTransformer.getMappedResult(readOnlyResult, SophiaAENSName.class);
          context.assertEquals(aensName, mappedResult);
        });
  }
}
