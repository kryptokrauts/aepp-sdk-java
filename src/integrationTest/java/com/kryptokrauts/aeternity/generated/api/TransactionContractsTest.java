package com.kryptokrauts.aeternity.generated.api;

import java.math.BigInteger;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runners.MethodSorters;
import org.opentest4j.AssertionFailedError;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.AccountServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionContractsTest extends BaseTest {

  BaseKeyPair baseKeyPair;

  static String localDeployedContractId;

  @Before
  public void initBeforeTest() {
    baseKeyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);
  }

  @Test
  public void decodeRLPArrayTest(TestContext context) {
    try {
      Bytes value = Bytes.fromHexString(TestConstants.binaryTxDevnet);
      RLP.decodeList(
          value,
          rlpReader -> {
            Assertions.assertEquals(
                SerializationTags.OBJECT_TAG_CONTRACT_CREATE_TRANSACTION, rlpReader.readInt());
            Assertions.assertEquals(SerializationTags.VSN, rlpReader.readInt());
            Assertions.assertArrayEquals(
                rlpReader.readByteArray(),
                EncodingUtils.decodeCheckAndTag(
                    baseKeyPair.getPublicKey(), SerializationTags.ID_TAG_ACCOUNT));
            Assertions.assertEquals(BigInteger.ONE, rlpReader.readBigInteger());
            Assertions.assertArrayEquals(
                EncodingUtils.decodeCheckWithIdentifier(TestConstants.testContractByteCode),
                rlpReader.readByteArray());
            Assertions.assertEquals(
                BigInteger.valueOf(262145), new BigInteger(rlpReader.readByteArray()));
            Assertions.assertEquals(
                BigInteger.valueOf(1098660000000000l), new BigInteger(rlpReader.readByteArray()));
            Assertions.assertEquals(20000, new BigInteger(rlpReader.readByteArray()).intValue());
            Assertions.assertEquals(0, new BigInteger(rlpReader.readByteArray()).intValue());
            Assertions.assertEquals(0, new BigInteger(rlpReader.readByteArray()).intValue());
            Assertions.assertEquals(1000, new BigInteger(rlpReader.readByteArray()).intValue());
            Assertions.assertEquals(
                1100000000, new BigInteger(rlpReader.readByteArray()).intValue());
            Assertions.assertArrayEquals(
                EncodingUtils.decodeCheckWithIdentifier(TestConstants.testContractCallData),
                rlpReader.readByteArray());
            return "Validation successful";
          });
    } catch (AssertionFailedError afe) {
      _logger.error("Error decoding RLP array", afe);
      context.fail();
    }
  }

  /**
   * create an unsigned native CreateContract transaction
   *
   * @param context
   */
  @Test
  public void buildCreateContractTransactionTest(TestContext context) {
    Async async = context.async();

    String ownerId = baseKeyPair.getPublicKey();
    BigInteger abiVersion = BigInteger.ONE;
    BigInteger vmVersion = BigInteger.valueOf(4);
    BigInteger amount = BigInteger.valueOf(100);
    BigInteger deposit = BigInteger.valueOf(100);
    BigInteger ttl = BigInteger.valueOf(20000l);
    BigInteger gas = BigInteger.valueOf(1000);
    BigInteger gasPrice = BigInteger.valueOf(1100000000l);

    BigInteger nonce = BigInteger.ONE;

    AbstractTransaction<?> contractTx =
        transactionServiceNative
            .getTransactionFactory()
            .createContractCreateTransaction(
                abiVersion,
                amount,
                TestConstants.testContractCallData,
                TestConstants.testContractByteCode,
                deposit,
                gas,
                gasPrice,
                nonce,
                ownerId,
                ttl,
                vmVersion);
    contractTx.setFee(BigInteger.valueOf(1098660000000000l));

    UnsignedTx unsignedTxNative =
        transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();

    Single<UnsignedTx> unsignedTxDebugSingle =
        transactionServiceDebug.createUnsignedTransaction(contractTx);
    unsignedTxDebugSingle.subscribe(
        it -> {
          context.assertEquals(it.getTx(), unsignedTxNative.getTx());
          async.complete();
        },
        throwable -> {
          _logger.error(TestConstants.errorOccured, throwable);
          context.fail();
        });
  }

  @Test
  public void buildCallContractTransactionTest(TestContext context) {
    Async async = context.async();
    Single<Account> acc = accountService.getAccount(baseKeyPair.getPublicKey());
    acc.subscribe(
        account -> {
          String callerId = baseKeyPair.getPublicKey();
          BigInteger abiVersion = BigInteger.ONE;
          BigInteger ttl = BigInteger.valueOf(20000);
          BigInteger gas = BigInteger.valueOf(1579000);
          BigInteger gasPrice = BigInteger.valueOf(1000000000);
          BigInteger nonce = account.getNonce().add(BigInteger.ONE);
          String callContractCalldata = TestConstants.encodedServiceCall;

          AbstractTransaction<?> contractTx =
              transactionServiceNative
                  .getTransactionFactory()
                  .createContractCallTransaction(
                      abiVersion,
                      callContractCalldata,
                      localDeployedContractId,
                      gas,
                      gasPrice,
                      nonce,
                      callerId,
                      ttl);
          contractTx.setFee(BigInteger.valueOf(1454500000000000l));

          UnsignedTx unsignedTxNative =
              transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();
          _logger.info("CreateContractTx hash (native unsigned): " + unsignedTxNative.getTx());

          Single<UnsignedTx> unsignedTxDebug =
              transactionServiceDebug.createUnsignedTransaction(contractTx);
          unsignedTxDebug.subscribe(
              usd -> {
                _logger.debug("CreateContractTx hash (debug unsigned): " + usd.getTx());
                context.assertEquals(unsignedTxNative.getTx(), usd.getTx());
                async.complete();
              },
              throwable -> {
                _logger.error(TestConstants.errorOccured, throwable);
                context.fail();
              });
        },
        ex -> {
          _logger.error(TestConstants.errorOccured, ex);
          context.fail();
        });
  }

  @Test
  public void aDeployContractNativeOnLocalNode(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                Single<Account> accountSingle =
                    accountService.getAccount(baseKeyPair.getPublicKey());
                TestObserver<Account> accountTestObserver = accountSingle.test();
                accountTestObserver.awaitTerminalEvent();
                Account account = accountTestObserver.values().get(0);
                String ownerId = baseKeyPair.getPublicKey();
                BigInteger abiVersion = BigInteger.ONE;
                BigInteger vmVersion = BigInteger.valueOf(4);
                BigInteger amount = BigInteger.ZERO;
                BigInteger deposit = BigInteger.ZERO;
                BigInteger ttl = BigInteger.ZERO;
                BigInteger gas = BigInteger.valueOf(1000000);
                BigInteger gasPrice = BigInteger.valueOf(2000000000);
                BigInteger nonce = account.getNonce().add(BigInteger.ONE);

                AbstractTransaction<?> contractTx =
                    transactionServiceNative
                        .getTransactionFactory()
                        .createContractCreateTransaction(
                            abiVersion,
                            amount,
                            TestConstants.testContractCallData,
                            TestConstants.testContractByteCode,
                            deposit,
                            gas,
                            gasPrice,
                            nonce,
                            ownerId,
                            ttl,
                            vmVersion);

                UnsignedTx unsignedTxNative =
                    transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();

                Tx signedTxNative =
                    transactionServiceNative.signTransaction(
                        unsignedTxNative, baseKeyPair.getPrivateKey());
                _logger.info("CreateContractTx hash (native signed): " + signedTxNative);

                Single<PostTxResponse> txResponse =
                    transactionServiceNative.postTransaction(signedTxNative);
                TestObserver<PostTxResponse> postTxResponseTestObserver = txResponse.test();
                postTxResponseTestObserver.awaitTerminalEvent();
                postTxResponseTestObserver.assertNoErrors();
                postTxResponseTestObserver.assertComplete();
                PostTxResponse postTxResponse = postTxResponseTestObserver.values().get(0);

                Single<TxInfoObject> txInfoObjectSingle =
                    transactionServiceNative.getTransactionInfoByHash(postTxResponse.getTxHash());
                TestObserver<TxInfoObject> txInfoObjectTestObserver = txInfoObjectSingle.test();
                txInfoObjectTestObserver.awaitTerminalEvent();
                txInfoObjectTestObserver.assertNoErrors();
                txInfoObjectTestObserver.assertComplete();
                TxInfoObject txInfoObject = txInfoObjectTestObserver.values().get(0);

                _logger.info(txInfoObject.toString());

                localDeployedContractId = txInfoObject.getCallInfo().getContractId();
              } catch (Exception e) {
                _logger.error(TestConstants.errorOccured, e);
                context.fail();
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void callContractOnLocalNodeTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                Single<Account> accountSingle =
                    accountService.getAccount(baseKeyPair.getPublicKey());
                TestObserver<Account> accountTestObserver = accountSingle.test();
                accountTestObserver.awaitTerminalEvent();
                Account account = accountTestObserver.values().get(0);
                String callerId = baseKeyPair.getPublicKey();
                BigInteger abiVersion = BigInteger.ONE;
                BigInteger ttl = BigInteger.ZERO;
                BigInteger gas = BigInteger.valueOf(1579000);
                BigInteger gasPrice = BigInteger.valueOf(1000000000);
                BigInteger nonce = account.getNonce().add(BigInteger.ONE);

                Single<Calldata> callDataSingle =
                    this.sophiaCompilerService.encodeCalldata(
                        TestConstants.testContractSourceCode,
                        TestConstants.testContractFunction,
                        TestConstants.testContractFunctionParams);
                TestObserver<Calldata> calldataTestObserver = callDataSingle.test();
                calldataTestObserver.awaitTerminalEvent();
                calldataTestObserver.assertNoErrors();
                calldataTestObserver.assertComplete();
                Calldata callData = calldataTestObserver.values().get(0);

                AbstractTransaction<?> contractTx =
                    transactionServiceNative
                        .getTransactionFactory()
                        .createContractCallTransaction(
                            abiVersion,
                            callData.getCalldata(),
                            localDeployedContractId,
                            gas,
                            gasPrice,
                            nonce,
                            callerId,
                            ttl);

                UnsignedTx unsignedTxNative =
                    transactionServiceNative.createUnsignedTransaction(contractTx).blockingGet();
                Tx signedTxNative =
                    transactionServiceNative.signTransaction(
                        unsignedTxNative, baseKeyPair.getPrivateKey());

                // post the signed contract call tx
                Single<PostTxResponse> postTxResponseSingle =
                    transactionServiceNative.postTransaction(signedTxNative);
                TestObserver<PostTxResponse> postTxResponseTestObserver =
                    postTxResponseSingle.test();
                postTxResponseTestObserver.awaitTerminalEvent();
                postTxResponseTestObserver.assertNoErrors();
                postTxResponseTestObserver.assertComplete();
                PostTxResponse postTxResponse = postTxResponseTestObserver.values().get(0);
                context.assertEquals(
                    postTxResponse.getTxHash(),
                    transactionServiceNative.computeTxHash(signedTxNative.getTx()));
                _logger.info("CreateContractTx hash: " + postTxResponse.getTxHash());

                // get the tx info object to resolve the result
                Single<TxInfoObject> txInfoObjectSingle =
                    transactionServiceNative.getTransactionInfoByHash(postTxResponse.getTxHash());
                TestObserver<TxInfoObject> txInfoObjectTestObserver = txInfoObjectSingle.test();
                txInfoObjectTestObserver.awaitTerminalEvent();
                txInfoObjectTestObserver.assertNoErrors();
                txInfoObjectTestObserver.assertComplete();
                TxInfoObject txInfoObject = txInfoObjectTestObserver.values().get(0);

                // decode the result to json
                Single<SophiaJsonData> sophiaJsonDataSingle =
                    this.sophiaCompilerService.decodeCalldata(
                        txInfoObject.getCallInfo().getReturnValue(),
                        TestConstants.testContractFunctionSophiaType);
                TestObserver<SophiaJsonData> sophiaJsonDataTestObserver =
                    sophiaJsonDataSingle.test();
                sophiaJsonDataTestObserver.awaitTerminalEvent();
                sophiaJsonDataTestObserver.assertNoErrors();
                sophiaJsonDataTestObserver.assertComplete();
                SophiaJsonData sophiaJsonData = sophiaJsonDataTestObserver.values().get(0);
                JsonObject json = JsonObject.mapFrom(sophiaJsonData.getData());
                context.assertEquals(
                    TestConstants.testContractFuntionParam, json.getValue("value").toString());
              } catch (Exception e) {
                _logger.error(TestConstants.errorOccured, e);
                context.fail();
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  @Ignore // specific testcase we don't want to run each time
  public void deployContractNativeOnTestNetworkTest(TestContext context) {
    Async async = context.async();

    baseKeyPair =
        keyPairService.generateBaseKeyPairFromSecret(TestConstants.testnetAccountPrivateKey);

    AccountService testnetAccountService = new AccountServiceFactory().getService();
    TransactionService testnetTransactionService =
        new TransactionServiceFactory()
            .getService(
                TransactionServiceConfiguration.configure()
                    .baseUrl(TestConstants.testnetURL)
                    .network(Network.TESTNET)
                    .vertx(rule.vertx())
                    .compile());

    Single<Account> acc = testnetAccountService.getAccount(baseKeyPair.getPublicKey());
    acc.subscribe(
        account -> {
          String ownerId = baseKeyPair.getPublicKey();
          BigInteger abiVersion = BigInteger.ONE;
          BigInteger vmVersion = BigInteger.valueOf(4);
          BigInteger amount = BigInteger.ZERO;
          BigInteger deposit = BigInteger.ZERO;
          BigInteger ttl = BigInteger.ZERO;
          BigInteger gas = BigInteger.valueOf(1000);
          BigInteger gasPrice = BigInteger.valueOf(1100000000);
          BigInteger nonce = account.getNonce().add(BigInteger.ONE);

          AbstractTransaction<?> contractTx =
              testnetTransactionService
                  .getTransactionFactory()
                  .createContractCreateTransaction(
                      abiVersion,
                      amount,
                      TestConstants.testContractCallData,
                      TestConstants.testContractByteCode,
                      deposit,
                      gas,
                      gasPrice,
                      nonce,
                      ownerId,
                      ttl,
                      vmVersion);

          UnsignedTx unsignedTxNative =
              testnetTransactionService.createUnsignedTransaction(contractTx).blockingGet();

          Tx signedTxNative =
              testnetTransactionService.signTransaction(
                  unsignedTxNative, baseKeyPair.getPrivateKey());

          Single<PostTxResponse> txResponse =
              testnetTransactionService.postTransaction(signedTxNative);
          txResponse.subscribe(
              it -> {
                context.assertEquals(
                    it.getTxHash(),
                    testnetTransactionService.computeTxHash(signedTxNative.getTx()));
                async.complete();
              },
              throwable -> {
                System.out.println("error occured deploy on testnetwork:");
                throwable.printStackTrace();
                /** we accept errors on testnet in case of lower ttl / nonce */
                async.complete();
              });
        },
        ex -> {
          _logger.error(TestConstants.errorOccured, ex);
          context.fail();
        });
  }
}
