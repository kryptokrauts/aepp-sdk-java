package com.kryptokrauts.aeternity.sdk.service.transaction.type;

import java.math.BigInteger;
import java.util.List;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.BaseFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCallFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCreateFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCreateTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelDepositTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCreateTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameClaimTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NamePreclaimTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameRevokeTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameUpdateTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;

import lombok.AllArgsConstructor;

/**
 * this factory provides a convenient way of accessing all currently supported
 * transaction types and abstracts the creation of further necessary or not
 * customizable parameters (f.e. calculation model)
 */
@AllArgsConstructor
public class TransactionFactory {

	private ExternalApi externalApi;

	private static FeeCalculationModel baseFeeCalculationModel = new BaseFeeCalculationModel();

	private static FeeCalculationModel contractCreateFeeCalculationModel = new ContractCreateFeeCalculationModel();

	private static FeeCalculationModel contractCallFeeCalculationModel = new ContractCallFeeCalculationModel();

	/**
	 * create a spendTx
	 *
	 * 
	 * @return a {@link SpendTransaction} object
	 */
	public SpendTransaction createSpendTransaction(SpendTransactionModel model) {

		return SpendTransaction.builder().model(model).externalApi(externalApi).build();
//		return SpendTransaction.builder().sender(sender).recipient(recipient).amount(amount).payload(payload).ttl(ttl)
//				.nonce(nonce).feeCalculationModel(baseFeeCalculationModel).transactionApi(transactionApi).build();
	}

	/**
	 * create a contractCreateTx
	 *
	 * @param abiVersion       version of the ABI
	 * @param amount           ættos to transfer to the contract (optional)
	 * @param callData         api encoded compiled AEVM calldata for the code
	 *                         (init/main-function that will be called when contract
	 *                         is being deployed). can be obtained using
	 *                         {@link com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService}
	 * @param contractByteCode api encoded compiled AEVM bytecode. can be obtained
	 *                         using
	 *                         {@link com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService}
	 * @param deposit          ættos that will be locked by the contract. currently
	 *                         unused, but future versions of the protocol will
	 *                         probably allow a contract deactivation where the
	 *                         deposit amount is then being released (optional)
	 * @param gas              gas for the initial call. from protocol version 4.0.0
	 *                         (Fortuna major release) this can be obtained using
	 *                         dryRun functionality available in
	 *                         {@link com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService}
	 * @param gasPrice         gas price for the call. must be at least the minimum
	 *                         gas price to follow consensus. see
	 *                         {@link com.kryptokrauts.aeternity.sdk.constants.BaseConstants}
	 * @param nonce            owners nonce + 1
	 * @param ownerId          the public key of the owner/creator that signs the
	 *                         transaction
	 * @param ttl              time to live (time to live (maximum height of a block
	 *                         to include the tx))
	 * @param vmVersion        version of the AEVM
	 * @return a {@link ContractCreateTransaction} object
	 */
	public ContractCreateTransaction createContractCreateTransaction(BigInteger abiVersion, BigInteger amount,
			String callData, String contractByteCode, BigInteger deposit, BigInteger gas, BigInteger gasPrice,
			BigInteger nonce, String ownerId, BigInteger ttl, BigInteger vmVersion) {
//		return CreateContractTransaction.builder().abiVersion(abiVersion).amount(amount).callData(callData)
//				.contractByteCode(contractByteCode).deposit(deposit).gas(gas).gasPrice(gasPrice).nonce(nonce)
//				.ownerId(ownerId).ttl(ttl).vmVersion(vmVersion).feeCalculationModel(contractCreateFeeCalculationModel)
//				.contractApi(contractApi).compilerApi(compilerApi).build();
		return null;
	}

	/**
	 * create a contractCallTx
	 *
	 * <p>
	 * for static-calls (read-only) the dryRun-functionality in
	 * {@link com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService}
	 * can be used and the transaction doesn't have to be submitted.
	 *
	 * @param abiVersion version of the ABI
	 * @param callData   api encoded compiled AEVM calldata for the code (function
	 *                   that will be called within the tx). can be obtained using
	 *                   {@link com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService}
	 * @param contractId address of the contract
	 * @param gas        gas for the function call. this can be obtained using
	 *                   dryRun functionality available in
	 *                   {@link com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService}
	 * @param gasPrice   gas price for the call. must be at least the minimum gas
	 *                   price to follow consensus. see
	 *                   {@link com.kryptokrauts.aeternity.sdk.constants.BaseConstants}
	 * @param nonce      callers nonce + 1
	 * @param callerId   the public key of the caller that signs the transaction
	 * @param ttl        time to live (maximum height of a block to include the tx)
	 * @return a {@link ContractCallTransaction} object
	 */
	public ContractCallTransaction createContractCallTransaction(BigInteger abiVersion, String callData,
			String contractId, BigInteger gas, BigInteger gasPrice, BigInteger nonce, String callerId, BigInteger ttl) {
//		return ContractCallTransaction.builder().abiVersion(abiVersion).amount(BigInteger.ZERO) // we provide a separate
//				// setter for the
//				// optional amount
//				.callData(callData).callerId(callerId).contractId(contractId).gas(gas).gasPrice(gasPrice).nonce(nonce)
//				.ttl(ttl).contractApi(contractApi).feeCalculationModel(contractCallFeeCalculationModel).build();
		return null;
	}

	/**
	 * create a namePreclaimTx
	 *
	 * @param accountId senders public key
	 * @param name      the domain to preclaim
	 * @param salt      a random salt that is later necessary to claim the name
	 * @param nonce     senders nonce + 1
	 * @param ttl       time to live (maximum height of a block to include the tx)
	 * @return a {@link NamePreclaimTransaction} object
	 */
	public NamePreclaimTransaction createNamePreclaimTransaction(String accountId, String name, BigInteger salt,
			BigInteger nonce, BigInteger ttl) {
//		return NamePreclaimTransaction.builder().accountId(accountId).name(name).salt(salt).nonce(nonce).ttl(ttl)
//				.nameServiceApi(nameServiceApi).feeCalculationModel(baseFeeCalculationModel).build();
		return null;
	}

	/**
	 * create a nameClaimTx
	 *
	 * <p>
	 * by default the domain will be claimed for 50000 blocks
	 *
	 * @param accountId senders public key
	 * @param name      the domain to claim
	 * @param nameSalt  the salt provided on the preclaim transaction
	 * @param nonce     senders nonce + 1
	 * @param ttl       time to live (maximum height of a block to include the tx)
	 * @return a {@link NameClaimTransaction} object
	 */
	public NameClaimTransaction createNameClaimTransaction(String accountId, String name, BigInteger nameSalt,
			BigInteger nonce, BigInteger ttl) {
//		return NameClaimTransaction.builder().accountId(accountId).name(name).nameSalt(nameSalt).nonce(nonce).ttl(ttl)
//				.nameServiceApi(nameServiceApi).feeCalculationModel(baseFeeCalculationModel).build();
		return null;
	}

	/**
	 * create a nameUpdateTx
	 *
	 * @param accountId senders public key
	 * @param nameId    the domain to update
	 * @param nonce     senders nonce + 1
	 * @param ttl       time to live (maximum height of a block to include the tx)
	 * @param clientTtl optional indicator for the clients until the requested
	 *                  domain-data can be cached locally
	 * @param nameTtl   new time to live (in blocks) for the domain (will be added
	 *                  to block-height the tx is included in)
	 * @param pointers  list of pointers to addresses of different types (account,
	 *                  oracle, contract, ...)
	 * @return a {@link NameUpdateTransaction} object
	 */
	public NameUpdateTransaction createNameUpdateTransaction(String accountId, String nameId, BigInteger nonce,
			BigInteger ttl, BigInteger clientTtl, BigInteger nameTtl, List<NamePointer> pointers) {
//		return NameUpdateTransaction.builder().accountId(accountId).nameId(nameId).nonce(nonce).ttl(ttl)
//				.clientTtl(clientTtl).nameTtl(nameTtl).pointers(pointers).nameServiceApi(nameServiceApi)
//				.feeCalculationModel(baseFeeCalculationModel).build();
		return null;
	}

	/**
	 * create a nameRevokeTx
	 *
	 * @param accountId senders public key
	 * @param nameId    the domain to revoke
	 * @param nonce     senders nonce + 1
	 * @param ttl       time to live (maximum height of a block to include the tx)
	 * @return a {@link NameRevokeTransaction} object
	 */
	public NameRevokeTransaction createNameRevokeTransaction(String accountId, String nameId, BigInteger nonce,
			BigInteger ttl) {
//		return NameRevokeTransaction.builder().accountId(accountId).nameId(nameId).nonce(nonce).ttl(ttl)
//				.nameServiceApi(nameServiceApi).feeCalculationModel(baseFeeCalculationModel).build();
		return null;
	}

	/**
	 * @param initiator       initiators public key
	 * @param initiatorAmount amount of tokens the initiator has committed to the
	 *                        channel
	 * @param responder       responders public key
	 * @param responderAmount amount of tokens the responder has committed to the
	 *                        channel
	 * @param channelReserve  the minimum amount both peers need to maintain
	 * @param lockPeriod      amount of blocks for disputing a solo close
	 * @param ttl             time to live (maximum height of a block to include the
	 *                        tx)
	 * @param stateHash       TODO
	 * @param nonce           initiators nonce + 1
	 * @return a {@link ChannelCreateTransaction} object
	 */
	public ChannelCreateTransaction createChannelCreateTransaction(String initiator, BigInteger initiatorAmount,
			String responder, BigInteger responderAmount, BigInteger channelReserve, BigInteger lockPeriod,
			BigInteger ttl, String stateHash, BigInteger nonce) {
//		return ChannelCreateTransaction.builder().initiator(initiator).initiatorAmount(initiatorAmount)
//				.responder(responder).responderAmount(responderAmount).channelReserve(channelReserve)
//				.lockPeriod(lockPeriod).ttl(ttl).stateHash(stateHash).nonce(nonce).channelApi(channelApi)
//				.feeCalculationModel(baseFeeCalculationModel).build();
		return null;
	}

	/**
	 * creates a ChannelDepositTransaction
	 *
	 * @param channelId the id of the channel
	 * @param fromId    senders public key
	 * @param amount    ættos to deposit
	 * @param ttl       time to live (maximum height of a block to include the tx)
	 * @param stateHash TODO
	 * @param round     TODO currentRound vs nextRound?
	 * @param nonce     senders nonce + 1
	 * @return a {@link ChannelDepositTransaction} object
	 */
	public ChannelDepositTransaction createChannelDepositTransaction(String channelId, String fromId, BigInteger amount,
			BigInteger ttl, String stateHash, BigInteger round, BigInteger nonce) {
//		return ChannelDepositTransaction.builder().channelId(channelId).fromId(fromId).amount(amount).ttl(ttl)
//				.stateHash(stateHash).round(round).nonce(nonce).feeCalculationModel(baseFeeCalculationModel)
//				.channelApi(channelApi).build();
		return null;
	}
}
