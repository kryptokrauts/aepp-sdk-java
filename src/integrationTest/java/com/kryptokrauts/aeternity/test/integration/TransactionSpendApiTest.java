package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.account.domain.NextNonceStrategy;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.CheckTxInPoolResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import io.reactivex.Single;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionSpendApiTest extends BaseTest {

  @Test
  public void postSpendSelfSignTxTest(TestContext context) {
    this.executeTest(
        context,
        t -> {

          // get the currents accounts nonce in case a transaction is already
          // created and increase it by one
          AccountResult account = this.aeternityService.accounts.blockingGetAccount();

          KeyPair kp = keyPairService.generateKeyPair();
          String recipient = kp.getAddress();
          BigInteger amount = new BigInteger("1000000000000000000");
          String payload = "payload";
          BigInteger nonce = account.getNonce().add(ONE);

          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(recipient)
                  .amount(amount)
                  .payload(payload)
                  .nonce(nonce)
                  .build();

          PostTransactionResult txResponse =
              aeternityService.transactions.blockingPostTransaction(spendTx);
          _logger.info("SpendTx hash: " + txResponse.getTxHash());
          context.assertEquals(
              txResponse.getTxHash(), aeternityService.transactions.computeTxHash(spendTx));
          try {
            waitForTxMined(txResponse.getTxHash());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void postSpendSignedTxString(TestContext context) {
    this.executeTest(
        context,
        t -> {
          AccountResult account = this.aeternityService.accounts.blockingGetAccount();

          KeyPair kp = keyPairService.generateKeyPair();
          String recipient = kp.getAddress();
          BigInteger amount = new BigInteger("1000000000000000000");
          String payload = "payload";
          BigInteger nonce = account.getNonce().add(ONE);

          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(recipient)
                  .amount(amount)
                  .payload(payload)
                  .nonce(nonce)
                  .build();

          String unsignedTxNative =
              aeternityService
                  .transactions
                  .asyncCreateUnsignedTransaction(spendTx)
                  .blockingGet()
                  .getResult();

          String signedTxNative =
              aeternityService.transactions.signTransaction(
                  unsignedTxNative, keyPair.getEncodedPrivateKey());

          PostTransactionResult txResponse =
              aeternityService.transactions.blockingPostTransaction(signedTxNative);

          _logger.info("SpendTx hash: " + txResponse.getTxHash());
          context.assertEquals(
              txResponse.getTxHash(), aeternityService.transactions.computeTxHash(spendTx));
          try {
            waitForTxMined(txResponse.getTxHash());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void postSpendTxWaitForConfirmationOnErrorTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          AccountResult account = this.aeternityService.accounts.blockingGetAccount();

          KeyPair kp = keyPairService.generateKeyPair();
          String recipient = kp.getAddress();
          BigInteger amount = new BigInteger("1000000000000000000");
          String payload = "payload";
          BigInteger nonce = account.getNonce().add(ONE);

          SpendTransactionModel spendTx =
              SpendTransactionModel.builder()
                  .sender(account.getPublicKey())
                  .recipient(recipient)
                  .amount(amount)
                  .payload(payload)
                  .nonce(nonce)
                  // fee is set on purpose way to low
                  .fee(BigInteger.ONE)
                  .build();

          try {
            aeternityService.transactions.blockingPostTransaction(spendTx);
            context.fail("Test failed because no AException raised, test contains error");
          } catch (AException spendException) {
            context.assertTrue(
                spendException
                    .getMessage()
                    .contains(
                        "An error occured while waiting for transaction to be included in block"));
          }
        });
  }

  @Test
  public void postSpendSelfSignTxTestWithModel(TestContext context)
      throws TransactionCreateException {
    this.executeTest(
        context,
        t -> {
          try {
            KeyPair recipient = keyPairService.generateKeyPair();
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(this.keyPair.getAddress())
                    .recipient(recipient.getAddress())
                    .amount(new BigInteger("1000000000000000000"))
                    .payload("donation")
                    .nonce(getNextKeypairNonce())
                    .build();
            PostTransactionResult txResponse =
                aeternityService.transactions.blockingPostTransaction(spendTx);
            _logger.info("SpendTx hash: " + txResponse.getTxHash());
            waitForTxMined(txResponse.getTxHash());
            context.assertEquals(
                txResponse.getTxHash(), aeternityService.transactions.computeTxHash(spendTx));
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void postSpendTxWithModelAndPK(TestContext context) throws TransactionCreateException {
    this.executeTest(
        context,
        t -> {
          try {
            KeyPair recipient = keyPairService.generateKeyPair();
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(this.keyPair.getAddress())
                    .recipient(recipient.getAddress())
                    .amount(new BigInteger("1000000000000000000"))
                    .payload("donation")
                    .nonce(getNextKeypairNonce())
                    .build();
            PostTransactionResult txResponse =
                aeternityService.transactions.blockingPostTransaction(spendTx);
            _logger.info("SpendTx hash: " + txResponse.getTxHash());
            waitForTxMined(txResponse.getTxHash());
            AccountResult recipientAccount =
                this.aeternityService.accounts.blockingGetAccount(recipient.getAddress());
            _logger.info("Account result for recipient {}", recipientAccount);
            // now send amount back
            long recipientAccountBalance = recipientAccount.getBalance().longValue();
            long recipientAccountSendAmount = 10000000l;
            spendTx =
                SpendTransactionModel.builder()
                    .sender(recipient.getAddress())
                    .recipient(keyPair.getAddress())
                    .amount(BigInteger.valueOf(recipientAccountSendAmount))
                    .nonce(recipientAccount.getNonce().add(ONE))
                    .build();
            _logger.info("Sending back {}", spendTx);
            txResponse =
                aeternityService.transactions.blockingPostTransaction(
                    spendTx, recipient.getEncodedPrivateKey());
            _logger.info("SpendTx hash: " + txResponse.getTxHash());
            waitForTxMined(txResponse.getTxHash());
            recipientAccount =
                this.aeternityService.accounts.blockingGetAccount(recipient.getAddress());
            context.assertEquals(
                recipientAccount.getBalance().longValue(),
                recipientAccountBalance
                    - recipientAccountSendAmount
                    - spendTx.getFee().longValue());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void waitForConfirmationSucceedsTest(TestContext context)
      throws TransactionCreateException {
    this.executeTest(
        context,
        t -> {
          try {
            Async async = context.async();
            KeyPair recipient = keyPairService.generateKeyPair();
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(this.keyPair.getAddress())
                    .recipient(recipient.getAddress())
                    .amount(new BigInteger("1000000000000000000"))
                    .payload("wait for confirmation works =)")
                    .nonce(getNextKeypairNonce())
                    .build();
            Single<PostTransactionResult> postTransactionResultSingle =
                aeternityService.transactions.asyncPostTransaction(spendTx);
            postTransactionResultSingle.subscribe(
                postTransactionResult -> {
                  _logger.info("SpendTx hash: " + postTransactionResult.getTxHash());
                  Single<TransactionResult> transactionResultSingle =
                      aeternityService.transactions.asyncWaitForConfirmation(
                          postTransactionResult.getTxHash());
                  transactionResultSingle.subscribe(
                      transactionResult -> {
                        _logger.info(transactionResult.toString());
                        if (transactionResult.getRootErrorMessage() != null
                            || transactionResult.getBlockHeight().intValue() == -1) {
                          context.fail();
                        }
                        async.complete();
                      },
                      throwable -> {
                        context.fail(throwable);
                      });
                },
                throwable -> {
                  context.fail(throwable);
                });

          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void waitForConfirmationFailsTest(TestContext context) throws TransactionCreateException {
    this.executeTest(
        context,
        t -> {
          try {
            Async async = context.async();
            KeyPair recipient = keyPairService.generateKeyPair();
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(this.keyPair.getAddress())
                    .recipient(recipient.getAddress())
                    .amount(new BigInteger("1000000000000000000"))
                    .payload("wait for confirmation fails :-(")
                    .nonce(getNextKeypairNonce())
                    .build();
            String computedTxHash = aeternityService.transactions.computeTxHash(spendTx);
            _logger.info("Computed txHash: " + computedTxHash);
            Single<TransactionResult> transactionResultSingle =
                aeternityService.transactions.asyncWaitForConfirmation(computedTxHash);
            transactionResultSingle.subscribe(
                transactionResult -> {
                  _logger.info(transactionResult.toString());
                  if (transactionResult.getRootErrorMessage() != null
                      || transactionResult.getAeAPIErrorMessage() != null
                      || transactionResult.getBlockHeight().intValue() == -1) {
                    // we expect the tx to be not existent in the network as it got never published
                    _logger.info("root error: " + transactionResult.getRootErrorMessage());
                    _logger.info("api error: " + transactionResult.getAeAPIErrorMessage());
                    async.complete();
                  } else {
                    context.fail();
                  }
                },
                throwable -> {
                  context.fail(throwable);
                });
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void checkTxInPoolAndNextNonceContinuityTest(TestContext context)
      throws TransactionCreateException {
    this.executeTest(
        context,
        t -> {
          try {
            // we are using asyncPostTransaction to avoid "waitUntilTransactionIsIncludedInBlock"
            // which is active by default for blockingPostTransaction
            KeyPair recipient = keyPairService.generateKeyPair();
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(recipient.getAddress())
                    .recipient(this.keyPair.getAddress())
                    .amount(new BigInteger("1000000000000000000"))
                    .payload("cannot work until sender has a balance")
                    .nonce(BigInteger.ONE)
                    .build();
            PostTransactionResult txResponse =
                aeternityService
                    .transactions
                    .asyncPostTransaction(spendTx, recipient.getEncodedPrivateKey())
                    .blockingGet();
            String txHashOfSenderWithoutBalance = txResponse.getTxHash();
            _logger.info("SpendTx hash: " + txHashOfSenderWithoutBalance);
            CheckTxInPoolResult checkTxInPoolResult =
                aeternityService.transactions.blockingCheckTxInPool(txHashOfSenderWithoutBalance);
            context.assertEquals("account_not_found", checkTxInPoolResult.getStatus());

            spendTx =
                SpendTransactionModel.builder()
                    .sender(this.keyPair.getAddress())
                    .recipient(recipient.getAddress())
                    .amount(new BigInteger("500000000000000000"))
                    .payload("sending a gift with some delay due to gap in nonce")
                    .nonce(getNextKeypairNonce().add(ONE))
                    .build();
            txResponse = aeternityService.transactions.asyncPostTransaction(spendTx).blockingGet();
            String txHashOfSenderWithNonceGap = txResponse.getTxHash();
            checkTxInPoolResult =
                aeternityService.transactions.blockingCheckTxInPool(txHashOfSenderWithNonceGap);
            context.assertEquals("tx_nonce_too_high_for_account", checkTxInPoolResult.getStatus());

            spendTx =
                SpendTransactionModel.builder()
                    .sender(this.keyPair.getAddress())
                    .recipient(recipient.getAddress())
                    .amount(new BigInteger("550000000000000000"))
                    .payload("now we should be good to go with all transactions")
                    .nonce(getNextKeypairNonce(NextNonceStrategy.CONTINUITY))
                    .build();
            txResponse = aeternityService.transactions.blockingPostTransaction(spendTx);
            checkTxInPoolResult =
                aeternityService.transactions.blockingCheckTxInPool(txResponse.getTxHash());
            context.assertEquals("included", checkTxInPoolResult.getStatus());
            waitForTxMined(txHashOfSenderWithNonceGap);
            checkTxInPoolResult =
                aeternityService.transactions.blockingCheckTxInPool(txHashOfSenderWithNonceGap);
            context.assertEquals("included", checkTxInPoolResult.getStatus());
            waitForTxMined(txHashOfSenderWithoutBalance);
            checkTxInPoolResult =
                aeternityService.transactions.blockingCheckTxInPool(txHashOfSenderWithoutBalance);
            context.assertEquals("included", checkTxInPoolResult.getStatus());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }
}
