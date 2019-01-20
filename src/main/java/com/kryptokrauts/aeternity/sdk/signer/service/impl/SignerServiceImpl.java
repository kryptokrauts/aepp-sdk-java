package com.kryptokrauts.aeternity.sdk.signer.service.impl;

import com.kryptokrauts.aeternity.sdk.signer.service.SignerService;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

import static com.kryptokrauts.aeternity.sdk.constants.BaseConstants.AETERNITY_MESSAGE_PREFIX;
import static com.kryptokrauts.aeternity.sdk.constants.BaseConstants.MAX_MESSAGE_LENGTH;
import static com.kryptokrauts.aeternity.sdk.util.CryptoUtils.privateKeyCipherParamsFromHex;
import static com.kryptokrauts.aeternity.sdk.util.CryptoUtils.publicKeyCipherParamsFromHex;

public class SignerServiceImpl implements SignerService {

    @Override
    public byte[] sign(final String data, final String privateKey) throws CryptoException {
        return sign(Hex.decode(data), privateKey);
    }

    @Override
    public byte[] sign(final byte[] data, final String privateKey) throws CryptoException {
        Signer signer = new Ed25519Signer();
        signer.init(true, privateKeyCipherParamsFromHex(privateKey));
        signer.update(data, 0, data.length);
        return signer.generateSignature();
    }

    @Override
    public byte[] signPersonalMessage(final String message, final String privateKey) throws CryptoException {
        return sign(personalMessageToBinary(message), privateKey);
    }

    @Override
    public boolean verify(final String data, final byte[] signature, final String publicKey) {
        byte[] dataBinary = Hex.decode(data);
        return verify(dataBinary, signature, publicKey);
    }

    @Override
    public final boolean verify(final byte[] data, final byte[] signature, final String publicKey) {
        Signer verifier = new Ed25519Signer();
        verifier.init(false, publicKeyCipherParamsFromHex(publicKey));
        verifier.update(data, 0, data.length);
        return verifier.verifySignature(signature);
    }

    @Override
    public boolean verifyPersonalMessage(final String message, final byte[] signature, final String publicKey) {
        return verify(personalMessageToBinary(message), signature, publicKey);
    }

    private byte[] personalMessageToBinary(final String message) {
        final byte[] p = AETERNITY_MESSAGE_PREFIX.getBytes(StandardCharsets.UTF_8);
        final byte[] msg = message.getBytes(StandardCharsets.UTF_8);
        if (msg.length > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException(String
                    .format("Message exceeds allow maximum size %s", MAX_MESSAGE_LENGTH));
        }
        final byte[] pLength = {(byte) p.length};
        final byte[] msgLength = {(byte) msg.length};
        return ByteUtils.concatenate(pLength, p, msgLength, msg);
    }
}
