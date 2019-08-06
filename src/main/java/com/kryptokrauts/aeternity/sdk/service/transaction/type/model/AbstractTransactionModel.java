package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(builderMethodName = "setParameters", buildMethodName = "wrap")
public abstract class AbstractTransactionModel<TxModel> {

	public abstract TxModel toApiModel();
}
