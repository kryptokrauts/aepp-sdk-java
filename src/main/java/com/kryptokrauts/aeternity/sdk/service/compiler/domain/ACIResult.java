package com.kryptokrauts.aeternity.sdk.service.compiler.domain;

import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import com.kryptokrauts.sophia.compiler.generated.model.ACI;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ACIResult extends GenericResultObject<ACI, ACIResult> {

	private Object encodedAci;

	private String _interface;

	@Override
	protected ACIResult map(ACI generatedResultObject) {
		if (generatedResultObject != null)
			return this.toBuilder().encodedAci(generatedResultObject.getEncodedAci())
					._interface(generatedResultObject.getInterface()).build();
		else
			return this.toBuilder().build();
	}

	@Override
	protected String getResultObjectClassName() {
		return this.getClass().getName();
	}

}
