package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.model.SpendTx;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(builderMethodName = "setParameters", buildMethodName = "wrap")
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
	protected void validateInput() {
		// nothing to validate here
	}
}
