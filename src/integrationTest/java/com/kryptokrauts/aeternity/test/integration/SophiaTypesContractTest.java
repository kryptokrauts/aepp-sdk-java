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
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaTuple;
import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaTypeTransformer;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxOptions;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxResult;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
          SophiaAENSPointee mappedResult =
              SophiaTypeTransformer.getMappedResult(readOnlyResult, SophiaAENSPointee.class);
          context.assertEquals(testPointeeParam, mappedResult);
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

  @Test
  public void testTtl(TestContext context) {
    this.executeTest(
        context,
        t -> {
          SophiaChainTTL relativeTtlParam =
              new SophiaChainTTL(BigInteger.valueOf(1000L), SophiaChainTTL.Type.RelativeTTL);
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testTtl",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(relativeTtlParam)).build());
          SophiaChainTTL mappedResult =
              SophiaTypeTransformer.getMappedResult(readOnlyResult, SophiaChainTTL.class);
          context.assertEquals(relativeTtlParam, mappedResult);

          SophiaChainTTL fixedTtlParam =
              new SophiaChainTTL(BigInteger.valueOf(1337L), SophiaChainTTL.Type.FixedTTL);
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testTtl",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(fixedTtlParam)).build());
          mappedResult =
              SophiaTypeTransformer.getMappedResult(readOnlyResult, SophiaChainTTL.class);
          context.assertEquals(fixedTtlParam, mappedResult);
        });
  }

  @Test
  public void testOracleQuery(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String testOracleQueryParam = "oq_2crnhCkYVabbVpJRHZdZD45Ffuv3tn4P6B293TmVgT1GRxLG2y";
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testOracleQuery",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testOracleQueryParam)).build());
          context.assertEquals(testOracleQueryParam, readOnlyResult);
        });
  }

  @Test
  public void testOption(TestContext context) {
    this.executeTest(
        context,
        t -> {
          Optional<BigInteger> testEmptyIntParam = Optional.empty();
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testOptionInt",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testEmptyIntParam)).build());
          _logger.info(readOnlyResult.toString());
          Optional<BigInteger> testIntParam = Optional.of(BigInteger.valueOf(1337L));
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testOptionInt",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testIntParam)).build());
          _logger.info(readOnlyResult.toString());

          Optional<SophiaString> testEmptyStringParam = Optional.empty();
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testOptionStr",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testEmptyStringParam)).build());
          _logger.info(readOnlyResult.toString());
          Optional<SophiaString> testStringParam = Optional.of(new SophiaString("kryptokrauts"));
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testOptionStr",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testStringParam)).build());
          _logger.info(readOnlyResult.toString());

          Optional<List<BigInteger>> testEmptyListParam = Optional.empty();
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testOptionList",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testEmptyStringParam)).build());
          _logger.info(readOnlyResult.toString());
          Optional<List<BigInteger>> testListIntParam =
              Optional.of(List.of(BigInteger.valueOf(1337L), BigInteger.valueOf(9999L)));
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testOptionList",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testListIntParam)).build());
          _logger.info(readOnlyResult.toString());
        });
  }

  @Test
  public void testTuple(TestContext context) {
    this.executeTest(
        context,
        t -> {
          SophiaTuple testTupleParam =
              new SophiaTuple(
                  List.of(BigInteger.valueOf(1337L), new SophiaString("kryptokrauts"), true));
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testTuple",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testTupleParam)).build());
          JsonArray expectedResult = new JsonArray();
          expectedResult.add(1337);
          expectedResult.add("kryptokrauts");
          expectedResult.add(true);
          context.assertEquals(expectedResult, readOnlyResult);
        });
  }

  @Test
  public void testListString(TestContext context) {
    this.executeTest(
        context,
        t -> {
          List<SophiaString> testStringListParam = new ArrayList<>();
          testStringListParam.add(new SophiaString("abc"));
          testStringListParam.add(new SophiaString("xyz"));
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testListString",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testStringListParam)).build());
          JsonArray expectedResult = new JsonArray();
          expectedResult.add("abc");
          expectedResult.add("xyz");
          context.assertEquals(expectedResult, readOnlyResult);
        });
  }

  @Test
  public void testListAddressInt(TestContext context) {
    this.executeTest(
        context,
        t -> {
          SophiaTuple firstPair =
              new SophiaTuple(List.of(keyPair.getAddress(), BigInteger.valueOf(1)));
          SophiaTuple secondPair = new SophiaTuple(List.of(testKeyPair.getAddress(), 2));
          List<SophiaTuple> testListAddressIntParam = List.of(firstPair, secondPair);
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testListAddressInt",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testListAddressIntParam)).build());
          JsonArray expectedResult = new JsonArray();
          expectedResult.add(List.of(keyPair.getAddress(), 1));
          expectedResult.add(List.of(testKeyPair.getAddress(), 2));
          context.assertEquals(expectedResult, readOnlyResult);
        });
  }

  @Test
  public void testMapOfMaps(TestContext context) {
    this.executeTest(
        context,
        t -> {
          Map<BigInteger, Map<BigInteger, BigInteger>> testMapOfMapsParam = new HashMap<>();
          testMapOfMapsParam.put(BigInteger.ONE, Map.of(BigInteger.ONE, BigInteger.ONE));
          testMapOfMapsParam.put(BigInteger.TWO, Map.of(BigInteger.TWO, BigInteger.TWO));
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testMapOfMaps",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testMapOfMapsParam)).build());
          _logger.info(readOnlyResult.toString());
        });
  }

  @Test
  public void testListListMap(TestContext context) {
    this.executeTest(
        context,
        t -> {
          List<List<Map<BigInteger, BigInteger>>> testListListMapParam = new ArrayList<>();
          testListListMapParam.add(
              List.of(
                  Map.of(BigInteger.ONE, BigInteger.ONE), Map.of(BigInteger.TWO, BigInteger.TWO)));
          testListListMapParam.add(List.of(Map.of(BigInteger.TEN, BigInteger.TEN)));
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testListListMap",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testListListMapParam)).build());
          _logger.info(readOnlyResult.toString());
        });
  }

  @Test
  public void testEmployee(TestContext context) {
    this.executeTest(
        context,
        t -> {
          JsonObject testEmployeeParam = new JsonObject();
          testEmployeeParam.put("address", testKeyPair.getAddress());
          // compiler value needs to be set explicitly as custom types are not allowed in JsonObject
          testEmployeeParam.put("firstname", new SophiaString("Satoshi").getCompilerValue());
          testEmployeeParam.put("lastname", new SophiaString("Nakamoto").getCompilerValue());
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testEmployee",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testEmployeeParam)).build());
          _logger.info(readOnlyResult.toString());
        });
  }

  @Test
  public void testCompanyAddress(TestContext context) {
    this.executeTest(
        context,
        t -> {
          JsonObject testCompanyAddressParam = new JsonObject();
          testCompanyAddressParam.put("zip", 1337);
          testCompanyAddressParam.put("street", new SophiaString("Elm Street").getCompilerValue());
          testCompanyAddressParam.put("city", new SophiaString("Dream City").getCompilerValue());
          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testCompanyAddress",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testCompanyAddressParam)).build());
          _logger.info(readOnlyResult.toString());
        });
  }

  @Test
  public void testCompany(TestContext context) {
    this.executeTest(
        context,
        t -> {
          JsonObject employee = new JsonObject();
          employee.put("address", testKeyPair.getAddress());
          employee.put("firstname", new SophiaString("Satoshi").getCompilerValue());
          employee.put("lastname", new SophiaString("Nakamoto").getCompilerValue());
          JsonObject location = new JsonObject();
          location.put("zip", 1337);
          location.put("street", new SophiaString("Elm Street").getCompilerValue());
          location.put("city", new SophiaString("Dream City").getCompilerValue());

          JsonObject testCompanyParam =
              new JsonObject(
                  Map.of(
                      "ceo",
                      keyPair.getAddress(),
                      "shareholders",
                      Map.of(
                          testKeyPair.getAddress(),
                          50,
                          keyPairService.generateKeyPair().getAddress(),
                          50),
                      "employees",
                      List.of(employee),
                      "location",
                      location));

          readOnlyResult =
              aeternityService.transactions.blockingReadOnlyContractCall(
                  contractId,
                  "testCompany",
                  sophiaTypesSource,
                  ContractTxOptions.builder().params(List.of(testCompanyParam)).build());
          _logger.info(readOnlyResult.toString());
        });
  }
}
