package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.sophia.compiler.generated.model.ByteCode;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.naming.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class PaymentSplitterContractTest extends BaseTest {

  BaseKeyPair baseKeyPair;
  String paymentSplitterSource;
  String localDeployedContractId;
  protected CompilerService sophiaCompilerService;

  @Before
  public void initBeforeTest() throws IOException, ConfigurationException {
    baseKeyPair = keyPairService.generateBaseKeyPairFromSecret(BENEFICIARY_PRIVATE_KEY);
    final InputStream inputStream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("contracts/PaymentSplitter.aes");
    paymentSplitterSource = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
    if (sophiaCompilerService == null) {
      Vertx vertx = rule.vertx();
      sophiaCompilerService =
          new CompilerServiceFactory()
              .getService(
                  ServiceConfiguration.configure()
                      .contractBaseUrl(getCompilerBaseUrl())
                      .vertx(vertx)
                      .compile());
    }
  }

  @Test
  public void compilePaymentSplitterTest(TestContext context) {
    Async async = context.async();
    rule.vertx()
        .executeBlocking(
            future -> {
              try {
                Single<ByteCode> byteCodeSingle =
                    this.sophiaCompilerService.compile(paymentSplitterSource, null, null);
                TestObserver<ByteCode> byteCodeTestObserver = byteCodeSingle.test();
                byteCodeTestObserver.awaitTerminalEvent();
                ByteCode byteCode = byteCodeTestObserver.values().get(0);

                Single<Calldata> calldataSingle =
                    this.sophiaCompilerService.encodeCalldata(
                        paymentSplitterSource,
                        "init",
                        Arrays.asList(
                            "[{'ak_YZj5WoWTBvga5MBXHa49SLaScbqbop2NVn5WLDsbXwS71aFrM': 40}, {'ak_ERsQrUnSN7RX3NNGRbuu9skZuBJ6cJLHwcE4FZ2XAotKoR6bo': 40}, {'ak_TtQYek584v6M2XYEKzBMPRqdat8FSUr2dqNshvN14RjvX6q8W': 20}]"));
                TestObserver<Calldata> calldataTestObserver = calldataSingle.test();
                calldataTestObserver.awaitTerminalEvent();
                if (calldataTestObserver.errorCount() > 0) {
                  context.fail(calldataTestObserver.errors().get(0));
                }
                Calldata calldata = calldataTestObserver.values().get(0);

                _logger.debug(byteCode.getBytecode());
                _logger.debug(calldata.getCalldata());

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
                            calldata.getCalldata(),
                            byteCode.getBytecode(),
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
                PostTxResponse postTxResponse = postTxResponseTestObserver.values().get(0);
                do {
                  Single<TxInfoObject> txInfoObjectSingle =
                      transactionServiceNative.getTransactionInfoByHash(postTxResponse.getTxHash());
                  TestObserver<TxInfoObject> txInfoObjectTestObserver = txInfoObjectSingle.test();
                  txInfoObjectTestObserver.awaitTerminalEvent();
                  if (txInfoObjectTestObserver.errorCount() > 0) {
                    _logger.warn("unable to receive txInfoObject. trying again in 1 second ...");
                    Thread.sleep(1000);
                  } else {
                    TxInfoObject txInfoObject = txInfoObjectTestObserver.values().get(0);
                    localDeployedContractId = txInfoObject.getCallInfo().getContractId();
                    _logger.info(
                        "Deployed contract - hash "
                            + postTxResponse.getTxHash()
                            + " - "
                            + txInfoObject);
                  }
                } while (localDeployedContractId == null);

              } catch (Exception e) {
                context.fail(e);
              }
              future.complete();
            },
            success -> async.complete());
  }

  @Test
  public void generateKeypair() {
    _logger.info(new KeyPairServiceFactory().getService().generateBaseKeyPair().getPublicKey());
    _logger.info(new KeyPairServiceFactory().getService().generateBaseKeyPair().getPublicKey());
    _logger.info(new KeyPairServiceFactory().getService().generateBaseKeyPair().getPublicKey());
  }
}
