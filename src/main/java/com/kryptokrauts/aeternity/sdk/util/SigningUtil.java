package com.kryptokrauts.aeternity.sdk.util;

import static com.kryptokrauts.aeternity.sdk.util.CryptoUtils.privateKeyCipherParamsFromHex;
import static com.kryptokrauts.aeternity.sdk.util.CryptoUtils.publicKeyCipherParamsFromHex;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.util.encoders.Hex;

/** This util class provides all methods related to signing */
@UtilityClass
public class SigningUtil {

  /**
   * sign data with given privateKey
   *
   * @param data to sign
   * @param privateKey to sign with
   * @return raw signed data
   * @throws CryptoException
   */
  public static final byte[] sign(final String data, final String privateKey)
      throws CryptoException {
    return sign(Hex.decode(data), privateKey);
  }

  /**
   * sign raw data with given privateKey
   *
   * @param data raw data to sign
   * @param privateKey to sign with
   * @return raw signed data
   * @throws CryptoException
   */
  public static final byte[] sign(final byte[] data, final String privateKey)
      throws CryptoException {
    Signer signer = new Ed25519Signer();
    signer.init(true, privateKeyCipherParamsFromHex(privateKey));
    signer.update(data, 0, data.length);
    return signer.generateSignature();
  }

  /**
   * sign a message with given privateKey
   *
   * @param message aeternity message to sign
   * @param privateKey to sign with
   * @return raw signed data
   * @throws CryptoException
   */
  public static final byte[] signMessage(final String message, final String privateKey)
      throws CryptoException {
    return sign(EncodingUtils.hash(messageToBinary(message)), privateKey);
  }

  /**
   * verify the given public keys signature of the given hashed data
   *
   * @param data hex encoded hash
   * @param signature to verify
   * @param publicKey
   * @return verification valid
   */
  public static final boolean verify(
      final String data, final byte[] signature, final String publicKey) {
    byte[] dataBinary = Hex.decode(data);
    return verify(dataBinary, signature, publicKey);
  }

  /**
   * verify the given public keys signature of the given raw data
   *
   * @param data raw data
   * @param signature to verify
   * @param publicKey
   * @return verification valid
   */
  public static final boolean verify(
      final byte[] data, final byte[] signature, final String publicKey) {
    Signer verifier = new Ed25519Signer();
    verifier.init(false, publicKeyCipherParamsFromHex(publicKey));
    verifier.update(data, 0, data.length);
    return verifier.verifySignature(signature);
  }

  /**
   * verify the given public keys signature of the given aeternity message
   *
   * @param message
   * @param signature to verify
   * @param publicKey
   * @return verification valid
   */
  public static final boolean verifyMessage(
      final String message, final byte[] signature, final String publicKey) {
    return verify(EncodingUtils.hash(messageToBinary(message)), signature, publicKey);
  }

  private static final byte[] messageToBinary(final String message) {
    final byte[] p = BaseConstants.AETERNITY_MESSAGE_PREFIX.getBytes(StandardCharsets.UTF_8);
    final byte[] msg = message.getBytes(StandardCharsets.UTF_8);
    if (msg.length > BaseConstants.MAX_MESSAGE_LENGTH) {
      throw new IllegalArgumentException(
          String.format("Message exceeds allow maximum size %s", BaseConstants.MAX_MESSAGE_LENGTH));
    }
    final byte[] pLength = {(byte) p.length};
    final byte[] msgLength = {(byte) msg.length};
    return ByteUtils.concatenate(pLength, p, msgLength, msg);
  }
}
