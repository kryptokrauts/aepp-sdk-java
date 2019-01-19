package com.kryptokrauts.aeternity.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keystore {

    @JsonProperty("public_key")
    private String publicKey;

    @JsonProperty("crypto")
    private Crypto crypto;

    private String id;

    private String name;

    private int version;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Crypto {
        @JsonProperty("secret_type")
        private String secretType;

        @JsonProperty("symmetric_alg")
        private String symmetricAlgorithm;

        @JsonProperty("ciphertext")
        private String cipherText;

        @JsonProperty("cipher_params")
        private CipherParams cipherParams;

        private String kdf;

        @JsonProperty("kdf_params")
        private KdfParams kdfParams;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CipherParams {
        private String nonce;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KdfParams {

        @JsonProperty("memlimit_kib")
        private int memLimitKib;

        @JsonProperty("opslimit")
        private int opsLimit;

        private String salt;

        private int parallelism;
    }
}
