package com.kryptokrauts.aeternity.sdk.constants;

import org.bouncycastle.crypto.params.Argon2Parameters;

public interface Argon2Configuration
{

    public static final String SECRET_TYPE = "ed25519";

    public static final String SYMMETRIC_ALGORITHM = "xsalsa20-poly1305";

    public static final int MEMLIMIT_KIB = 65536;

    public static final int OPSLIMIT = 2;

    public static final int PARALLELISM = 1;

    public static final int VERSION = 1;

    public static final int argon2Parameter = Argon2Parameters.ARGON2_id;

    public static final int SALT_HEX_SIZE = 16;

    public static final int NONCE_HEX_SIZE = 24;

    public static final String argon2Mode = "argon2id";

}
