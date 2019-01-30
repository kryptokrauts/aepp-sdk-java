package com.kryptokrauts.aeternity.sdk.service.keypair;

import com.kryptokrauts.aeternity.sdk.service.config.ServiceConfiguration;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
// @Builder( builderMethodName = "configure", buildMethodName = "compile" )
@SuperBuilder
public class KeyPairServiceConfiguration extends ServiceConfiguration {
    @Default
    private String cipherAlgorithm = "AES/ECB/NoPadding";

    @Default
    private String secretKeySpec = "AES";
}
