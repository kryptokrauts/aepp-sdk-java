package com.kryptokrauts.aeternity.sdk.constants;

import org.bouncycastle.crypto.params.Argon2Parameters;

public interface Argon2Configuration {

    String SECRET_TYPE = "ed25519";

    String SYMMETRIC_ALGORITHM = "xsalsa20-poly1305";

    int MEMLIMIT_KIB = 65536;

    int OPSLIMIT = 2;

    int PARALLELISM = 1;

    int VERSION = 1;

    int argon2Parameter = Argon2Parameters.ARGON2_id;

}
