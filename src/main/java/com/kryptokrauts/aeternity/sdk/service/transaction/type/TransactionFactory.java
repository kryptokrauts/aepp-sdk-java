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
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCreateTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelDepositTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
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
   * create a spendTx
   *
   * @param sender sender's public key
   * @param recipient recipient's public key
   * @param amount aeons to send
   * @param payload payload
   * @param ttl time to live
   * @param nonce signers nonce + 1
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
   * create a contractCreateTx
   *
   * @param abiVersion version of the ABI
   * @param amount aeons to transfer to the contract
   * @param callData api encoded compiled AEVM calldata for the code
   * @param contractByteCode api encoded compiled AEVM bytecode
   * @param deposit
   * @param gas gas for the initial call
   * @param gasPrice gas price for the call
   * @param nonce signers nonce + 1
   * @param ownerId the public key of the owner/creator that signs the transaction
   * @param ttl
   * @param vmVersion version of the AEVM
   * @return a {@link CreateContractTransaction} object
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
   * create a contractCallTx
   *
   * @param abiVersion version of the ABI
   * @param callData api encoded compiled AEVM calldata for the code
   * @param contractId address of the contract
   * @param gas gas for the call
   * @param gasPrice gas price for the call
   * @param nonce signers nonce + 1
   * @param callerId the public key of the caller that signs the transaction
   * @param ttl
   * @return a {@link ContractCallTransaction} object
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
   * @param name the domain to preclaim
   * @param salt a random salt that is later necessary to claim the name
   * @param nonce signers nonce + 1
   * @param ttl
   * @return a {@link NamePreclaimTransaction} object
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
   * create a nameClaimTx
   *
   * @param accountId
   * @param name the domain to claim
   * @param nameSalt the salt provided on the preclaim transaction
   * @param nonce signers nonce + 1
   * @param ttl
   * @return a {@link NameClaimTransaction} object
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

  /**
   * create a nameUpdateTx
   *
   * @param accountId
   * @param nameId the domain to update
   * @param nonce signers nonce + 1
   * @param ttl
   * @param clientTtl
   * @param nameTtl
   * @param pointers
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
   * create a nameRevokeTx
   *
   * @param accountId
   * @param nameId the domain to revoke
   * @param nonce signers nonce + 1
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
   * @param initiator initiator's public key
   * @param initiatorAmount amount of tokens the initiator has committed to the channel
   * @param responder responder's public key
   * @param responderAmount amount of tokens the responder has committed to the channel
   * @param channelReserve the minimum amount both peers need to maintain
   * @param lockPeriod amount of blocks for disputing a solo close
   * @param ttl minimum block height to include the channel_create_tx
   * @param stateHash TODO
   * @param nonce initiators nonce + 1
   * @return a {@link ChannelCreateTransaction} object
   */
  public ChannelCreateTransaction createChannelCreateTransaction(
      String initiator,
      BigInteger initiatorAmount,
      String responder,
      BigInteger responderAmount,
      BigInteger channelReserve,
      BigInteger lockPeriod,
      BigInteger ttl,
      String stateHash,
      BigInteger nonce) {
    return ChannelCreateTransaction.builder()
        .initiator(initiator)
        .initiatorAmount(initiatorAmount)
        .responder(responder)
        .responderAmount(responderAmount)
        .channelReserve(channelReserve)
        .lockPeriod(lockPeriod)
        .ttl(ttl)
        .stateHash(stateHash)
        .nonce(nonce)
        .channelApi(channelApi)
        .feeCalculationModel(baseFeeCalculationModel)
        .build();
  }

  /**
   * creates a ChannelDepositTransaction
   *
   * @param channelId the id of the channel
   * @param fromId sender's public key
   * @param amount aeons to deposit
   * @param ttl
   * @param stateHash TODO
   * @param round
   * @param nonce signers nonce + 1
   * @return a {@link ChannelDepositTransaction} object
   */
  public ChannelDepositTransaction createChannelDepositTransaction(
      String channelId,
      String fromId,
      BigInteger amount,
      BigInteger ttl,
      String stateHash,
      BigInteger round,
      BigInteger nonce) {
    return ChannelDepositTransaction.builder()
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
}
