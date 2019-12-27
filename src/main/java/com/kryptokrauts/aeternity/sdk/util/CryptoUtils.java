package com.kryptokrauts.aeternity.sdk.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import lombok.experimental.UtilityClass;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;

/** this util class provides common methods */
@UtilityClass
public final class CryptoUtils {

  private static final SecureRandom secureRandom = new SecureRandom();

  /**
   * generate a securely randomed salt of given size
   *
   * @param size (in bytes) of the salt
   * @return salt as bytearray
   */
  public static final byte[] generateSalt(int size) {
    byte[] salt = new byte[size];
    secureRandom.nextBytes(salt);
    return salt;
  }

  /** @return positive salt */
  public static final BigInteger generateNamespaceSalt() {
    return new BigInteger(1, generateSalt(8));
  }

  /** @return an initialized SecureRandom object */
  public static final SecureRandom getSecureRandom() {
    return secureRandom;
  }

  /**
   * Extract CipherParameters from given privateKey
   *
   * @param privateKey (Hex string)
   * @return CipherParameters from given privateKey
   */
  public static CipherParameters privateKeyCipherParamsFromHex(final String privateKey) {
    final String privateKey32;
    if (privateKey.length() == 128) {
      privateKey32 = privateKey.substring(0, 64);
    } else {
      privateKey32 = privateKey;
    }
    return new Ed25519PrivateKeyParameters(Hex.decode(privateKey32), 0);
  }

  /**
   * extract CipherParameters from given publicKey
   *
   * @param publicKey (Hex string)
   * @return CipherParameters of the publicKey
   */
  public static CipherParameters publicKeyCipherParamsFromHex(final String publicKey) {
    return new Ed25519PublicKeyParameters(Hex.decode(publicKey), 0);
  }
}
