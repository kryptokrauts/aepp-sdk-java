package com.kryptokrauts.aeternity.sdk.constants;

import org.bouncycastle.crypto.params.Argon2Parameters;

public interface Argon2Configuration {

    public static String SECRET_TYPE = "ed25519";

    public static String SYMMETRIC_ALGORITHM = "xsalsa20-poly1305";

    public static int MEMLIMIT_KIB = 65536;

    public static int OPSLIMIT = 2;

    public static int PARALLELISM = 1;

    public static int VERSION = 1;

    public static int argon2Parameter = Argon2Parameters.ARGON2_id;

}
