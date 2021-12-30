package com.kryptokrauts.aeternity.test.integration;

import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractTxResult;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil;
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import io.vertx.ext.unit.TestContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentSplitterContractTest extends BaseTest {

  static String contractId;

  static KeyPair initialReceiver1;
  static KeyPair initialReceiver2;
  static KeyPair initialReceiver3;

  static Map<String, Integer> initialWeights = new HashMap<>();

  @Test
  public void a_a_init(TestContext context) {
    this.executeTest(
        context,
        t -> {
          initialReceiver1 = new KeyPairServiceFactory().getService().generateKeyPair();
          initialReceiver2 = new KeyPairServiceFactory().getService().generateKeyPair();
          initialReceiver3 = new KeyPairServiceFactory().getService().generateKeyPair();
          _logger.info("Initial receiver 1: " + initialReceiver1.getAddress());
          _logger.info("Initial receiver 2: " + initialReceiver2.getAddress());
          _logger.info("Initial receiver 3: " + initialReceiver3.getAddress());

          initialWeights.put(initialReceiver1.getAddress(), 40);
          initialWeights.put(initialReceiver2.getAddress(), 40);
          initialWeights.put(initialReceiver3.getAddress(), 20);
          context.assertEquals(3, initialWeights.size());
        });
  }

  @Test
  public void a_deployPaymentSplitterTest(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            ContractTxResult contractTxResult =
                aeternityService.transactions.blockingContractCreate(
                    List.of(initialWeights), null, paymentSplitterSource, null);
            contractId = contractTxResult.getCallResult().getContractId();
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void b_callPayAndSplitMethodTest(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            BigInteger balanceRecipient1;
            BigInteger balanceRecipient2;
            BigInteger balanceRecipient3;
            // if one of the accounts wasn't active we get an error and know
            // that the
            // accounts don't have any balance
            balanceRecipient1 =
                Optional.ofNullable(getAccount(initialReceiver1.getAddress()).getBalance())
                    .orElse(ZERO);
            balanceRecipient2 =
                Optional.ofNullable(getAccount(initialReceiver2.getAddress()).getBalance())
                    .orElse(ZERO);
            balanceRecipient3 =
                Optional.ofNullable(getAccount(initialReceiver3.getAddress()).getBalance())
                    .orElse(ZERO);

            BigDecimal paymentValue = UnitConversionUtil.toAettos("1", Unit.AE);

            ContractTxResult contractTxResult =
                aeternityService.transactions.blockingStatefulContractCall(
                    contractId,
                    "payAndSplit",
                    null,
                    paymentValue.toBigInteger(),
                    paymentSplitterSource,
                    null);

            _logger.info(contractTxResult.toString());

            context.assertEquals(
                balanceRecipient1.add(
                    paymentValue.multiply(BigDecimal.valueOf(0.4)).toBigInteger()),
                getAccount(initialReceiver1.getAddress()).getBalance());
            context.assertEquals(
                balanceRecipient2.add(
                    paymentValue.multiply(BigDecimal.valueOf(0.4)).toBigInteger()),
                getAccount(initialReceiver2.getAddress()).getBalance());
            context.assertEquals(
                balanceRecipient3.add(
                    paymentValue.multiply(BigDecimal.valueOf(0.2)).toBigInteger()),
                getAccount(initialReceiver3.getAddress()).getBalance());
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void c_callGetTotalAmountSplitted(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            Object decodedValue =
                aeternityService.transactions.blockingReadOnlyContractCall(
                    contractId, "getTotalAmountSplitted", null, paymentSplitterSource, null);
            _logger.info(decodedValue.toString());
            context.assertEquals(1000000000000000000L, decodedValue);
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }

  @Test
  public void d_callGetOwner(TestContext context) throws Throwable {
    this.executeTest(
        context,
        t -> {
          try {
            Object decodedValue =
                aeternityService.transactions.blockingReadOnlyContractCall(
                    contractId, "getOwner", null, paymentSplitterSource, null);
            _logger.info(decodedValue.toString());
            context.assertEquals(
                "ak_twR4h7dEcUtc2iSEDv8kB7UFJJDGiEDQCXr85C3fYF8FdVdyo", decodedValue);
          } catch (Throwable e) {
            context.fail(e);
          }
        });
  }
}
