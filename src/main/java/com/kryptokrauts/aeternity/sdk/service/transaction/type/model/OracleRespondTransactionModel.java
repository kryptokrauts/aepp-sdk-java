package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleRespondTx;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRespondTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OracleRespondTransactionModel extends AbstractTransactionModel<OracleRespondTx> {

	@NonNull
	private String senderId;
	@NonNull
	private String oracleId;
	@NonNull
	private String queryId;
	@NonNull
	private BigInteger nonce;
	@NonNull
	private String response;
	@NonNull
	private RelativeTTL responseTtl;
	@NonNull
	private String responseFormat;
	@NonNull
	private BigInteger ttl;

	@Override
	public OracleRespondTx toApiModel() {
		OracleRespondTx oracleRespondTx = new OracleRespondTx();
		oracleRespondTx.fee(this.fee);
		oracleRespondTx.nonce(this.nonce);
		oracleRespondTx.oracleId(this.oracleId);
		oracleRespondTx.queryId(this.queryId);
		oracleRespondTx.response(this.response);
		oracleRespondTx.responseTtl(this.responseTtl);
		oracleRespondTx.ttl(this.ttl);
		return oracleRespondTx;
	}

	@Override
	public void validateInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return OracleRespondTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
