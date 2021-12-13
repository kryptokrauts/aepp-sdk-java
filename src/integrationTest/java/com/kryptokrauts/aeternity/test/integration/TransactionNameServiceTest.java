package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.constants.AENS;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.info.domain.TransactionResult;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.NameAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.name.domain.NameEntryResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameClaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NamePreclaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameRevokeTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameUpdateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionNameServiceTest extends BaseTest {

  static Random random = new Random();

  static String invalidName = TestConstants.NAME + random.nextInt();
  static String validName = invalidName + TestConstants.NAMESPACE;

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
                    .accountId(keyPair.getAddress())
                    .name(validName)
                    .salt(salt)
                    .nonce(getNextKeypairNonce())
                    .build();

            PostTransactionResult result = this.blockingPostTx(namePreclaimTx);
            _logger.info("NamePreclaimTx hash: " + result.getTxHash());
            context.assertEquals(
                result.getTxHash(),
                this.aeternityService.transactions.computeTxHash(namePreclaimTx));

            NameClaimTransactionModel nameClaimTx =
                NameClaimTransactionModel.builder()
                    .accountId(keyPair.getAddress())
                    .name(validName)
                    .nameSalt(salt)
                    .nonce(getNextKeypairNonce())
                    .build();

            _logger.info(
                this.aeternityService
                    .transactions
                    .blockingCreateUnsignedTransaction(nameClaimTx)
                    .getResult());

            result = this.blockingPostTx(nameClaimTx);
            _logger.info(
                String.format(
                    "Using namespace %s and salt %s for committmentId %s",
                    validName, salt, EncodingUtils.generateCommitmentHash(validName, salt)));
            _logger.info("NameClaimTx hash: " + result.getTxHash());

            TransactionResult genericSignedTx =
                this.aeternityService.info.blockingGetTransactionByHash(result.getTxHash());
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
            String name = TestConstants.NAME + random.nextInt() + TestConstants.NAMESPACE;

            /** create a new namespace to update later */
            NamePreclaimTransactionModel namePreclaimTx =
                NamePreclaimTransactionModel.builder()
                    .accountId(keyPair.getAddress())
                    .name(name)
                    .salt(salt)
                    .nonce(getNextKeypairNonce())
                    .build();

            PostTransactionResult namePreclaimResult = this.blockingPostTx(namePreclaimTx);
            _logger.info("NamePreclaimTx hash: " + namePreclaimResult.getTxHash());

            context.assertEquals(
                namePreclaimResult.getTxHash(),
                this.aeternityService.transactions.computeTxHash(namePreclaimTx));

            NameClaimTransactionModel nameClaimTx =
                NameClaimTransactionModel.builder()
                    .accountId(keyPair.getAddress())
                    .name(name)
                    .nameSalt(salt)
                    .nonce(getNextKeypairNonce())
                    .build();
            PostTransactionResult nameClaimResult = this.blockingPostTx(nameClaimTx);
            _logger.info(
                String.format(
                    "Using namespace %s and salt %s for committmentId %s",
                    name, salt, EncodingUtils.generateCommitmentHash(name, salt)));
            _logger.info("NameClaimTx hash: " + nameClaimResult.getTxHash());

            NameEntryResult nameEntryResult = this.aeternityService.names.blockingGetNameId(name);
            BigInteger initialTTL = nameEntryResult.getTtl();

            _logger.info(
                String.format(
                    "Created namespace %s with salt %s and nameEntry %s in tx %s for update test",
                    name, salt, nameEntryResult, nameClaimResult.getTxHash()));
            /** finished creating namespace */
            BigInteger nameTtl = BigInteger.valueOf(10000l);

            KeyPair recipient = keyPairService.generateKeyPair();
            String accountPointer = recipient.getAddress();
            // fake other allowed pointers
            String contractPointer = keyPair.getContractAddress();
            String channelPointer = keyPair.getAddress().replace("ak_", "ch_");
            String oraclePointer = keyPair.getOracleAddress();

            KeyPair anotherKeyPair = keyPairService.generateKeyPair();

            NameUpdateTransactionModel nameUpdateTx =
                NameUpdateTransactionModel.builder()
                    .accountId(keyPair.getAddress())
                    .nameId(AENS.getNameId(name))
                    .nonce(getNextKeypairNonce())
                    .nameTtl(nameTtl)
                    .pointers(
                        new HashMap<String, String>() {
                          {
                            put(AENS.POINTER_KEY_ACCOUNT, accountPointer);
                            put(AENS.POINTER_KEY_CHANNEL, channelPointer);
                            put(AENS.POINTER_KEY_CONTRACT, contractPointer);
                            put(AENS.POINTER_KEY_ORACLE, oraclePointer);
                            put("arbitrary-account-pointer-key", anotherKeyPair.getAddress());
                            put(
                                "arbitrary-channel-pointer-key",
                                // workaround to set a valid channel id
                                anotherKeyPair.getAddress().replace("ak_", "ch_"));
                            put(
                                "arbitrary-contract-pointer-key",
                                anotherKeyPair.getContractAddress());
                            put("arbitrary-oracle-pointer-key", anotherKeyPair.getOracleAddress());
                          }
                        })
                    .build();

            PostTransactionResult nameUpdateResult = this.blockingPostTx(nameUpdateTx);

            context.assertEquals(
                nameUpdateResult.getTxHash(),
                this.aeternityService.transactions.computeTxHash(nameUpdateTx));

            nameEntryResult = this.aeternityService.names.blockingGetNameId(name);
            _logger.info(
                String.format(
                    "Updated namespace %s with salt %s and nameEntry %s in tx %s for update test",
                    name, salt, nameEntryResult, nameUpdateResult.getTxHash()));

            context.assertEquals(accountPointer, nameEntryResult.getAccountPointer().get());
            context.assertEquals(channelPointer, nameEntryResult.getChannelPointer().get());
            context.assertEquals(contractPointer, nameEntryResult.getContractPointer().get());
            context.assertEquals(oraclePointer, nameEntryResult.getOraclePointer().get());
            context.assertEquals(
                anotherKeyPair.getAddress(),
                nameEntryResult.getPointers().get("arbitrary-account-pointer-key"));
            context.assertTrue(nameEntryResult.getPointers().size() == 8);
            BigInteger updatedTTL = nameEntryResult.getTtl();
            // subtract 170000 because initial default ttl is 180000 and
            // updated ttl was 10000
            int diffTtl = initialTTL.subtract(updatedTTL).intValue();
            context.assertTrue(diffTtl <= 170000);
            if (diffTtl < 170000) {
              _logger.info(
                  String.format(
                      "Diff of Ttl is %s, this happens when meanwhile new blocks are mined",
                      diffTtl));
            }
            BigInteger aettos = new BigInteger("1000000000000000000");
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(this.keyPair.getAddress())
                    .recipient(nameEntryResult.getId())
                    .amount(aettos)
                    .payload("send to AENS name test")
                    .nonce(getNextKeypairNonce())
                    .build();
            PostTransactionResult txResponse =
                aeternityService.transactions.blockingPostTransaction(spendTx);
            _logger.info("SpendTx hash: " + txResponse.getTxHash());
            waitForTxMined(txResponse.getTxHash());
            AccountResult recipientAccount =
                this.aeternityService.accounts.blockingGetAccount(accountPointer);
            _logger.info("Account result for recipient {}", recipientAccount);
            context.assertEquals(aettos, recipientAccount.getBalance());
            _logger.info("--------------------- postUpdateAndSpendTxTest ---------------------");
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void postUpdateFailsByExceedingPointersLimit(TestContext context) {
    this.executeTest(
        context,
        t -> {
          try {
            BigInteger salt = CryptoUtils.generateNamespaceSalt();
            String name = TestConstants.NAME + random.nextInt() + TestConstants.NAMESPACE;
            NamePreclaimTransactionModel namePreclaimTx =
                NamePreclaimTransactionModel.builder()
                    .accountId(keyPair.getAddress())
                    .name(name)
                    .salt(salt)
                    .nonce(getNextKeypairNonce())
                    .build();

            PostTransactionResult namePreclaimResult = this.blockingPostTx(namePreclaimTx);
            _logger.info("NamePreclaimTx hash: " + namePreclaimResult.getTxHash());

            context.assertEquals(
                namePreclaimResult.getTxHash(),
                this.aeternityService.transactions.computeTxHash(namePreclaimTx));

            NameClaimTransactionModel nameClaimTx =
                NameClaimTransactionModel.builder()
                    .accountId(keyPair.getAddress())
                    .name(name)
                    .nameSalt(salt)
                    .nonce(getNextKeypairNonce())
                    .build();
            PostTransactionResult nameClaimResult = this.blockingPostTx(nameClaimTx);
            _logger.info("NameClaimTx hash: " + nameClaimResult.getTxHash());
            Map<String, String> pointers = new HashMap<>();
            // exceed pointer limit
            for (int i = 0; i < 33; i++) {
              pointers.put("name" + i, keyPair.getAddress());
            }
            NameUpdateTransactionModel nameUpdateTx =
                NameUpdateTransactionModel.builder()
                    .accountId(keyPair.getAddress())
                    .nameId(AENS.getNameId(name))
                    .nonce(getNextKeypairNonce())
                    .nameTtl(AENS.MAX_TTL)
                    .pointers(pointers)
                    .build();
            try {
              this.blockingPostTx(nameUpdateTx);
              context.fail("expected InvalidParameterException");
            } catch (InvalidParameterException invalidParameterException) {
              context.assertTrue(
                  invalidParameterException
                      .getMessage()
                      .contains(ValidationUtil.POINTER_LIMIT_EXCEEDED));
            }
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
            String nameId = this.aeternityService.names.blockingGetNameId(validName).getId();

            NameRevokeTransactionModel nameRevokeTx =
                NameRevokeTransactionModel.builder()
                    .accountId(keyPair.getAddress())
                    .nameId(nameId)
                    .nonce(getNextKeypairNonce())
                    .build();

            PostTransactionResult nameRevokeResult = this.blockingPostTx(nameRevokeTx);
            _logger.info("NameRevokeTx hash: " + nameRevokeResult.getTxHash());

            context.assertEquals(
                nameRevokeResult.getTxHash(),
                this.aeternityService.transactions.computeTxHash(nameRevokeTx));

            NameEntryResult result = this.aeternityService.names.blockingGetNameId(validName);
            context.assertTrue(
                "{\"reason\":\"Name revoked\"}".contentEquals(result.getRootErrorMessage()));

            _logger.info(String.format("Validated, that namespace %s is revoked", validName));

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
            String name = "auction" + (random.nextInt(90000) + 10000) + TestConstants.NAMESPACE;
            _logger.info("name has {} chars", name.split("\\.")[0].length());

            NameAuctionsResult oldNameAuctionsResult =
                this.aeternityService.mdw.blockingGetNameAuctions();
            _logger.info("active auctions: {}", oldNameAuctionsResult);
            _logger.info(
                "active auctions count: {}", oldNameAuctionsResult.getNameAuctions().size());

            /** create a new namespace to update later */
            NamePreclaimTransactionModel namePreclaimTx =
                NamePreclaimTransactionModel.builder()
                    .accountId(this.keyPair.getAddress())
                    .name(name)
                    .salt(salt)
                    .nonce(getNextKeypairNonce())
                    .build();
            PostTransactionResult namePreclaimResult = this.blockingPostTx(namePreclaimTx);
            _logger.info("NamePreclaimTx hash: {}", namePreclaimResult.getTxHash());
            context.assertEquals(
                namePreclaimResult.getTxHash(),
                this.aeternityService.transactions.computeTxHash(namePreclaimTx));

            // currently we do not have an active auction
            // we expect an error (not found)
            context.assertFalse(
                this.aeternityService
                    .mdw
                    .blockingGetNameAuction(name)
                    .getAeAPIErrorMessage()
                    .isEmpty());

            NameClaimTransactionModel nameClaimTx =
                NameClaimTransactionModel.builder()
                    .accountId(this.keyPair.getAddress())
                    .name(name)
                    .nameSalt(salt)
                    .nonce(getNextKeypairNonce())
                    .build();
            BigInteger currentNameFee = nameClaimTx.getNameFee();
            _logger.info("current nameFee: {} ættos", currentNameFee);
            _logger.info(
                "current nameFee: {} Æ",
                UnitConversionUtil.fromAettos(
                    currentNameFee.toString(), UnitConversionUtil.Unit.AE));
            PostTransactionResult nameClaimResult = this.blockingPostTx(nameClaimTx);
            _logger.info(
                String.format(
                    "Using namespace %s and salt %s for committmentId %s",
                    name, salt, EncodingUtils.generateCommitmentHash(name, salt)));
            _logger.info("NameClaimTx hash: {}", nameClaimResult.getTxHash());

            while (this.aeternityService.mdw.blockingGetNameAuction(name).getAeAPIErrorMessage()
                != null) {
              _logger.info("waiting for auction of name {}", name);
              Thread.sleep(1000);
            }

            NameAuctionsResult newNameAuctionsResult =
                this.aeternityService.mdw.blockingGetNameAuctions();
            _logger.info("active auctions: {}", newNameAuctionsResult.getNameAuctions().size());
            context.assertEquals(
                oldNameAuctionsResult.getNameAuctions().size() + 1,
                newNameAuctionsResult.getNameAuctions().size());

            _logger.info("found auction for name {}", name);
            /** name cannot be found due to running auction */
            NameEntryResult nameEntryResult = this.aeternityService.names.blockingGetNameId(name);
            context.assertTrue(
                nameEntryResult.getRootErrorMessage() != null
                    && nameEntryResult.getRootErrorMessage().contains("Name not found"));
            _logger.info(
                "Created namespace {} with salt {} and nameEntry {} in tx {} for update test",
                name,
                salt,
                nameEntryResult,
                nameClaimResult.getTxHash());

            BigInteger nextNameFee = AENS.getNextNameFee(currentNameFee);
            _logger.info("next nameFee: {} ættos", nextNameFee);
            _logger.info(
                "next nameFee: {} Æ",
                UnitConversionUtil.fromAettos(nextNameFee.toString(), UnitConversionUtil.Unit.AE));

            /** create and fund other account to claim the same name with nextNameFee */
            AccountResult account = this.aeternityService.accounts.blockingGetAccount();
            KeyPair kpNextClaimer = keyPairService.generateKeyPair();
            String recipient = kpNextClaimer.getAddress();
            BigInteger amount =
                UnitConversionUtil.toAettos("50", UnitConversionUtil.Unit.AE).toBigInteger();
            BigInteger nonce = account.getNonce().add(ONE);
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(account.getPublicKey())
                    .recipient(recipient)
                    .amount(amount)
                    .nonce(nonce)
                    .build();
            PostTransactionResult txResponse = this.blockingPostTx(spendTx);
            _logger.info("SpendTx hash: " + txResponse.getTxHash());
            context.assertEquals(
                txResponse.getTxHash(), aeternityService.transactions.computeTxHash(spendTx));

            /** get funded account and create next nameClaimTx */
            AccountResult otherAccount =
                this.aeternityService.accounts.blockingGetAccount(recipient);
            NameClaimTransactionModel nextNameClaimTx =
                nameClaimTx
                    .toBuilder()
                    .accountId(recipient)
                    .nonce(otherAccount.getNonce().add(BigInteger.ONE))
                    .nameFee(nextNameFee)
                    .nameSalt(BigInteger.ZERO)
                    .build();
            PostTransactionResult result =
                this.blockingPostTx(nextNameClaimTx, kpNextClaimer.getEncodedPrivateKey());
            TransactionResult transactionResult = waitForTxMined(result.getTxHash());
            _logger.info("next claimTx result: {}", transactionResult);
            BigInteger finalBlockHeight =
                transactionResult.getBlockHeight().add(AENS.getBlockTimeout(name));
            _logger.info("claim will be final at block {}", finalBlockHeight);

            waitForBlockHeight(finalBlockHeight, 5000l);
            nameEntryResult = this.aeternityService.names.blockingGetNameId(name);
            context.assertTrue(nameEntryResult.getRootErrorMessage() == null);
            _logger.info("NameEntryResult: {}", nameEntryResult);
            context.assertEquals(kpNextClaimer.getAddress(), nameEntryResult.getOwner());
            _logger.info("--------------------- auctionTest ---------------------");
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }
}
