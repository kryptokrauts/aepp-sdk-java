package com.kryptokrauts.aeternity.sdk.service.keypair;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder( builderMethodName = "configure", buildMethodName = "compile" )
public class KeyPairServiceConfiguration extends ServiceConfiguration {
    @Default
    private String cipherAlgorithm = "AES/ECB/NoPadding";

    @Default
    private String secretKeySpec = "AES";
}
