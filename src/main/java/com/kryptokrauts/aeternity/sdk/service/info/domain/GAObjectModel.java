package com.kryptokrauts.aeternity.sdk.service.info.domain;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.model.GAObject;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class GAObjectModel extends GenericResultObject<GAObject, GAObjectModel> {

	private String callerId;

	private BigInteger height;

	private BigInteger gasPrice;

	private BigInteger gasUsed;

	private String returnValue;

	private String returnType;

	private TransactionInfoResult innerObject;

	@Override
	protected GAObjectModel map(GAObject generatedResultObject) {
		if (generatedResultObject != null)
			return this.toBuilder().callerId(generatedResultObject.getCallerId())
					.height(generatedResultObject.getHeight()).gasPrice(generatedResultObject.getGasPrice())
					.gasUsed(generatedResultObject.getGasUsed()).returnValue(generatedResultObject.getReturnValue())
					.returnType(generatedResultObject.getReturnType())
					.innerObject(TransactionInfoResult.builder().build().map(generatedResultObject.getInnerObject()))
					.build();
		else
			return this.toBuilder().build();
	}

	@Override
	protected String getResultObjectClassName() {
		return this.getClass().getName();
	}

}
