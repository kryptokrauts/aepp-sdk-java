package com.kryptokrauts.aeternity.sdk.service.transaction.type;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.NameServiceApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.BaseFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCallFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCreateFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.CreateChannelDepositTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.CreateContractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameClaimTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NamePreclaimTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameRevokeTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameUpdateTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.List;
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

  private NameServiceApi nameServiceApi;

  private DefaultApi compilerApi;

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

  /**
   * create a contractCreate Transaction
   *
   * @param abiVersion
   * @param amount
   * @param callData
   * @param contractByteCode
   * @param deposit
   * @param gas
   * @param gasPrice
   * @param nonce
   * @param ownerId
   * @param ttl
   * @param vmVersion
   * @return
   */
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

  /**
   * create a contractCall Transaction
   *
   * @param abiVersion
   * @param callData
   * @param contractId
   * @param gas
   * @param gasPrice
   * @param nonce
   * @param callerId
   * @param ttl
   * @return
   */
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

  /**
   * create a namePreclaimTx
   *
   * @param accountId
   * @param name
   * @param salt
   * @param nonce
   * @param ttl
   * @return
   */
  public NamePreclaimTransaction createNamePreclaimTransaction(
      String accountId, String name, BigInteger salt, BigInteger nonce, BigInteger ttl) {
    return NamePreclaimTransaction.builder()
        .accountId(accountId)
        .name(name)
        .salt(salt)
        .nonce(nonce)
        .ttl(ttl)
        .nameServiceApi(nameServiceApi)
        .feeCalculationModel(baseFeeCalculationModel)
        .build();
  }

  /**
   * create a nameRevokeTx
   *
   * @param accountId
   * @param nameId
   * @param nonce
   * @param ttl
   * @return
   */
  public NameRevokeTransaction createNameRevokeTransaction(
      String accountId, String nameId, BigInteger nonce, BigInteger ttl) {
    return NameRevokeTransaction.builder()
        .accountId(accountId)
        .nameId(nameId)
        .nonce(nonce)
        .ttl(ttl)
        .nameServiceApi(nameServiceApi)
        .feeCalculationModel(baseFeeCalculationModel)
        .build();
  }

  /**
   * create a nameUpdateTx
   *
   * @param accountId
   * @param nameId
   * @param nonce
   * @param ttl
   * @return
   */
  public NameUpdateTransaction createNameUpdateTransaction(
      String accountId,
      String nameId,
      BigInteger nonce,
      BigInteger ttl,
      BigInteger clientTtl,
      BigInteger nameTtl,
      List<NamePointer> pointers) {
    return NameUpdateTransaction.builder()
        .accountId(accountId)
        .nameId(nameId)
        .nonce(nonce)
        .ttl(ttl)
        .clientTtl(clientTtl)
        .nameTtl(nameTtl)
        .pointers(pointers)
        .nameServiceApi(nameServiceApi)
        .feeCalculationModel(baseFeeCalculationModel)
        .build();
  }

  /**
   * create a claimTx
   *
   * @param accountId
   * @param name
   * @param nameSalt
   * @param nonce
   * @param ttl
   * @return
   */
  public NameClaimTransaction createNameClaimTransaction(
      String accountId, String name, BigInteger nameSalt, BigInteger nonce, BigInteger ttl) {
    return NameClaimTransaction.builder()
        .accountId(accountId)
        .name(name)
        .nameSalt(nameSalt)
        .nonce(nonce)
        .ttl(ttl)
        .nameServiceApi(nameServiceApi)
        .feeCalculationModel(baseFeeCalculationModel)
        .build();
  }
}
