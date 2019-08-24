package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSettleTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelSettleTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelSettleTransactionModel extends AbstractTransactionModel<ChannelSettleTx> {
	@NonNull
	private String channelId;
	@NonNull
	private String fromId;
	@NonNull
	private BigInteger initiatorAmountFinal;
	@NonNull
	private BigInteger responderAmountFinal;
	@NonNull
	private BigInteger ttl;
	@NonNull
	private BigInteger nonce;

	@Override
	public ChannelSettleTx toApiModel() {
		ChannelSettleTx channelSettleTx = new ChannelSettleTx();
		channelSettleTx.setChannelId(channelId);
		channelSettleTx.setFromId(fromId);
		channelSettleTx.setInitiatorAmountFinal(initiatorAmountFinal);
		channelSettleTx.setResponderAmountFinal(responderAmountFinal);
		channelSettleTx.setFee(fee);
		channelSettleTx.setTtl(ttl);
		channelSettleTx.setNonce(nonce);
		return channelSettleTx;
	}

	@Override
	public void validateInput() {
	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return ChannelSettleTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
