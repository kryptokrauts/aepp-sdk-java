package com.kryptokrauts.aeternity.sdk.util;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;

/**
 * this util class provides common methods
 */
public final class CryptoUtils {

    /**
     * add leading zeros to given byte array
     *
     * @param length
     * @param data
     * @return
     */
    public static final byte[] leftPad(final int length, final byte[] data) {
        int fill = length - data.length;
        if (fill > 0) {
            byte[] fillArray = new byte[fill];
            byte[] leftPadded = new byte[fillArray.length + data.length];
            System.arraycopy(fillArray, 0, leftPadded, 0, fillArray.length);
            System.arraycopy(data, 0, leftPadded, fillArray.length, data.length);
            return leftPadded;
        }
        return data;
    }

    /**
     * add trailing zeros to given byte array
     *
     * @param length
     * @param data
     * @return
     */
    public static final byte[] rightPad(final int length, final byte[] data) {
        int fill = length - data.length;
        if (fill > 0) {
            byte[] fillArray = new byte[fill];
            byte[] rightPadded = new byte[data.length + fillArray.length];
            System.arraycopy(data, 0, rightPadded, 0, data.length);
            System.arraycopy(fillArray, 0, rightPadded, data.length, fillArray.length);
            return rightPadded;
        }
        return data;
    }

    /**
     * Extract CipherParameters from given privateKey
     *
     * @param privateKey
     * @return
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
     * @param publicKey
     * @return
     */
    public static CipherParameters publicKeyCipherParamsFromHex(final String publicKey) {
        return new Ed25519PublicKeyParameters(Hex.decode(publicKey), 0);
    }

}
