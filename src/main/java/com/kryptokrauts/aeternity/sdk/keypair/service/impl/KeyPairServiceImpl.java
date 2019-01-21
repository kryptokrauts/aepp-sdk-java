package com.kryptokrauts.aeternity.sdk.keypair.service.impl;

import static com.kryptokrauts.aeternity.sdk.constants.BaseConstants.CIPHER_ALGORITHM;
import static com.kryptokrauts.aeternity.sdk.constants.BaseConstants.SECRET_KEY_SPEC;
import static com.kryptokrauts.aeternity.sdk.util.CryptoUtils.leftPad;
import static com.kryptokrauts.aeternity.sdk.util.CryptoUtils.rightPad;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.keypair.service.KeyPairService;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

public final class KeyPairServiceImpl implements KeyPairService {

	@Override
	public BaseKeyPair generateBaseKeyPair() {
		RawKeyPair rawKeyPair = generateKeyPairInternal();
		byte[] publicKey = rawKeyPair.getPublicKey();
		byte[] privateKey = rawKeyPair.getPrivateKey();

		String aePublicKey = ApiIdentifiers.ACCOUNT_PUBKEY + "_"
				+ EncodingUtils.encodeCheck(publicKey, EncodingType.BASE58);
		String privateKeyHex = Hex.toHexString(privateKey) + Hex.toHexString(publicKey);

		return BaseKeyPair.builder().publicKey(aePublicKey).privateKey(privateKeyHex).build();
	}

	@Override
	public RawKeyPair generateRawKeyPair() {
		RawKeyPair rawKeyPair = generateKeyPairInternal();
		byte[] publicKey = rawKeyPair.getPublicKey();
		byte[] privateKey = rawKeyPair.getPrivateKey();

		byte[] privateAndPublicKey = new byte[privateKey.length + publicKey.length];
		System.arraycopy(privateKey, 0, privateAndPublicKey, 0, privateKey.length);
		System.arraycopy(publicKey, 0, privateAndPublicKey, privateKey.length, publicKey.length);

		return RawKeyPair.builder().publicKey(publicKey).privateKey(privateAndPublicKey).build();
	}

	/**
	 * the actual keypair generation method
	 *
	 * @return the raw byte arrays for private and public key
	 */
	private RawKeyPair generateKeyPairInternal() {
		Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
		keyPairGenerator.init(new Ed25519KeyGenerationParameters(CryptoUtils.getSecureRandom()));
		AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
		Ed25519PublicKeyParameters publicKeyParams = (Ed25519PublicKeyParameters) asymmetricCipherKeyPair.getPublic();
		Ed25519PrivateKeyParameters privateKeyParams = (Ed25519PrivateKeyParameters) asymmetricCipherKeyPair
				.getPrivate();

		byte[] publicKey = publicKeyParams.getEncoded();
		byte[] privateKey = privateKeyParams.getEncoded();
		return RawKeyPair.builder().publicKey(publicKey).privateKey(privateKey).build();
	}

	@Override
	public AsymmetricCipherKeyPair generateKeyPairFromSecret(final String privateKey) {
		final String privateKey32;
		if (privateKey.length() == 128) {
			privateKey32 = privateKey.substring(0, 64);
		} else {
			privateKey32 = privateKey;
		}
		Ed25519PrivateKeyParameters privateKeyParams = new Ed25519PrivateKeyParameters(Hex.decode(privateKey32), 0);
		Ed25519PublicKeyParameters publicKeyParams = privateKeyParams.generatePublicKey();
		AsymmetricCipherKeyPair asymmetricCipherKeyPair = new AsymmetricCipherKeyPair(publicKeyParams,
				privateKeyParams);
		return asymmetricCipherKeyPair;
	}

	@Override
	public final byte[] encryptPrivateKey(final String password, final byte[] binaryKey) throws NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		return encryptKey(password, leftPad(64, binaryKey));
	}

	@Override
	public final byte[] encryptPublicKey(final String password, final byte[] binaryKey) throws NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		return encryptKey(password, rightPad(32, binaryKey));
	}

	@Override
	public final byte[] decryptPrivateKey(final String password, final byte[] encryptedBinaryKey)
			throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, InvalidKeyException {
		return decryptKey(password, encryptedBinaryKey);
	}

	@Override
	public final byte[] decryptPublicKey(final String password, final byte[] encryptedBinaryKey)
			throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
			InvalidKeyException {
		return Arrays.copyOfRange(decryptKey(password, encryptedBinaryKey), 0, 32);
	}

	@Override
	public RawKeyPair encryptRawKeyPair(final RawKeyPair keyPairRaw, final String password)
			throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException {
		byte[] encryptedPublicKey = encryptPublicKey(password, keyPairRaw.getPublicKey());
		byte[] encryptedPrivateKey = encryptPrivateKey(password, keyPairRaw.getPrivateKey());
		return RawKeyPair.builder().publicKey(encryptedPublicKey).privateKey(encryptedPrivateKey).build();
	}

	private final byte[] encryptKey(final String password, final byte[] binaryData) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] hashedPassword = Sha256Hash.hash(password.getBytes());
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		SecretKey secretKey = new SecretKeySpec(hashedPassword, SECRET_KEY_SPEC);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(binaryData);
	}

	private final byte[] decryptKey(final String password, final byte[] encryptedBinaryData)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException {
		byte[] hashedPassword = Sha256Hash.hash(password.getBytes());
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		SecretKey secretKey = new SecretKeySpec(hashedPassword, SECRET_KEY_SPEC);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(encryptedBinaryData);
	}

}
