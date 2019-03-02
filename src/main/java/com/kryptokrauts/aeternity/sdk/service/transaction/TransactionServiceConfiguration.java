package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(builderMethodName = "configure", buildMethodName = "compile")
public class TransactionServiceConfiguration extends ServiceConfiguration {

  @Default private boolean nativeMode = true;

  @Default private Network network = Network.TESTNET;
}
