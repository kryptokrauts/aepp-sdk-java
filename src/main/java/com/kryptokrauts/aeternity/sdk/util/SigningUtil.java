package com.kryptokrauts.aeternity.sdk.util;

import static com.kryptokrauts.aeternity.sdk.util.CryptoUtils.privateKeyCipherParamsFromHex;
import static com.kryptokrauts.aeternity.sdk.util.CryptoUtils.publicKeyCipherParamsFromHex;

import java.nio.charset.StandardCharsets;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.util.encoders.Hex;

import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SigningUtil {

    public static final byte[] sign( final String data, final String privateKey ) throws CryptoException {
        return sign( Hex.decode( data ), privateKey );
    }

    public static final byte[] sign( final byte[] data, final String privateKey ) throws CryptoException {
        Signer signer = new Ed25519Signer();
        signer.init( true, privateKeyCipherParamsFromHex( privateKey ) );
        signer.update( data, 0, data.length );
        return signer.generateSignature();
    }

    public static final byte[] signPersonalMessage( final String message, final String privateKey ) throws CryptoException {
        return sign( personalMessageToBinary( message ), privateKey );
    }

    public static final boolean verify( final String data, final byte[] signature, final String publicKey ) {
        byte[] dataBinary = Hex.decode( data );
        return verify( dataBinary, signature, publicKey );
    }

    public static final boolean verify( final byte[] data, final byte[] signature, final String publicKey ) {
        Signer verifier = new Ed25519Signer();
        verifier.init( false, publicKeyCipherParamsFromHex( publicKey ) );
        verifier.update( data, 0, data.length );
        return verifier.verifySignature( signature );
    }

    public static final boolean verifyPersonalMessage( final String message, final byte[] signature, final String publicKey ) {
        return verify( personalMessageToBinary( message ), signature, publicKey );
    }

    private static final byte[] personalMessageToBinary( final String message ) {
        final byte[] p = BaseConstants.AETERNITY_MESSAGE_PREFIX.getBytes( StandardCharsets.UTF_8 );
        final byte[] msg = message.getBytes( StandardCharsets.UTF_8 );
        if ( msg.length > BaseConstants.MAX_MESSAGE_LENGTH ) {
            throw new IllegalArgumentException( String.format( "Message exceeds allow maximum size %s", BaseConstants.MAX_MESSAGE_LENGTH ) );
        }
        final byte[] pLength = {(byte) p.length };
        final byte[] msgLength = {(byte) msg.length };
        return ByteUtils.concatenate( pLength, p, msgLength, msg );
    }
}
