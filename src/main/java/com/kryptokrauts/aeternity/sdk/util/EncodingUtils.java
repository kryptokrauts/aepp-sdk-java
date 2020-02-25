package com.kryptokrauts.aeternity.sdk.util;

import static com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers.IDENTIFIERS_B58_LIST;
import static com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers.IDENTIFIERS_B64_LIST;
import static com.kryptokrauts.aeternity.sdk.constants.BaseConstants.PREFIX_ZERO_X;
import static com.kryptokrauts.aeternity.sdk.util.EncodingType.BASE58;
import static com.kryptokrauts.aeternity.sdk.util.EncodingType.BASE64;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.EncodingNotSupportedException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

/** this util class provides all encoding related methods */
@UtilityClass
public final class EncodingUtils {

  /**
   * encode input with encoding determined from given identifier String
   *
   * @param input the input to encode
   * @param identifier see {@link ApiIdentifiers}
   * @return base58 or base64 encoded string
   * @throws EncodingNotSupportedException if the encoding type cannot be determined for some reason
   * @throws IllegalArgumentException if the identifier is unknown
   */
  public static final String encodeCheck(final byte[] input, String identifier)
      throws IllegalArgumentException, EncodingNotSupportedException {
    if (identifier != null && identifier.trim().length() > 0) {
      String encoded;
      // determine encoding from given identifier
      if (IDENTIFIERS_B58_LIST.contains(identifier)) {
        encoded = encodeCheck(input, BASE58);
      } else if (IDENTIFIERS_B64_LIST.contains(identifier)) {
        encoded = encodeCheck(input, BASE64);
      } else {
        throw new IllegalArgumentException("unknown identifier");
      }
      return identifier + "_" + encoded;
    }
    // if we get to this point, we cannot determine the encoding
    throw new EncodingNotSupportedException(
        String.format("Cannot determine encoding type from given identifier %s", identifier));
  }

  /**
   * encode input with given encodingType
   *
   * @param input the input to encode
   * @param encodingType see {@link EncodingType}
   * @return base58 or base64 encoded string
   * @throws EncodingNotSupportedException if the encodingType is not supported
   * @throws IllegalArgumentException if the encodingType is null
   */
  public static final String encodeCheck(final byte[] input, EncodingType encodingType)
      throws EncodingNotSupportedException {
    if (encodingType == null) {
      throw new IllegalArgumentException("encodingType mustn't be null");
    }
    switch (encodingType) {
      case BASE58:
        return encodeBase58Check(input);
      case BASE64:
        return encodeBase64Check(input);
      default:
        throw new EncodingNotSupportedException(
            String.format("Encoding %s is currently not supported", encodingType));
    }
  }

  /**
   * decode input which is combined of the identifier and the encoded string (e.g. ak_[encoded])
   *
   * @param input the encoded string to decode
   * @return the decoded bytearray
   * @throws IllegalArgumentException if the input has a wrong/unexpected format
   */
  public static final byte[] decodeCheckWithIdentifier(final String input)
      throws IllegalArgumentException {
    String[] splitted = input.split("_");
    if (splitted.length != 2) {
      throw new IllegalArgumentException("input has wrong format");
    }
    String identifier = splitted[0];
    String encoded = splitted[1];
    return decodeCheck(encoded, identifier);
  }

  /**
   * decode input with encoding determined from given identifier String
   *
   * @param input the encoded string to decode
   * @param identifier see @{@link ApiIdentifiers}
   * @return the decoded bytearray
   * @throws EncodingNotSupportedException if the encoding type cannot be determined for some reason
   */
  private static final byte[] decodeCheck(final String input, String identifier)
      throws EncodingNotSupportedException {
    if (identifier != null && identifier.trim().length() > 0) {
      // determine encoding from given identifier
      if (IDENTIFIERS_B58_LIST.contains(identifier)) {
        return decodeCheck(input, BASE58);
      } else if (IDENTIFIERS_B64_LIST.contains(identifier)) {
        return decodeCheck(input, BASE64);
      }
    }
    // if we get to this point, we cannot determine the encoding
    throw new EncodingNotSupportedException(
        String.format("Cannot determine encoding type from given identifier %s", identifier));
  }

  /**
   * decode input with given encodingType
   *
   * @param input the input to decode
   * @param encodingType see {@link EncodingType}
   * @return the decoded bytearray
   * @throws EncodingNotSupportedException if the encodingType is not supported
   * @throws IllegalArgumentException if the encodingType is null
   */
  public static final byte[] decodeCheck(final String input, EncodingType encodingType)
      throws EncodingNotSupportedException {
    if (encodingType == null) {
      throw new IllegalArgumentException("encodingType mustn't be null");
    }
    switch (encodingType) {
      case BASE58:
        return decodeBase58Check(input);
      case BASE64:
        return decodeBase64Check(input);
      default:
        throw new EncodingNotSupportedException(
            String.format("Encoding %s is currently not supported", encodingType));
    }
  }

  /**
   * @param input the input to decode
   * @param allowedIdentifiers the apiIdentifiers allowed (if null or empty all known identifiers
   *     are allowed)
   * @return the decoded bytearray
   */
  public static final byte[] decodeCheckAndTag(
      final String input, final List<String> allowedIdentifiers) {
    byte[] tag = determineSerializationTag(input, allowedIdentifiers);
    byte[] decoded = EncodingUtils.decodeCheckWithIdentifier(input);
    return ByteUtils.concatenate(tag, decoded);
  }

  /**
   * @param input the input to decode
   * @return the decoded bytearray
   */
  public static final byte[] decodeCheckAndTag(final String input) {
    return decodeCheckAndTag(input, null);
  }

  private byte[] determineSerializationTag(
      final String input, final List<String> allowedIdentifiers) {
    String[] splitted = input.split("_");
    if (splitted.length != 2) {
      throw new IllegalArgumentException("input has wrong format");
    }
    String identifier = splitted[0];
    if (allowedIdentifiers != null && !allowedIdentifiers.isEmpty()) {
      if (!allowedIdentifiers.contains(identifier)) {
        throw new IllegalArgumentException("illegal identifier: " + identifier);
      }
    }
    int tag;
    switch (identifier) {
      case ApiIdentifiers.ACCOUNT_PUBKEY:
        tag = SerializationTags.ID_TAG_ACCOUNT;
        break;
      case ApiIdentifiers.NAME:
        tag = SerializationTags.ID_TAG_NAME;
        break;
      case ApiIdentifiers.COMMITMENT:
        tag = SerializationTags.ID_TAG_COMMITMENT;
        break;
      case ApiIdentifiers.ORACLE_PUBKEY:
        tag = SerializationTags.ID_TAG_ORACLE;
        break;
      case ApiIdentifiers.CONTRACT_PUBKEY:
        tag = SerializationTags.ID_TAG_CONTRACT;
        break;
      case ApiIdentifiers.CHANNEL:
        tag = SerializationTags.ID_TAG_CHANNEL;
        break;
      default:
        throw new IllegalArgumentException("unknown identifier: " + identifier);
    }
    return BigInteger.valueOf(tag).toByteArray();
  }

  private static final String encodeBase58Check(final byte[] input) {
    byte[] checksum = Arrays.copyOfRange(Sha256Hash.hashTwice(input), 0, 4);
    byte[] base58checksum = ByteUtils.concatenate(input, checksum);
    return Base58.encode(base58checksum);
  }

  private static final byte[] decodeBase58Check(final String base58encoded) {
    return Base58.decodeChecked(base58encoded);
  }

  private static final String encodeBase64Check(byte[] input) {
    byte[] checksum = Arrays.copyOfRange(Sha256Hash.hashTwice(input), 0, 4);
    byte[] base64checksum = ByteUtils.concatenate(input, checksum);
    return new String(Base64.encode(base64checksum));
  }

  private static final byte[] decodeBase64Check(String base64encoded) {
    byte[] decoded = Base64.decode(base64encoded);
    if (decoded.length < 4) throw new AddressFormatException("Input too short");
    byte[] data = Arrays.copyOfRange(decoded, 0, decoded.length - 4);
    byte[] checksum = Arrays.copyOfRange(decoded, decoded.length - 4, decoded.length);
    byte[] actualChecksum = Arrays.copyOfRange(Sha256Hash.hashTwice(data), 0, 4);
    if (!Arrays.equals(checksum, actualChecksum))
      throw new AddressFormatException("Checksum does not validate");
    return data;
  }

  /**
   * check if the given address has the correct length
   *
   * @param address base58 encoded aeternity address (ak_...)
   * @return true if the address is valid <br>
   *     false if the address is invalid
   */
  public static final boolean isAddressValid(final String address) {
    boolean isValid;
    try {
      isValid =
          decodeBase58Check(assertedType(address, ApiIdentifiers.ACCOUNT_PUBKEY)).length == 32;
    } catch (Exception e) {
      isValid = false;
    }
    return isValid;
  }

  /**
   * @param base58CheckAddress base58 encoded aeternity address (ak_...)
   * @return the readable public key as hex
   */
  public static final String addressToHex(final String base58CheckAddress) {
    return PREFIX_ZERO_X
        + Hex.toHexString(
            decodeBase58Check(assertedType(base58CheckAddress, ApiIdentifiers.ACCOUNT_PUBKEY)));
  }

  private static final String assertedType(final String data, final String type) {
    if (data.matches("^" + type + "_.+$")) {
      return data.split("_")[1];
    } else {
      throw new IllegalArgumentException("Data doesn't match expected type " + type);
    }
  }

  public static String hashEncode(final byte[] input, final String identifier) {
    byte[] hash = hash(input);
    return EncodingUtils.encodeCheck(hash, identifier);
  }

  public static byte[] hash(final byte[] input) {
    Blake2bDigest digest = new Blake2bDigest(256);
    digest.update(input, 0, input.length);
    byte[] hash = new byte[digest.getDigestSize()];
    digest.doFinal(hash, 0);
    return hash;
  }

  public static BaseKeyPair createBaseKeyPair(final RawKeyPair rawKeyPair) {
    String privateKey =
        Hex.toHexString(rawKeyPair.getPrivateKey()) + Hex.toHexString(rawKeyPair.getPublicKey());
    String publicKey = encodeCheck(rawKeyPair.getPublicKey(), ApiIdentifiers.ACCOUNT_PUBKEY);
    return BaseKeyPair.builder().privateKey(privateKey).publicKey(publicKey).build();
  }

  public static String generateCommitmentHash(final String name, final BigInteger salt) {
    return encodeCheck(
        hash(ByteUtils.concatenate(name.getBytes(), ByteUtils.leftPad(32, salt.toByteArray()))),
        ApiIdentifiers.COMMITMENT);
  }

  /**
   * @param senderId senders public key
   * @param nonce senders nonce
   * @param oracleId oracleId
   * @return queryId
   */
  public static String queryId(String senderId, BigInteger nonce, String oracleId) {
    return hashEncode(
        ByteUtils.concatenate(
            decodeCheckWithIdentifier(senderId),
            ByteUtils.leftPad(32, nonce.toByteArray()),
            decodeCheckWithIdentifier(oracleId)),
        ApiIdentifiers.ORACLE_QUERY_ID);
  }
}
