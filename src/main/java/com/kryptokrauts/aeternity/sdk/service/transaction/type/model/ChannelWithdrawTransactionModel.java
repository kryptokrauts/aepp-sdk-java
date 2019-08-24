package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelWithdrawTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelWithdrawTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelWithdrawTransactionModel extends AbstractTransactionModel<ChannelWithdrawTx> {

	@NonNull
	private String channelId;
	@NonNull
	private String toId;
	@NonNull
	private BigInteger amount;
	@NonNull
	private BigInteger ttl;
	@NonNull
	private String stateHash;
	@NonNull
	private BigInteger round;
	@NonNull
	private BigInteger nonce;

	@Override
	public ChannelWithdrawTx toApiModel() {
		ChannelWithdrawTx channelWithdrawTx = new ChannelWithdrawTx();
		channelWithdrawTx.setChannelId(channelId);
		channelWithdrawTx.setToId(toId);
		channelWithdrawTx.setAmount(amount);
		channelWithdrawTx.setFee(fee);
		channelWithdrawTx.setTtl(ttl);
		channelWithdrawTx.setStateHash(stateHash);
		channelWithdrawTx.setRound(round);
		channelWithdrawTx.setNonce(nonce);
		return channelWithdrawTx;
	}

	@Override
	public void validateInput() {
	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return ChannelWithdrawTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
