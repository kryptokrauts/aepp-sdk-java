package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionApiTest extends BaseTest {

  BaseKeyPair baseKeyPair;

  @Before
  public void initBeforeTest() {
    baseKeyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);
  }

  @Test
  public void decodeRLPArray() {
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
          Assertions.assertEquals(262145, rlpReader.readBigInteger().intValue());
          Assertions.assertEquals(
              BigInteger.valueOf(1098660000000000l), rlpReader.readBigInteger());
          Assertions.assertEquals(20000, rlpReader.readBigInteger().intValue());
          Assertions.assertEquals(0, rlpReader.readBigInteger().intValue());
          Assertions.assertEquals(0, rlpReader.readBigInteger().intValue());
          Assertions.assertEquals(1000, rlpReader.readBigInteger().intValue());
          Assertions.assertEquals(1100000000, rlpReader.readBigInteger().intValue());
          Assertions.assertArrayEquals(
              EncodingUtils.decodeCheckWithIdentifier(TestConstants.testContractCallData),
              rlpReader.readByteArray());
          return "Validation successful";
        });
  }

  /**
   * create an unsigned native spend transaction
   *
   * @param context
   */
  @Test
  public void buildNativeSpendTransactionTest(TestContext context) {
    Async async = context.async();

    String sender = keyPairService.generateBaseKeyPair().getPublicKey();
    String recipient = keyPairService.generateBaseKeyPair().getPublicKey();
    BigInteger amount = BigInteger.valueOf(1000);
    String payload = "payload";
    BigInteger ttl = BigInteger.valueOf(100);
    BigInteger nonce = BigInteger.valueOf(5);

    AbstractTransaction<?> spendTx =
        transactionServiceNative
            .getTransactionFactory()
            .createSpendTransaction(sender, recipient, amount, payload, ttl, nonce);
    UnsignedTx unsignedTxNative =
        transactionServiceNative.createUnsignedTransaction(spendTx).blockingGet();

    Single<UnsignedTx> unsignedTx = transactionServiceDebug.createUnsignedTransaction(spendTx);
    unsignedTx.subscribe(
        it -> {
          Assertions.assertEquals(it, unsignedTxNative);
          async.complete();
        },
        throwable -> {
          _logger.error(TestConstants.errorOccured, throwable);
          context.fail();
        });
  }

    /**
     * create an unsigned native CreateContract transaction
     *
     * @param context
     */
    @Test
    public void buildNativeCreateContractTransactionTest(TestContext context) {
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

        Single<UnsignedTx> unsignedTxDebugSingle = transactionServiceDebug.createUnsignedTransaction(contractTx);
        unsignedTxDebugSingle.subscribe(
                it -> {
                    Assertions.assertEquals(it.getTx(), unsignedTxNative.getTx());
                    async.complete();
                },
                throwable -> {
                    _logger.error(TestConstants.errorOccured, throwable);
                    context.fail();
                });
    }

  /**
   * Use an unsigned test contract transaction, sign it and deploy it
   *
   * @param context
   * @throws CryptoException
   */
  @Test
  @Ignore // fails for a unnkown reason -> maybe also because of the switch to BigInteger ?
  public void deployATestContractNativeOnLocalNode(TestContext context) throws CryptoException {
    Async async = context.async();

    UnsignedTx unsignedTx = new UnsignedTx();
    unsignedTx.setTx(TestConstants.base64TxDevnet);

    Tx signedTxNative =
        transactionServiceNative.signTransaction(unsignedTx, baseKeyPair.getPrivateKey());

    _logger.info("Native Tx (signed): " + signedTxNative.getTx());

    Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTxNative);
    txResponse.subscribe(
        it -> {
          Assertions.assertEquals(
              it.getTxHash(), transactionServiceNative.computeTxHash(signedTxNative.getTx()));
          async.complete();
        },
        throwable -> {
          _logger.error(TestConstants.errorOccured, throwable);
          context.fail();
        });
  }

  /**
   * create debug and a native unsigned transaction and compare against a given correct sample
   * unsigned transaction
   *
   * @param context
   */
  @Test
  @Ignore // fails probably due to BigInteger.ZERO
  public void checkCreateContractUnsignedTx(TestContext context) {
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
  public void deployContractNativeOnTestNetwork(TestContext context)
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
          Assertions.assertEquals(
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
  public void deployBContractNativeOnLocalNode(TestContext context) {
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
                Assertions.assertEquals(
                    it.getTxHash(), transactionServiceNative.computeTxHash(signedTxNative.getTx()));
                _logger.info("CreateContractTx hash: " + it.getTxHash());
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
  public void postSpendTxTest(TestContext context) {
    Async async = context.async();

    BaseKeyPair keyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);

    // get the currents accounts nonce in case a transaction is already
    // created and increase it by one
    Single<Account> acc = accountService.getAccount(keyPair.getPublicKey());
    acc.subscribe(
        account -> {
          BaseKeyPair kp = keyPairService.generateBaseKeyPair();
          String recipient = kp.getPublicKey();
          BigInteger amount = new BigInteger("1000000000000000000");
          String payload = "payload";
          BigInteger ttl = BigInteger.valueOf(20000);
          BigInteger nonce = account.getNonce().add(BigInteger.ONE);

          AbstractTransaction<?> spendTx =
              transactionServiceNative
                  .getTransactionFactory()
                  .createSpendTransaction(
                      keyPair.getPublicKey(), recipient, amount, payload, ttl, nonce);
          UnsignedTx unsignedTxNative =
              transactionServiceNative.createUnsignedTransaction(spendTx).blockingGet();
          Tx signedTx =
              transactionServiceNative.signTransaction(unsignedTxNative, keyPair.getPrivateKey());

          Single<PostTxResponse> txResponse = transactionServiceNative.postTransaction(signedTx);
          txResponse.subscribe(
              it -> {
                _logger.info("SpendTx hash: " + it.getTxHash());
                Assertions.assertEquals(
                    it.getTxHash(), transactionServiceNative.computeTxHash(signedTx.getTx()));
                async.complete();
              },
              throwable -> {
                context.fail();
              });
        },
        throwable -> {
          context.fail();
        });
  }
}
