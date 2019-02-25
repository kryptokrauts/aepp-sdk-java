package com.kryptokrauts.aeternity.sdk.service.wallet.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bouncycastle.util.encoders.Hex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.domain.Keystore;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.wallet.WalletService;
import com.kryptokrauts.aeternity.sdk.service.wallet.WalletServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import de.mkammerer.argon2.Argon2Advanced;
import de.mkammerer.argon2.Argon2Factory;
import lombok.RequiredArgsConstructor;
import net.consensys.cava.crypto.sodium.SecretBox;

@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

	@Nonnull
	private WalletServiceConfiguration config;

	@Override
	public String createHDKeystore(MnemonicKeyPair mnemonicKeyPair) throws AException {
		Map<String, String> keystore = new HashMap<String, String>();
		keystore.put("publicKey", getPublicKey(mnemonicKeyPair));
		keystore.put("mnemonicSeedWords", String.join(" ", mnemonicKeyPair.getMnemonicSeedWords()));
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(keystore);
		} catch (JsonProcessingException e) {
			throw new AException("Error creating wallet-json", e);
		}
	}

	@Override
	public String generateKeystore(RawKeyPair rawKeyPair, String walletPassword, String walletName) throws AException {
		// create derived key with Argon2
		Argon2Advanced argon2Advanced = Argon2Factory.createAdvanced(config.getArgon2Type());
		byte[] salt = CryptoUtils.generateSalt(config.getDefaultSaltLength());

		// generate hash from password
		byte[] rawHash = argon2Advanced.rawHash(config.getOpsLimit(), config.getMemlimitKIB(), config.getParallelism(),
				walletPassword.toCharArray(), StandardCharsets.UTF_8, salt);

		// chain public and private key byte arrays
		byte[] privateAndPublicKey = new byte[rawKeyPair.getPrivateKey().length + rawKeyPair.getPublicKey().length];
		System.arraycopy(rawKeyPair.getPrivateKey(), 0, privateAndPublicKey, 0, rawKeyPair.getPrivateKey().length);
		System.arraycopy(rawKeyPair.getPublicKey(), 0, privateAndPublicKey, rawKeyPair.getPrivateKey().length,
				rawKeyPair.getPublicKey().length);

		// encrypt the key arrays with nonce and derived key
		SecretBox.Nonce nonce = SecretBox.Nonce.random();
		byte[] ciphertext = SecretBox.encrypt(privateAndPublicKey, SecretBox.Key.fromBytes(rawHash), nonce);

		// generate walletName if not given
		if (walletName == null || walletName.trim().length() == 0) {
			walletName = "generated wallet file -" + new Timestamp(System.currentTimeMillis());
		}

		// generate the domain object for keystore
		Keystore wallet = Keystore.builder().publicKey(getPublicKey(rawKeyPair)).crypto(Keystore.Crypto.builder()
				.secretType(config.getSecretType()).symmetricAlgorithm(config.getSymmetricAlgorithm())
				.cipherText(Hex.toHexString(ciphertext))
				.cipherParams(Keystore.CipherParams.builder().nonce(Hex.toHexString(nonce.bytesArray())).build())
				.kdf(config.getArgon2Mode())
				.kdfParams(
						Keystore.KdfParams.builder().memLimitKib(config.getMemlimitKIB()).opsLimit(config.getOpsLimit())
								.salt(Hex.toHexString(salt)).parallelism(config.getParallelism()).build())
				.build()).id(UUID.randomUUID().toString()).name(walletName).version(config.getVersion()).build();

		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(wallet);
		} catch (JsonProcessingException e) {
			throw new AException("Error creating wallet-json", e);
		}
	}

	@Override
	public byte[] recoverPrivateKeyFromKeystore(String json, String walletPassword) throws AException {
		try {
			Keystore recoverWallet = new ObjectMapper().readValue(json, Keystore.class);
			Argon2Advanced argon2Advanced = Argon2Factory.createAdvanced(config.getArgon2Type());
			// extract salt
			byte[] salt = Hex.decode(recoverWallet.getCrypto().getKdfParams().getSalt());
			// generate hash from password
			byte[] rawHash = argon2Advanced.rawHash(config.getOpsLimit(), config.getMemlimitKIB(),
					config.getParallelism(), walletPassword.toCharArray(), StandardCharsets.UTF_8, salt);
			// extract nonce
			byte[] nonce = Hex.decode(recoverWallet.getCrypto().getCipherParams().getNonce());

			// extract cipertext
			byte[] ciphertext = Hex.decode(recoverWallet.getCrypto().getCipherText());

			// recover private key
			byte[] decrypted = SecretBox.decrypt(ciphertext, SecretBox.Key.fromBytes(rawHash),
					SecretBox.Nonce.fromBytes(nonce));
			if (decrypted == null) {
				throw new AException("Error recovering privateKey: wrong password.");
			}
			return decrypted;
		} catch (IOException e) {
			throw new AException("Error recovering wallet-json", e);
		}
	}

	@Override
	public String getPublicKey(RawKeyPair rawKeyPair) {
		return EncodingUtils.encodeCheck(rawKeyPair.getPublicKey(), ApiIdentifiers.ACCOUNT_PUBKEY);
	}

}
