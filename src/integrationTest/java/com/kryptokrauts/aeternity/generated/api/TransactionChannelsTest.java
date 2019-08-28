package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.TransactionCreateException;
import com.kryptokrauts.aeternity.sdk.service.domain.transaction.PostTransactionResult;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import io.reactivex.Single;
import io.vertx.ext.unit.Async;
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
    Async async = context.async();

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

    Single<PostTransactionResult> txResponse =
        aeternityServiceNative.transactions.asyncPostTransaction(spendTx);

    txResponse.subscribe(
        resultObject -> {
          context.assertNotNull(resultObject);
          async.complete();
        });
    async.awaitSuccess(TEST_CASE_TIMEOUT_MILLIS);
  }

  @Test
  public void channelCreateTest(TestContext context) throws TransactionCreateException {
    Async async = context.async();

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

    Single<String> txResponse =
        aeternityServiceNative.transactions.asyncCreateUnsignedTransaction(model);

    txResponse.subscribe(
        resultObject -> {
          context.assertNotNull(resultObject);
          _logger.info("Channel create tx hash: " + resultObject);
          async.complete();
        });
    async.awaitSuccess(TEST_CASE_TIMEOUT_MILLIS);
  }
}
