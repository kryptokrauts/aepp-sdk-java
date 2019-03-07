package com.kryptokrauts.aeternity.sdk.service.transaction.type;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.BaseFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.CreateChannelDepositTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
import java.math.BigInteger;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;

/**
 * this factory provides a convenient way of accessing all currently supported transaction types and
 * abstracts the creation of further necessary or not customizable parameters (f.e. calculation
 * model)
 */
@AllArgsConstructor
public class TransactionFactory {

  private TransactionApi transactionApi;

  private ChannelApi channelApi;

  private ContractApi contractApi;

  private static FeeCalculationModel baseFeeCalculationModel = new BaseFeeCalculationModel();

  private static FeeCalculationModel contractFeeCalculationModel =
      new ContractFeeCalculationModel();

  /**
   * create a SpendTransaction
   *
   * @param sender sender public key
   * @param recipient recipient public key
   * @param amount amount
   * @param payload payload
   * @param fee fee, if null the minimal transaction fee will be automatically calculated (default)
   * @param ttl time to live
   * @param nonce account nonce
   * @return a {@link SpendTransaction} object
   */
  public SpendTransaction createSpendTransaction(
      String sender,
      String recipient,
      BigInteger amount,
      String payload,
      @Nullable BigInteger fee,
      BigInteger ttl,
      BigInteger nonce) {
    return SpendTransaction.builder()
        .sender(sender)
        .recipient(recipient)
        .amount(amount)
        .payload(payload)
        .fee(fee)
        .ttl(ttl)
        .nonce(nonce)
        .feeCalculationModel(baseFeeCalculationModel)
        .transactionApi(transactionApi)
        .build();
  }

  /**
   * creates a CreateChannelDepositTransaction
   *
   * @param channelId
   * @param fromId
   * @param amount
   * @param fee fee, if null the minimal transaction fee will be automatically calculated (default)
   * @param ttl
   * @param stateHash
   * @param round
   * @param nonce
   * @return
   */
  public CreateChannelDepositTransaction createChannelDepositTransaction(
      String channelId,
      String fromId,
      BigInteger amount,
      BigInteger fee,
      BigInteger ttl,
      String stateHash,
      BigInteger round,
      BigInteger nonce) {
    return CreateChannelDepositTransaction.builder()
        .channelId(channelId)
        .fromId(fromId)
        .amount(amount)
        .fee(fee)
        .ttl(ttl)
        .stateHash(stateHash)
        .round(round)
        .nonce(nonce)
        .feeCalculationModel(baseFeeCalculationModel)
        .channelApi(channelApi)
        .build();
  }
}
