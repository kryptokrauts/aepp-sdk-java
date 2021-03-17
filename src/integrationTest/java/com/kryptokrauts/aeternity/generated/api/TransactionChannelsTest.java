package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import io.vertx.ext.unit.TestContext;
import java.math.BigInteger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionChannelsTest extends BaseTest {

  BaseKeyPair initiator;
  BaseKeyPair responder;

  @Before
  public void initBeforeTest() {
    initiator = keyPairService.generateBaseKeyPairFromSecret(TestConstants.BENEFICIARY_PRIVATE_KEY);
    responder = keyPairService.generateBaseKeyPair();
  }

  @Test
  public void aFundResponderAccount(TestContext context) throws TransactionCreateException {
    this.executeTest(
        context,
        t -> {
          try {
            BigInteger amount = UnitConversionUtil.toAettos("10", Unit.AE).toBigInteger();
            SpendTransactionModel spendTx =
                SpendTransactionModel.builder()
                    .sender(initiator.getPublicKey())
                    .recipient(responder.getPublicKey())
                    .amount(amount)
                    .payload("")
                    .ttl(ZERO)
                    .nonce(getNextBaseKeypairNonce())
                    .build();
            PostTransactionResult txResponse = this.blockingPostTx(spendTx);
            context.assertNotNull(txResponse);
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void channelCreateTest(TestContext context) throws TransactionCreateException {
    this.executeTest(
        context,
        t -> {
          BigInteger amount = UnitConversionUtil.toAettos("2", Unit.AE).toBigInteger();

          ChannelCreateTransactionModel model =
              ChannelCreateTransactionModel.builder()
                  .initiator(initiator.getPublicKey())
                  .initiatorAmount(amount)
                  .responder(responder.getPublicKey())
                  .responderAmount(amount)
                  .channelReserve(ZERO)
                  .lockPeriod(ZERO)
                  .ttl(ZERO)
                  .stateHash("")
                  .nonce(getNextBaseKeypairNonce())
                  .build();
          String txResponse =
              aeternityServiceNative
                  .transactions
                  .blockingCreateUnsignedTransaction(model)
                  .getResult();
          context.assertNotNull(txResponse);
          _logger.info("Channel create tx hash: " + txResponse);
        });
  }
}
