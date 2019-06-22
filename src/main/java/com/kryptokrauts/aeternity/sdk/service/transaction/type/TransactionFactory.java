package com.kryptokrauts.aeternity.sdk.service.transaction.type;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.DebugApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.BaseFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCallFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCreateFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.CreateChannelDepositTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.CreateContractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.StaticContractCallTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
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

  private DefaultApi compilerApi;

  private DebugApi debugApi;

  private static FeeCalculationModel baseFeeCalculationModel = new BaseFeeCalculationModel();

  private static FeeCalculationModel contractCreateFeeCalculationModel =
      new ContractCreateFeeCalculationModel();

  private static FeeCalculationModel contractCallFeeCalculationModel =
      new ContractCallFeeCalculationModel();

  /**
   * create a SpendTransaction
   *
   * @param sender sender public key
   * @param recipient recipient public key
   * @param amount amount
   * @param payload payload
   * @param ttl time to live
   * @param nonce account nonce
   * @return a {@link SpendTransaction} object
   */
  public SpendTransaction createSpendTransaction(
      String sender,
      String recipient,
      BigInteger amount,
      String payload,
      BigInteger ttl,
      BigInteger nonce) {
    return SpendTransaction.builder()
        .sender(sender)
        .recipient(recipient)
        .amount(amount)
        .payload(payload)
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
      BigInteger ttl,
      String stateHash,
      BigInteger round,
      BigInteger nonce) {
    return CreateChannelDepositTransaction.builder()
        .channelId(channelId)
        .fromId(fromId)
        .amount(amount)
        .ttl(ttl)
        .stateHash(stateHash)
        .round(round)
        .nonce(nonce)
        .feeCalculationModel(baseFeeCalculationModel)
        .channelApi(channelApi)
        .build();
  }

  public CreateContractTransaction createContractCreateTransaction(
      BigInteger abiVersion,
      BigInteger amount,
      String callData,
      String contractByteCode,
      BigInteger deposit,
      BigInteger gas,
      BigInteger gasPrice,
      BigInteger nonce,
      String ownerId,
      BigInteger ttl,
      BigInteger vmVersion) {
    return CreateContractTransaction.builder()
        .abiVersion(abiVersion)
        .amount(amount)
        .callData(callData)
        .contractByteCode(contractByteCode)
        .deposit(deposit)
        .gas(gas)
        .gasPrice(gasPrice)
        .nonce(nonce)
        .ownerId(ownerId)
        .ttl(ttl)
        .vmVersion(vmVersion)
        .feeCalculationModel(contractCreateFeeCalculationModel)
        .contractApi(contractApi)
        .compilerApi(compilerApi)
        .build();
  }

  public ContractCallTransaction createContractCallTransaction(
      BigInteger abiVersion,
      String callData,
      String contractId,
      BigInteger gas,
      BigInteger gasPrice,
      BigInteger nonce,
      String callerId,
      BigInteger ttl) {
    return ContractCallTransaction.builder()
        .abiVersion(abiVersion)
        .amount(BigInteger.ZERO) // we provide a separate
        // setter for the
        // optional amount
        .callData(callData)
        .callerId(callerId)
        .contractId(contractId)
        .gas(gas)
        .gasPrice(gasPrice)
        .nonce(nonce)
        .ttl(ttl)
        .contractApi(contractApi)
        .feeCalculationModel(contractCallFeeCalculationModel)
        .build();
  }

  public StaticContractCallTransaction createStaticContractCallTransaction(
      BigInteger abiVersion,
      String callData,
      String contractId,
      BigInteger gas,
      BigInteger nonce,
      String callerId,
      BigInteger ttl) {
    return StaticContractCallTransaction.builder()
        .abiVersion(abiVersion)
        .amount(BigInteger.ZERO) // we provide a
        // separate
        // setter for the
        // optional amount
        .callData(callData)
        .callerId(callerId)
        .contractId(contractId)
        .gas(gas)
        .gasPrice(BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE))
        .nonce(nonce)
        .ttl(ttl)
        .contractApi(contractApi)
        .debugApi(debugApi)
        .feeCalculationModel(contractCallFeeCalculationModel)
        .build();
  }
}
