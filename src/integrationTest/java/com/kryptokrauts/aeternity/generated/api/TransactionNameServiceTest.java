package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.constants.AENS;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveNameAuctionsCountResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveNameResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.name.domain.NameIdResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameClaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NamePreclaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameRevokeTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameUpdateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
              this.aeternityServiceNative
                  .transactions
                  .blockingCreateUnsignedTransaction(preclaim)
                  .getResult();

          String unsignedTxDebug =
              this.aeternityServiceDebug
                  .transactions
                  .blockingCreateUnsignedTransaction(preclaim)
                  .getResult();

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

            PostTransactionResult result = this.blockingPostTx(namePreclaimTx, Optional.empty());
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
                this.aeternityServiceNative
                    .transactions
                    .blockingCreateUnsignedTransaction(nameClaimTx)
                    .getResult());

            result = this.blockingPostTx(nameClaimTx, Optional.empty());
            _logger.info(
                String.format(
                    "Using namespace %s and salt %s for committmentId %s",
                    validDomain, salt, EncodingUtils.generateCommitmentHash(validDomain, salt)));
            _logger.info("NameClaimTx hash: " + result.getTxHash());

            TransactionResult genericSignedTx =
                this.aeternityServiceNative.info.blockingGetTransactionByHash(result.getTxHash());
            context.assertTrue(genericSignedTx.getBlockHeight().intValue() > 0);
            // NameClaimTx typedTx = (NameClaimTx) genericSignedTx.gett
            // _logger.info("Successfully claimed aens " +
            // typedTx.getName());
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
  public void postUpdateAndSpendTxTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            _logger.info("--------------------- postUpdateAndSpendTxTest ---------------------");
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

            PostTransactionResult namePreclaimResult =
                this.blockingPostTx(namePreclaimTx, Optional.empty());
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
            PostTransactionResult nameClaimResult =
                this.blockingPostTx(nameClaimTx, Optional.empty());
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

            BaseKeyPair recipient = keyPairService.generateBaseKeyPair();
            String accountPointer = recipient.getPublicKey();
            // fake other allowed pointers
            String contractPointer = baseKeyPair.getContractPK();
            String channelPointer = baseKeyPair.getPublicKey().replace("ak_", "ch_");
            String oraclePointer = baseKeyPair.getOraclePK();

            NameUpdateTransactionModel nameUpdateTx =
                NameUpdateTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .nameId(nameIdResult.getId())
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .clientTtl(clientTtl)
                    .nameTtl(nameTtl)
                    .pointers(
                        Arrays.asList(
                            accountPointer, contractPointer, channelPointer, oraclePointer))
                    .build();

            PostTransactionResult nameUpdateResult =
                this.blockingPostTx(nameUpdateTx, Optional.empty());

            context.assertEquals(
                nameUpdateResult.getTxHash(),
                this.aeternityServiceNative.transactions.computeTxHash(nameUpdateTx));

            nameIdResult = this.aeternityServiceNative.names.blockingGetNameId(domain);
            _logger.info(
                String.format(
                    "Updated namespace %s with salt %s and nameEntry %s in tx %s for update test",
                    domain, salt, nameIdResult, nameUpdateResult.getTxHash()));

            context.assertEquals(accountPointer, nameIdResult.getAccountPointer().get());
            context.assertEquals(channelPointer, nameIdResult.getChannelPointer().get());
            context.assertEquals(contractPointer, nameIdResult.getContractPointer().get());
            context.assertEquals(oraclePointer, nameIdResult.getOraclePointer().get());
            BigInteger updatedTTL = nameIdResult.getTtl();
            // subtract 40000 because initial default ttl is 50000 and
            // updated ttl was 10000
            int diffTtl = initialTTL.subtract(updatedTTL).intValue();
            context.assertTrue(diffTtl <= 40000);
            if (diffTtl < 40000) {
              _logger.info(
                  String.format(
                      "Diff of Ttl is %s, this happens when meanwhile new blocks are mined",
                      diffTtl));
            }
            BigInteger aettos = new BigInteger("1000000000000000000");
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(this.baseKeyPair.getPublicKey())
                    .recipient(nameIdResult.getId())
                    .amount(aettos)
                    .payload("send to AENS name test")
                    .ttl(ZERO)
                    .nonce(getNextBaseKeypairNonce())
                    .build();
            PostTransactionResult txResponse =
                aeternityServiceNative.transactions.blockingPostTransaction(spendTx);
            _logger.info("SpendTx hash: " + txResponse.getTxHash());
            waitForTxMined(txResponse.getTxHash());
            AccountResult recipientAccount =
                this.aeternityServiceNative.accounts.blockingGetAccount(
                    Optional.of(accountPointer));
            _logger.info("Account result for recipient {}", recipientAccount);
            context.assertEquals(aettos, recipientAccount.getBalance());
            _logger.info("--------------------- postUpdateAndSpendTxTest ---------------------");
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

            PostTransactionResult nameRevokeResult =
                this.blockingPostTx(nameRevokeTx, Optional.empty());
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

  /**
   * @param context
   * @throws Throwable
   */
  @Test
  public void zAuctionTest(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            _logger.info("--------------------- auctionTest ---------------------");
            BigInteger salt = CryptoUtils.generateNamespaceSalt();
            String domain = "auction" + (random.nextInt(90000) + 10000) + TestConstants.NAMESPACE;
            _logger.info("domain has {} chars", domain.split("\\.")[0].length());

            ActiveNameAuctionsCountResult oldActiveAuctions =
                this.aeternityServiceNative.aeternal.blockingGetActiveNameAuctionsCount();
            _logger.info("active auctions: {}", oldActiveAuctions.getCount());

            /** create a new namespace to update later */
            NamePreclaimTransactionModel namePreclaimTx =
                NamePreclaimTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .name(domain)
                    .salt(salt)
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .build();
            PostTransactionResult namePreclaimResult =
                this.blockingPostTx(namePreclaimTx, Optional.empty());
            _logger.info("NamePreclaimTx hash: {}", namePreclaimResult.getTxHash());
            context.assertEquals(
                namePreclaimResult.getTxHash(),
                this.aeternityServiceNative.transactions.computeTxHash(namePreclaimTx));
            // currently we do not have an active auction
            context.assertFalse(
                this.aeternityServiceNative.aeternal.blockingIsNameAuctionActive(domain));
            NameClaimTransactionModel nameClaimTx =
                NameClaimTransactionModel.builder()
                    .accountId(baseKeyPair.getPublicKey())
                    .name(domain)
                    .nameSalt(salt)
                    .nonce(getNextBaseKeypairNonce())
                    .ttl(ZERO)
                    .build();
            BigInteger currentNameFee = nameClaimTx.getNameFee();
            _logger.info("current nameFee: {} ættos", currentNameFee);
            _logger.info(
                "current nameFee: {} Æ",
                UnitConversionUtil.fromAettos(
                    currentNameFee.toString(), UnitConversionUtil.Unit.AE));
            PostTransactionResult nameClaimResult =
                this.blockingPostTx(nameClaimTx, Optional.empty());
            _logger.info(
                String.format(
                    "Using namespace %s and salt %s for committmentId %s",
                    domain, salt, EncodingUtils.generateCommitmentHash(domain, salt)));
            _logger.info("NameClaimTx hash: {}", nameClaimResult.getTxHash());
            /** now we have an active auction and we wait for it to be present at aeternal */
            while (!this.aeternityServiceNative.aeternal.blockingIsNameAuctionActive(domain)) {
              _logger.info("waiting for auction of domain {}", domain);
              Thread.sleep(1000);
            }
            _logger.info("found auction for domain {}", domain);
            /** name cannot be found due to running auction */
            NameIdResult nameIdResult = this.aeternityServiceNative.names.blockingGetNameId(domain);
            context.assertTrue(
                nameIdResult.getRootErrorMessage() != null
                    && nameIdResult.getRootErrorMessage().contains("Name not found"));
            _logger.info(
                "Created namespace {} with salt {} and nameEntry {} in tx {} for update test",
                domain,
                salt,
                nameIdResult,
                nameClaimResult.getTxHash());

            BigInteger nextNameFee = AENS.getNextNameFee(currentNameFee);
            _logger.info("next nameFee: {} ættos", nextNameFee);
            _logger.info(
                "next nameFee: {} Æ",
                UnitConversionUtil.fromAettos(nextNameFee.toString(), UnitConversionUtil.Unit.AE));

            /** create and fund other account to claim the same name with nextNameFee */
            AccountResult account =
                this.aeternityServiceNative.accounts.blockingGetAccount(Optional.empty());
            BaseKeyPair kpNextClaimer = keyPairService.generateBaseKeyPair();
            String recipient = kpNextClaimer.getPublicKey();
            BigInteger amount =
                UnitConversionUtil.toAettos("50", UnitConversionUtil.Unit.AE).toBigInteger();
            BigInteger nonce = account.getNonce().add(ONE);
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(account.getPublicKey())
                    .recipient(recipient)
                    .amount(amount)
                    .ttl(ZERO)
                    .nonce(nonce)
                    .build();
            PostTransactionResult txResponse = this.blockingPostTx(spendTx, Optional.empty());
            _logger.info("SpendTx hash: " + txResponse.getTxHash());
            context.assertEquals(
                txResponse.getTxHash(), aeternityServiceNative.transactions.computeTxHash(spendTx));

            /** get funded account and create next nameClaimTx */
            AccountResult otherAccount =
                this.aeternityServiceNative.accounts.blockingGetAccount(Optional.of(recipient));
            NameClaimTransactionModel nextNameClaimTx =
                nameClaimTx
                    .toBuilder()
                    .accountId(recipient)
                    .nonce(otherAccount.getNonce().add(BigInteger.ONE))
                    .nameFee(nextNameFee)
                    .nameSalt(BigInteger.ZERO)
                    .build();
            PostTransactionResult result =
                this.blockingPostTx(nextNameClaimTx, Optional.of(kpNextClaimer.getPrivateKey()));
            TransactionResult transactionResult = waitForTxMined(result.getTxHash());
            _logger.info("next claimTx result: {}", transactionResult);
            BigInteger finalBlockHeight =
                transactionResult.getBlockHeight().add(AENS.getBlockTimeout(domain));
            _logger.info("claim will be final at block {}", finalBlockHeight);
            ActiveNameAuctionsCountResult newActiveAuctions =
                this.aeternityServiceNative.aeternal.blockingGetActiveNameAuctionsCount();
            _logger.info("active auctions: {}", newActiveAuctions.getCount());
            context.assertEquals(
                oldActiveAuctions.getCount().add(BigInteger.ONE), newActiveAuctions.getCount());
            waitForBlockHeight(finalBlockHeight, 5000l);
            nameIdResult = this.aeternityServiceNative.names.blockingGetNameId(domain);
            context.assertTrue(nameIdResult.getRootErrorMessage() == null);
            _logger.info("NameIdResult: {}", nameIdResult);
            List<ActiveNameResult> activeNameResults = Collections.emptyList();
            while (activeNameResults.isEmpty()) {
              activeNameResults =
                  this.aeternityServiceNative
                      .aeternal
                      .blockingSearchName(domain)
                      .getActiveNameResults();
              _logger.info("waiting for ActiveNameResult: {}", domain);
              Thread.sleep(1000);
            }
            ActiveNameResult activeNameResult = activeNameResults.stream().findFirst().get();
            _logger.info("ActiveNameResult: {}", activeNameResult);
            // kpNextClaimer should be the owner
            context.assertEquals(kpNextClaimer.getPublicKey(), activeNameResult.getOwner());
            context.assertEquals(nameIdResult.getId(), activeNameResult.getNameHash());
            _logger.info("--------------------- auctionTest ---------------------");
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }
}
