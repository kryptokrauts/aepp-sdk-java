package com.kryptokrauts.aeternity.sdk.constants;

public interface Argon2Configuration
{

    String SECRET_TYPE = "ed25519";

    String SYMMETRIC_ALGORITHM = "xsalsa20-poly1305";

    int MEMLIMIT_KIB = 65536;

    int OPSLIMIT = 2;

    int PARALLELISM = 1;

    int VERSION = 1;

    int DEFAULT_SALT_LENGTH = 16;

    String argon2Mode = "argon2id";

}
