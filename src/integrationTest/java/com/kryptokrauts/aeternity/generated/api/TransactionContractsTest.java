package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;
import org.bouncycastle.crypto.CryptoException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runners.MethodSorters;
import org.opentest4j.AssertionFailedError;

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

  /**
   * create debug and a native unsigned transaction and compare against a given correct sample
   * unsigned transactions
   *
   * @param context
   */
  @Test
  public void buildCreateContractUnsignedTransactionTest(TestContext context) {
    Async async = context.async();
    String ownerId = baseKeyPair.getPublicKey();
    BigInteger abiVersion = BigInteger.ONE;
    BigInteger vmVersion = BigInteger.valueOf(4);
    BigInteger amount = BigInteger.ZERO;
    BigInteger deposit = BigInteger.ZERO;
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

    Single<UnsignedTx> unsignedTxDebug =
        transactionServiceDebug.createUnsignedTransaction(contractTx);
    unsignedTxDebug.subscribe(
        it -> {
          _logger.info("Devnet Tx:	" + TestConstants.base64TxDevnet);
          _logger.info("Native Tx: 	" + unsignedTxNative.getTx());
          _logger.info("Debug Tx: 	" + it.getTx());

          context.assertEquals(TestConstants.base64TxDevnet, it.getTx());
          context.assertEquals(TestConstants.base64TxDevnet, unsignedTxNative.getTx());
          async.complete();
        },
        throwable -> {
          _logger.error(TestConstants.errorOccured, throwable);
          context.fail();
        });
  }

  @Test
  @Ignore // specific testcase we don't want to run each time
  public void deployContractNativeOnTestNetworkTest(TestContext context)
      throws ExecutionException, InterruptedException, CryptoException {
    Async async = context.async();

    baseKeyPair =
        keyPairService.generateBaseKeyPairFromSecret(TestConstants.testnetAccountPrivateKey);

    TransactionService testnetTransactionService =
        new TransactionServiceFactory()
            .getService(
                TransactionServiceConfiguration.configure()
                    .baseUrl(TestConstants.testnetURL)
                    .network(Network.TESTNET)
                    .vertx(rule.vertx())
                    .compile());

    String ownerId = baseKeyPair.getPublicKey();
    BigInteger abiVersion = BigInteger.ONE;
    BigInteger vmVersion = BigInteger.valueOf(4);
    BigInteger amount = BigInteger.ZERO;
    BigInteger deposit = BigInteger.ZERO;
    BigInteger ttl = BigInteger.valueOf(120000);
    BigInteger gas = BigInteger.valueOf(1000);
    BigInteger gasPrice = BigInteger.valueOf(1100000000);
    // resolve this from https://testnet.contracts.aepps.com/
    BigInteger nonce = BigInteger.valueOf(13014);

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
        testnetTransactionService.signTransaction(unsignedTxNative, baseKeyPair.getPrivateKey());

    Single<PostTxResponse> txResponse = testnetTransactionService.postTransaction(signedTxNative);
    txResponse.subscribe(
        it -> {
          context.assertEquals(
              it.getTxHash(), testnetTransactionService.computeTxHash(signedTxNative.getTx()));
          async.complete();
        },
        throwable -> {
          System.out.println("error occured deploy on testnetwork:");
          throwable.printStackTrace();
          /** we accept errors on testnet in case of lower ttl / nonce */
          async.complete();
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
          BigInteger amount = BigInteger.valueOf(100);
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
                      amount,
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
  public void callContractOnLocalNodeTest(TestContext context) {
    Async async = context.async();
    Single<Account> acc = accountService.getAccount(baseKeyPair.getPublicKey());
    acc.subscribe(
        account -> {
          String callerId = baseKeyPair.getPublicKey();
          BigInteger abiVersion = BigInteger.ONE;
          BigInteger amount = BigInteger.valueOf(100);
          BigInteger ttl = BigInteger.valueOf(20000);
          BigInteger gas = BigInteger.valueOf(1579000);
          BigInteger gasPrice = BigInteger.valueOf(1000000000);
          BigInteger nonce = account.getNonce().add(BigInteger.ONE);

          Single<Calldata> encodedCallData =
              this.sophiaCompilerService.encodeCalldata(
                  TestConstants.testContractSourceCode,
                  TestConstants.testContractFunction,
                  TestConstants.testContractFunctionParams);

          // Encode the contract call
          encodedCallData.subscribe(
              encodedCD -> {
                AbstractTransaction<?> contractTx =
                    transactionServiceNative
                        .getTransactionFactory()
                        .createContractCallTransaction(
                            abiVersion,
                            amount,
                            encodedCD.getCalldata(),
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
                Single<PostTxResponse> txResponse =
                    transactionServiceNative.postTransaction(signedTxNative);
                txResponse.subscribe(
                    it -> {
                      context.assertEquals(
                          it.getTxHash(),
                          transactionServiceNative.computeTxHash(signedTxNative.getTx()));
                      _logger.info("CreateContractTx hash: " + it.getTxHash());
                      Thread.sleep(250);
                      // get the tx info object to resolve the result
                      Single<TxInfoObject> info =
                          transactionServiceNative.getTransactionInfoByHash(it.getTxHash());
                      info.subscribe(
                          infoObject -> {
                            // decode the result to json
                            Single<SophiaJsonData> result =
                                this.sophiaCompilerService.decodeCalldata(
                                    infoObject.getCallInfo().getReturnValue(),
                                    TestConstants.testContractFunctionSophiaType);
                            result.subscribe(
                                resultjson -> {
                                  JsonObject json = JsonObject.mapFrom(resultjson.getData());
                                  context.assertEquals(
                                      TestConstants.testContractFuntionParam,
                                      json.getValue("value").toString());
                                  async.complete();
                                },
                                throwable -> {
                                  context.fail();
                                });
                          },
                          throwable -> {
                            context.fail();
                          });
                    },
                    throwable -> {
                      _logger.error(TestConstants.errorOccured, throwable);
                      context.fail();
                    });
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
  public void aDeployContractNativeOnLocalNode(TestContext context) throws InterruptedException {
    Async async = context.async();
    Single<Account> acc = accountService.getAccount(baseKeyPair.getPublicKey());
    acc.subscribe(
        account -> {
          String ownerId = baseKeyPair.getPublicKey();
          BigInteger abiVersion = BigInteger.ONE;
          BigInteger vmVersion = BigInteger.valueOf(4);
          BigInteger amount = BigInteger.valueOf(100);
          BigInteger deposit = BigInteger.valueOf(100);
          BigInteger ttl = BigInteger.valueOf(20000);
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
          txResponse.subscribe(
              it -> {
                context.assertEquals(
                    it.getTxHash(), transactionServiceNative.computeTxHash(signedTxNative.getTx()));
                _logger.info("CreateContractTx hash: " + it.getTxHash());
                // wait until contract is available
                Thread.sleep(500);
                Single<TxInfoObject> info =
                    transactionServiceNative.getTransactionInfoByHash(it.getTxHash());
                info.subscribe(
                    infoObject -> {
                      localDeployedContractId = infoObject.getCallInfo().getContractId();
                      async.complete();
                    },
                    throwable -> {
                      context.fail();
                    });
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
}
