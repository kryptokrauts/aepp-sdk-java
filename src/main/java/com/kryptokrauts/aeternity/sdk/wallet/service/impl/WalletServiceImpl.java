package com.kryptokrauts.aeternity.sdk.wallet.service.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.UUID;

import org.abstractj.kalium.crypto.SecretBox;
import org.spongycastle.util.encoders.Hex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.Argon2Configuration;
import com.kryptokrauts.aeternity.sdk.domain.Keystore;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.wallet.service.WalletService;

import de.mkammerer.argon2.Argon2Advanced;
import de.mkammerer.argon2.Argon2Factory;

public class WalletServiceImpl implements WalletService
{
    @Override
    public String generateWalletFile( RawKeyPair rawKeyPair, String walletPassword, String walletName ) throws AException
    {
        // create derived key with Argon2
        Argon2Advanced argon2Advanced = Argon2Factory.createAdvanced( Argon2Factory.Argon2Types.ARGON2id );
        byte[] salt = CryptoUtils.generateSalt( Argon2Configuration.SALT_HEX_SIZE );

        // generate hash from password
        byte[] rawHash = argon2Advanced.rawHash( Argon2Configuration.OPSLIMIT, Argon2Configuration.MEMLIMIT_KIB, Argon2Configuration.PARALLELISM,
                                                 walletPassword.toCharArray(), Charset.forName( "UTF-8" ), salt );

        // initialize secretBox with derived key
        SecretBox secretBox = new SecretBox( rawHash );

        // create nonce
        byte[] nonce = CryptoUtils.generateSalt( Argon2Configuration.NONCE_HEX_SIZE );

        // chain public and private key byte arrays
        byte[] privateAndPublicKey = new byte[rawKeyPair.getPrivateKey().length + rawKeyPair.getPublicKey().length];
        System.arraycopy( rawKeyPair.getPrivateKey(), 0, privateAndPublicKey, 0, rawKeyPair.getPrivateKey().length );
        System.arraycopy( rawKeyPair.getPublicKey(), 0, privateAndPublicKey, rawKeyPair.getPrivateKey().length, rawKeyPair.getPublicKey().length );

        // encrypt the key arrays with nonce and derived key
        byte[] ciphertext = secretBox.encrypt( nonce, privateAndPublicKey );

        // generate walletName if not given
        if ( walletName == null || walletName.trim().length() == 0 )
        {
            walletName = "generated wallet file -" + new Timestamp( System.currentTimeMillis() );
        }

        // generate the domain object for keystore
        Keystore wallet = Keystore.builder().publicKey( getWalletAddress( rawKeyPair ) )
        .crypto( Keystore.Crypto.builder().secretType( Argon2Configuration.SECRET_TYPE ).symmetricAlgorithm( Argon2Configuration.SYMMETRIC_ALGORITHM )
        .cipherText( Hex.toHexString( ciphertext ) ).cipherParams( Keystore.CipherParams.builder().nonce( Hex.toHexString( nonce ) ).build() )
        .kdf( Argon2Configuration.argon2Mode )
        .kdfParams( Keystore.KdfParams.builder().memLimitKib( Argon2Configuration.MEMLIMIT_KIB ).opsLimit( Argon2Configuration.OPSLIMIT )
        .salt( Hex.toHexString( salt ) ).parallelism( 1 ).build() ).build() ).id( UUID.randomUUID().toString() ).name( walletName )
        .version( Argon2Configuration.VERSION ).build();

        try
        {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString( wallet );
        }
        catch ( JsonProcessingException e )
        {
            throw new AException( "Error creating wallet-json", e );
        }
    }

    @Override
    public byte[] recoverPrivateKeyFromWalletFile( String json, String walletPassword ) throws AException
    {
        try
        {
            Keystore recoverWallet = new ObjectMapper().readValue( json, Keystore.class );
            Argon2Advanced argon2Advanced = Argon2Factory.createAdvanced( Argon2Factory.Argon2Types.ARGON2id );
            // extract salt
            byte[] salt = Hex.decode( recoverWallet.getCrypto().getKdfParams().getSalt() );
            // generate hash from password
            byte[] rawHash = argon2Advanced.rawHash( Argon2Configuration.OPSLIMIT, Argon2Configuration.MEMLIMIT_KIB, Argon2Configuration.PARALLELISM,
                                                     walletPassword.toCharArray(), Charset.forName( "UTF-8" ), salt );
            // initialize secretBox with derived key
            SecretBox secretBox = new SecretBox( rawHash );

            // extract nonce
            byte[] nonce = Hex.decode( recoverWallet.getCrypto().getCipherParams().getNonce() );

            // extract cipertext
            byte[] ciphertext = Hex.decode( recoverWallet.getCrypto().getCipherText() );
            // recover private key
            byte[] decrypted = secretBox.decrypt( nonce, ciphertext );

            return decrypted;
        }
        catch ( IOException e )
        {
            throw new AException( "Error recovering wallet-json", e );
        }
    }

    @Override
    public String getWalletAddress( RawKeyPair rawKeyPair )
    {
        return ApiIdentifiers.ACCOUNT_PUBKEY + "_" + EncodingUtils.encodeCheck( rawKeyPair.getPublicKey(), EncodingType.BASE58 );
    }

}
