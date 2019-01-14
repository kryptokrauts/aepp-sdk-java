package com.kryptokrauts.aeternity.sdk.util;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.exception.EncodingNotSupportedException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

import static com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers.IDENTIFIERS_B58_LIST;
import static com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers.IDENTIFIERS_B64_LIST;
import static com.kryptokrauts.aeternity.sdk.constants.BaseConstants.PREFIX_ZERO_X;
import static com.kryptokrauts.aeternity.sdk.util.EncodingType.BASE58;
import static com.kryptokrauts.aeternity.sdk.util.EncodingType.BASE64;

/**
 * this util class provides all encoding related methods
 */
public final class EncodingUtils {

    /**
     * encode input with encoding determined from given identifier String
     *
     * @param input
     * @param identifier see @{@link ApiIdentifiers}
     * @return
     * @throws EncodingNotSupportedException
     */
    public static final String encodeCheck(final byte[] input, String identifier) throws EncodingNotSupportedException {
        if (identifier != null && identifier.trim().length() > 0) {
            //determine encoding from given identifier
            if (IDENTIFIERS_B58_LIST.contains(identifier)) {
                return encodeCheck(input, BASE58);
            } else if (IDENTIFIERS_B64_LIST.contains(identifier)) {
                return encodeCheck(input, BASE64);
            }
        }
        // if we get to this point, we cannot determine the encoding
        throw new EncodingNotSupportedException(String
                .format("Cannot determine encoding type from given identifier %s", identifier));
    }

    /**
     * encode input with given encodingType
     *
     * @param input
     * @param encodingType
     * @return
     * @throws EncodingNotSupportedException
     */
    public static final String encodeCheck(final byte[] input, EncodingType encodingType) throws EncodingNotSupportedException {
        switch (encodingType) {
            case BASE58:
                return encodeBase58Check(input);
            case BASE64:
                return encodeBase64Check(input);
            default:
                throw new EncodingNotSupportedException(String
                        .format("Encoding %s is currently not supported", encodingType));
        }
    }

    /**
     * decode input with encoding determined from given identifier String
     *
     * @param input
     * @param identifier see @{@link ApiIdentifiers}
     * @return
     * @throws EncodingNotSupportedException
     */
    public static final byte[] decodeCheck(final String input, String identifier) throws EncodingNotSupportedException {
        if (identifier != null && identifier.trim().length() > 0) {
            //determine encoding from given identifier
            if (IDENTIFIERS_B58_LIST.contains(identifier)) {
                return decodeCheck(input, BASE58);
            } else if (IDENTIFIERS_B64_LIST.contains(identifier)) {
                return decodeCheck(input, BASE64);
            }
        }
        // if we get to this point, we cannot determine the encoding
        throw new EncodingNotSupportedException(String
                .format("Cannot determine encoding type from given identifier %s", identifier));
    }

    /**
     * decode input with given encodingType
     *
     * @param input
     * @param encodingType
     * @return
     * @throws EncodingNotSupportedException
     */
    public static final byte[] decodeCheck(final String input, EncodingType encodingType) throws EncodingNotSupportedException {
        switch (encodingType) {
            case BASE58:
                return decodeBase58Check(input);
            case BASE64:
                return decodeBase58Check(input);
            default:
                throw new EncodingNotSupportedException(String
                        .format("Encoding %s is currently not supported", encodingType));
        }
    }

    private static final String encodeBase58Check(final byte[] input) {
        byte[] checksum = Arrays.copyOfRange(Sha256Hash.hashTwice(input), 0, 4);
        byte[] base58checksum = new byte[input.length + checksum.length];
        System.arraycopy(input, 0, base58checksum, 0, input.length);
        System.arraycopy(checksum, 0, base58checksum, input.length, checksum.length);
        return Base58.encode(base58checksum);
    }

    private static final byte[] decodeBase58Check(final String base58encoded) {
        return Base58.decodeChecked(base58encoded);
    }

    private static final String encodeBase64Check(byte[] input) {
        byte[] checksum = Arrays.copyOfRange(Sha256Hash.hashTwice(input), 0, 4);
        byte[] base64checksum = new byte[input.length + checksum.length];
        System.arraycopy(input, 0, base64checksum, 0, input.length);
        System.arraycopy(checksum, 0, base64checksum, input.length, checksum.length);
        return new String(Base64.encode(base64checksum));
    }

    private static final byte[] decodeBase64Check(String base64encoded) {
        // TODO decode base64 with checksum
        return new byte[0];
    }

    public static final boolean isAddressValid(final String address) {
        boolean isValid;
        try {
            isValid = decodeBase58Check(assertedType(address, ApiIdentifiers.ACCOUNT_PUBKEY)).length == 32;
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }

    public static final String addressToHex(final String base58CheckAddress) {
        return PREFIX_ZERO_X + Hex
                .toHexString(decodeBase58Check(assertedType(base58CheckAddress, ApiIdentifiers.ACCOUNT_PUBKEY)));
    }

    private static final String assertedType(final String data, final String type) {
        if (data.matches("^" + type + "_.+$")) {
            return data.split("_")[1];
        } else {
            throw new IllegalArgumentException("Data doesn't match expected type " + type);
        }
    }
}
