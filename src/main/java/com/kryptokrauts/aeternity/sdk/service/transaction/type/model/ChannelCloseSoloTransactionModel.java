package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCloseSoloTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCloseSoloTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelCloseSoloTransactionModel extends AbstractTransactionModel<ChannelCloseSoloTx> {
	@NonNull
	private String channelId;
	@NonNull
	private String fromId;
	@NonNull
	private String payload;
	@NonNull
	private String poi;
	@NonNull
	private BigInteger ttl;
	@NonNull
	private BigInteger nonce;

	@Override
	public ChannelCloseSoloTx toApiModel() {
		ChannelCloseSoloTx channelCloseSoloTx = new ChannelCloseSoloTx();
		channelCloseSoloTx.setChannelId(channelId);
		channelCloseSoloTx.setFromId(fromId);
		channelCloseSoloTx.setPayload(payload);
		channelCloseSoloTx.setPoi(poi);
		channelCloseSoloTx.setFee(fee);
		channelCloseSoloTx.setTtl(ttl);
		channelCloseSoloTx.setNonce(nonce);
		return channelCloseSoloTx;
	}

	@Override
	public void validateInput() {
	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return ChannelCloseSoloTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
