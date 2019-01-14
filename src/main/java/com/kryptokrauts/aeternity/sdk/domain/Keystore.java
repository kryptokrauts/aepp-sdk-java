package com.kryptokrauts.aeternity.sdk.domain;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keystore {

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("crypto")
    private Crypto crypto;

    private String id;

    private String name;

    private int version;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Crypto {
        @SerializedName("secret_type")
        private String secretType;

        @SerializedName("symmetric_alg")
        private String symmetricAlgorithm;

        @SerializedName("ciphertext")
        private String cipherText;

        @SerializedName("cipher_params")
        private CipherParams cipherParams;

        private String kdf;

        @SerializedName("kdf_params")
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

        @SerializedName("memlimit_kib")
        private int memLimitKib;

        @SerializedName("opslimit")
        private int opsLimit;

        private String salt;

        private int parallelism;
    }
}
