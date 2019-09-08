package com.kryptokrauts.aeternity.sdk.service.info.domain;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class KeyBlockResult extends GenericResultObject<KeyBlock, KeyBlockResult> {

	private String hash;

	private BigInteger height;

	private String prevHash;

	private String prevKeyHash;

	private String stateHash;

	private String miner;

	private String beneficiary;

	private BigInteger target;

	@Default
	private List<Integer> pow = new LinkedList<Integer>();

	private BigInteger nonce;

	private BigInteger time;

	private BigInteger version;

	private String info;

	@Override
	protected KeyBlockResult map(KeyBlock generatedResultObject) {
		if (generatedResultObject != null)
			return this.toBuilder().hash(generatedResultObject.getHash()).height(generatedResultObject.getHeight())
					.prevHash(generatedResultObject.getPrevHash()).prevKeyHash(generatedResultObject.getPrevKeyHash())
					.stateHash(generatedResultObject.getStateHash()).miner(generatedResultObject.getMiner())
					.beneficiary(generatedResultObject.getBeneficiary()).target(generatedResultObject.getTarget())
					.pow(generatedResultObject.getPow()).nonce(generatedResultObject.getNonce())
					.time(generatedResultObject.getTime()).version(generatedResultObject.getVersion())
					.info(generatedResultObject.getInfo()).build();
		else
			return this.toBuilder().build();
	}

	@Override
	protected String getResultObjectClassName() {
		return this.getClass().getName();
	}

}
