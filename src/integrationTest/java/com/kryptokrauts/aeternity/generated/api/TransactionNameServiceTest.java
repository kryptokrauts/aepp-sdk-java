package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.name.domain.NameIdResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameClaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NamePreclaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameRevokeTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameUpdateTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionNameServiceTest extends BaseTest {

  static Random random = new Random();

  static String invalidDomain = TestConstants.DOMAIN + random.nextInt();
  static String validDomain = invalidDomain + TestConstants.NAMESPACE;

  /**
   * create an unsigned native namepreclaim transaction
   *
   * @param context
   */
  @Test
  public void buildNativeNamePreclaimTransactionTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          String sender = keyPairService.generateBaseKeyPair().getPublicKey();
          BigInteger salt = CryptoUtils.generateNamespaceSalt();
          BigInteger ttl = BigInteger.valueOf(100);

          NamePreclaimTransactionModel preclaim =
              NamePreclaimTransactionModel.builder()
                  .accountId(sender)
                  .name(validDomain)
                  .salt(salt)
                  .nonce(getNextBaseKeypairNonce())
                  .ttl(ttl)
                  .build();

          String unsignedTxNative =
              this.aeternityServiceNative.transactions.blockingCreateUnsignedTransaction(preclaim);

          String unsignedTxDebug =
              this.aeternityServiceDebug.transactions.blockingCreateUnsignedTransaction(preclaim);

          context.assertEquals(unsignedTxDebug, unsignedTxNative);
        });
  }

  /**
   * @param context
   * @throws Throwable
   */
  @Test
  public void postNameClaimTxTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            _logger.info("--------------------- postNameClaimTxTest ---------------------");
            BigInteger salt = CryptoUtils.generateNamespaceSalt();

            NamePreclaimTransactionModel namePreclaimTx =
                NamePreclaimTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .name(validDomain)
                    .salt(salt)
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .build();

            PostTransactionResult result = this.postTx(namePreclaimTx);
            _logger.info("NamePreclaimTx hash: " + result.getTxHash());
            context.assertEquals(
                result.getTxHash(),
                this.aeternityServiceNative.transactions.computeTxHash(namePreclaimTx));

            NameClaimTransactionModel nameClaimTx =
                NameClaimTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .name(validDomain)
                    .nameSalt(salt)
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .build();

            _logger.info(
                this.aeternityServiceNative.transactions.blockingCreateUnsignedTransaction(
                    nameClaimTx));

            result = this.postTx(nameClaimTx);
            _logger.info(
                String.format(
                    "Using namespace %s and salt %s for committmentId %s",
                    validDomain, salt, EncodingUtils.generateCommitmentHash(validDomain, salt)));
            _logger.info("NameClaimTx hash: " + result.getTxHash());

            TransactionResult genericSignedTx =
                this.aeternityServiceNative.info.blockingGetTransactionByHash(result.getTxHash());
            context.assertTrue(genericSignedTx.getBlockHeight().intValue() > 0);
            // NameClaimTx typedTx = (NameClaimTx) genericSignedTx.gett
            // _logger.info("Successfully claimed aens " + typedTx.getName());
            _logger.info("--------------------- postNameClaimTxTest ---------------------");
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  /**
   * @param context
   * @throws Throwable
   */
  @Test
  public void postUpdateTxTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            _logger.info("--------------------- postUpdateTxTest ---------------------");
            BigInteger salt = CryptoUtils.generateNamespaceSalt();
            String domain = TestConstants.DOMAIN + random.nextInt() + TestConstants.NAMESPACE;

            /** create a new namespace to update later */
            NamePreclaimTransactionModel namePreclaimTx =
                NamePreclaimTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .name(domain)
                    .salt(salt)
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .build();

            PostTransactionResult namePreclaimResult = this.postTx(namePreclaimTx);
            _logger.info("NamePreclaimTx hash: " + namePreclaimResult.getTxHash());

            context.assertEquals(
                namePreclaimResult.getTxHash(),
                this.aeternityServiceNative.transactions.computeTxHash(namePreclaimTx));

            NameClaimTransactionModel nameClaimTx =
                NameClaimTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .name(domain)
                    .nameSalt(salt)
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .build();
            PostTransactionResult nameClaimResult = this.postTx(nameClaimTx);
            _logger.info(
                String.format(
                    "Using namespace %s and salt %s for committmentId %s",
                    domain, salt, EncodingUtils.generateCommitmentHash(domain, salt)));
            _logger.info("NameClaimTx hash: " + nameClaimResult.getTxHash());

            NameIdResult nameIdResult = this.aeternityServiceNative.names.blockingGetNameId(domain);
            BigInteger initialTTL = nameIdResult.getTtl();

            _logger.info(
                String.format(
                    "Created namespace %s with salt %s and nameEntry %s in tx %s for update test",
                    domain, salt, nameIdResult, nameClaimResult.getTxHash()));
            /** finished creating namespace */
            BigInteger nameTtl = BigInteger.valueOf(10000l);
            BigInteger clientTtl = BigInteger.valueOf(50l);

            String accountPointer = baseKeyPair.getPublicKey();
            // fake contract-address
            String contractPointer = baseKeyPair.getPublicKey().replace("ak_", "ct_");

            NameUpdateTransactionModel nameUpdateTx =
                NameUpdateTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .nameId(nameIdResult.getId())
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .clientTtl(clientTtl)
                    .nameTtl(nameTtl)
                    .pointerAddresses(Arrays.asList(accountPointer, contractPointer))
                    .build();

            PostTransactionResult nameUpdateResult = this.postTx(nameUpdateTx);
            context.assertEquals(
                nameUpdateResult.getTxHash(),
                this.aeternityServiceNative.transactions.computeTxHash(nameUpdateTx));

            nameIdResult = this.aeternityServiceNative.names.blockingGetNameId(domain);
            _logger.info(
                String.format(
                    "Updated namespace %s with salt %s and nameEntry %s in tx %s for update test",
                    domain, salt, nameIdResult, nameUpdateResult.getTxHash()));

            BigInteger updatedTTL = nameIdResult.getTtl();
            // subtract 40000 because initial default ttl is 50000 and updated ttl was 10000
            int diffTtl = initialTTL.subtract(updatedTTL).intValue();
            context.assertTrue(diffTtl <= 40000);
            if (diffTtl < 40000) {
              _logger.info(
                  String.format(
                      "Diff of Ttl is %s, this happens when meanwhile new blocks are mined",
                      diffTtl));
            }
            _logger.info("--------------------- postUpdateTxTest ---------------------");
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  /**
   * @param context
   * @throws Throwable
   */
  @Test
  public void postRevokeTxTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            _logger.info("--------------------- postRevokeTxTest ---------------------");
            String nameId =
                this.aeternityServiceNative.names.blockingGetNameId(validDomain).getId();

            NameRevokeTransactionModel nameRevokeTx =
                NameRevokeTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .nameId(nameId)
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .build();

            PostTransactionResult nameRevokeResult = this.postTx(nameRevokeTx);
            _logger.info("NameRevokeTx hash: " + nameRevokeResult.getTxHash());

            context.assertEquals(
                nameRevokeResult.getTxHash(),
                this.aeternityServiceNative.transactions.computeTxHash(nameRevokeTx));

            NameIdResult result = this.aeternityServiceNative.names.blockingGetNameId(validDomain);
            context.assertTrue(
                "{\"reason\":\"Name revoked\"}".contentEquals(result.getRootErrorMessage()));

            _logger.info(String.format("Validated, that namespace %s is revoked", validDomain));

            _logger.info("--------------------- postRevokeTxTest ---------------------");
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }
}
