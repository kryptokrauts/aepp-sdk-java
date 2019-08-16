package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.model.SpendTx;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * this class describes the input values of a spend transaction
 *
 * @param sender    senders public key
 * @param recipient recipients public key
 * @param amount    ættos to send
 * @param payload   payload / message
 * @param ttl       time to live (maximum height of a block to include the tx)
 * @param nonce     senders nonce + 1
 * 
 */
@Getter
@SuperBuilder(builderMethodName = "setParameters", buildMethodName = "wrap", toBuilder = true)
public class SpendTransactionModel extends AbstractTransactionModel<SpendTx> {
	@NonNull
	private String sender;
	@NonNull
	private String recipient;
	@NonNull
	private BigInteger amount;

	private String payload;
	@NonNull
	private BigInteger ttl;
	@NonNull
	private BigInteger nonce;

	@Override
	public SpendTx toApiModel() {
		SpendTx spendTx = new SpendTx();
		spendTx.setSenderId(this.sender);
		spendTx.setRecipientId(this.recipient);
		spendTx.setAmount(this.amount);
		spendTx.setPayload(this.payload);
		spendTx.setFee(this.fee);
		spendTx.setTtl(this.ttl);
		spendTx.setNonce(this.nonce);

		return spendTx;
	}

	@Override
	public void validateInput() {
		// nothing to validate here
	}
}
