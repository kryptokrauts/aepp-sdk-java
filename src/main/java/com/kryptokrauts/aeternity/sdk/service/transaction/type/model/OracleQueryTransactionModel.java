package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleQueryTx;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleQueryTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OracleQueryTransactionModel extends AbstractTransactionModel<OracleQueryTx> {

	@NonNull
	private String senderId;
	@NonNull
	private String oracleId;
	@NonNull
	private BigInteger nonce;
	@NonNull
	private String query;
	@NonNull
	private BigInteger queryFee;
	@NonNull
	private TTL queryTtl;
	@NonNull
	private RelativeTTL responseTtl;
	@NonNull
	private BigInteger ttl;

	@Override
	public OracleQueryTx toApiModel() {
		OracleQueryTx oracleQueryTx = new OracleQueryTx();
		oracleQueryTx.senderId(this.senderId);
		oracleQueryTx.oracleId(this.oracleId);
		oracleQueryTx.fee(this.fee);
		oracleQueryTx.nonce(this.nonce);
		oracleQueryTx.query(this.query);
		oracleQueryTx.queryFee(this.queryFee);
		oracleQueryTx.queryTtl(this.queryTtl);
		oracleQueryTx.responseTtl(this.responseTtl);
		return oracleQueryTx;
	}

	@Override
	public void validateInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return OracleQueryTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
