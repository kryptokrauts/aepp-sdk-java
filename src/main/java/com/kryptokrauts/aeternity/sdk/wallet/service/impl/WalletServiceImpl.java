package com.kryptokrauts.aeternity.sdk.wallet.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternity.sdk.constants.Argon2Configuration;
import com.kryptokrauts.aeternity.sdk.domain.Keystore;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.wallet.service.WalletService;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.abstractj.kalium.NaCl;
import org.abstractj.kalium.crypto.Random;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public class WalletServiceImpl implements WalletService {
    // TODO generate wallet-file
    // TODO recover wallet-file




    /*public static void main(String[] args) throws Exception {
        //1 Schlüsselpaar aus privateKey generieren
        AsymmetricCipherKeyPair as = KeyPairServiceImpl.getInstance()
                                                       .generateKeyPairFromSecret("706c8b80b9a0dc73fa3a446082d523502704819f29c7e0597a889b799f3a2361");

        Ed25519PublicKeyParameters pub = (Ed25519PublicKeyParameters) as.getPublic();
        Ed25519PrivateKeyParameters privateKeyParams = (Ed25519PrivateKeyParameters) as.getPrivate();

        System.out.println("Wallet-Adresse (PublicKey): ak_" + KeyPairServiceImpl.getInstance()
                                                                                 .encodeBase58Check(pub.getEncoded()));

        //2 mit Argon2 derivedKey erzeugen
        Argon2Advanced argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id);
        String salt = "a84887d9539cbd9490d2b1a5c41262b2";

        byte[] rawHash = argon2
                .rawHash(2, 65536, 1, "aeternity".toCharArray(), Charset.forName("UTF-8"), Hex.decode(salt));

        //3 mit derivedKey secretBox initialisieren
        SecretBox secretBox = new SecretBox(rawHash);

        //4 Nonce erzeugen
        byte[] nonce = Hex.decode("375673a887fd10910fe3bfa9c9abfd72d3d240d124ed3f3b");

        //5 private und public key byte arrays aneinanderhängen
        byte[] privateAndPublicKey = new byte[privateKeyParams.getEncoded().length + pub.getEncoded().length];
        System.arraycopy(privateKeyParams.getEncoded(), 0, privateAndPublicKey, 0, privateKeyParams
                .getEncoded().length);
        System.arraycopy(pub.getEncoded(), 0, privateAndPublicKey, privateKeyParams.getEncoded().length, pub
                .getEncoded().length);

        //6 encrypt mit nonce und key ausführen
        byte[] ciphertext = secretBox
                .encrypt(nonce, privateAndPublicKey);

        System.out.println("cipher: " + Hex.toHexString(ciphertext));

        //decrypten
        byte[] dec = secretBox.decrypt(nonce, Hex
                .decode("71acf8412331806b3ad5482cda1f6e682c541c522f61715056faf5ed2d21a9c4d68fe2cdcf147b1be99fbab20b33433f82b2a2d3bbc772957bd2fb6cf9a97f611670e0f044d8076efbe31fe30142e6f5"));

        System.out.println(Hex.toHexString(dec));
        System.out.println(KeyPairServiceImpl.getInstance()
                                             .encodeBase58Check(Hex
                                                     .decode("df827a01b927f687419be62597c6ea6dd5e9c133d9aeb7c691d99e69028a6de0")
                                             ));
    }*/

    @Override
    public String generateWalletJSON(BaseKeyPair baseKeyPair, String password, String walletName) throws AException {

        //Salt
        byte[] salt = new Random().randomBytes(NaCl.Sodium.CRYPTO_BOX_CURVE25519XSALSA20POLY1305_BOXZEROBYTES);
        int memLimit = NaCl.Sodium.CRYPTO_PWHASH_SCRYPTSALSA208SHA256_MEMLIMIT_INTERACTIVE;
        int opsLimit = NaCl.Sodium.CRYPTO_PWHASH_SCRYPTSALSA208SHA256_OPSLIMIT_INTERACTIVE;

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String derivedKey = argon2.hash(10, 65536, 1, password);

        //Argon2 configuration
        Argon2Parameters argon2Parameters = new Argon2Parameters.Builder(Argon2Configuration.argon2Parameter)
                .withVersion(Argon2Configuration.VERSION).withMemoryAsKB(Argon2Configuration.MEMLIMIT_KIB)
                .withParallelism(Argon2Configuration.PARALLELISM).withIterations(Argon2Configuration.OPSLIMIT).build();

        Argon2BytesGenerator argon2BytesGenerator = new Argon2BytesGenerator();
        argon2BytesGenerator.init(argon2Parameters);


        Keystore wallet = Keystore.builder()
                                  .publicKey(baseKeyPair.getPublicKey())
                                  .crypto(Keystore.Crypto.builder().secretType("ed25519")
                                                         .symmetricAlgorithm("xsalsa20-poly1305")
                                                         .cipherText("71acf8412331806b3ad5482cda1f6e682c541c522f61715056faf5ed2d21a9c4d68fe2cdcf147b1be99fbab20b33433f82b2a2d3bbc772957bd2fb6cf9a97f611670e0f044d8076efbe31fe30142e6f5")
                                                         .cipherParams(Keystore.CipherParams.builder()
                                                                                            .nonce("375673a887fd10910fe3bfa9c9abfd72d3d240d124ed3f3b")
                                                                                            .build())
                                                         .kdf("argon2id")
                                                         .kdfParams(Keystore.KdfParams.builder()
                                                                                      .memLimitKib(65536)
                                                                                      .opsLimit(2)
                                                                                      .salt("a84887d9539cbd9490d2b1a5c41262b2")
                                                                                      .parallelism(1).build())
                                                         .build())
                                  .id("ff7d9f9c-e0ab-4fab-b8fc-beab2d322f6b")
                                  .name("!!!FOR TESTING PURPOSE ONLY!!! - Wed 14 Nov 2018 09:40:10 CET - password:aeternity")
                                  .version(1).build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(wallet);
        } catch (JsonProcessingException e) {
            throw new AException("Error occured generating json of domain object", e);
        }
    }


}
