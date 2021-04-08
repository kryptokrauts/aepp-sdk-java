package com.kryptokrauts.aeternity.sdk.service.keystore;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(builderMethodName = "configure", buildMethodName = "compile")
public class KeystoreServiceConfiguration extends ServiceConfiguration {
  @Default private String secretType = "ed25519";

  @Default private String symmetricAlgorithm = "xsalsa20-poly1305";

  @Default private int memlimitKIB = 65536;

  @Default private int opsLimit = 2;

  @Default private int parallelism = 1;

  @Default private int version = 1;

  @Default private int defaultSaltLength = 16;

  @Default private String argon2Mode = "argon2id";

  @Default private Argon2Types argon2Type = Argon2Types.ARGON2id;
}
