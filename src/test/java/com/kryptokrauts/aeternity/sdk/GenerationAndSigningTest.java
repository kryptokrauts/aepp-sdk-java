package com.kryptokrauts.aeternity.sdk;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import java.nio.charset.StandardCharsets;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

public class GenerationAndSigningTest extends BaseTest {
  {
    final KeyPairService keypairService = new KeyPairServiceFactory().getService();

    final String privateKeyAsHex =
        "4d881dd1917036cc231f9881a0db978c8899dd76a817252418606b02bf6ab9d22378f892b7cc82c2d2739e994ec9953aa36461f1eb5a4a49a5b0de17b3d23ae8";
    final String publicKeyWithPrefix = "ak_Gd6iMVsoonGuTF8LeswwDDN2NF5wYHAoTRtzwdEcfS32LWoxm";
    final byte[] publicKey = EncodingUtils.decodeCheckWithIdentifier(publicKeyWithPrefix);

    final byte[] txBinaryAsArray = {
      (byte) 248,
      76,
      12,
      1,
      (byte) 160,
      35,
      120,
      (byte) 248,
      (byte) 146,
      (byte) 183,
      (byte) 204,
      (byte) 130,
      (byte) 194,
      (byte) 210,
      115,
      (byte) 158,
      (byte) 153,
      78,
      (byte) 201,
      (byte) 149,
      58,
      (byte) 163,
      100,
      97,
      (byte) 241,
      (byte) 235,
      90,
      74,
      73,
      (byte) 165,
      (byte) 176,
      (byte) 222,
      23,
      (byte) 179,
      (byte) 210,
      58,
      (byte) 232,
      (byte) 160,
      63,
      40,
      35,
      12,
      40,
      65,
      38,
      (byte) 215,
      (byte) 218,
      (byte) 236,
      (byte) 136,
      (byte) 133,
      42,
      120,
      (byte) 160,
      (byte) 179,
      18,
      (byte) 191,
      (byte) 241,
      (byte) 162,
      (byte) 198,
      (byte) 203,
      (byte) 209,
      (byte) 173,
      89,
      (byte) 136,
      (byte) 202,
      (byte) 211,
      (byte) 158,
      59,
      12,
      122,
      1,
      1,
      1,
      (byte) 132,
      84,
      101,
      115,
      116
    };
    final byte[] signatureAsArray = {
      95,
      (byte) 146,
      31,
      37,
      95,
      (byte) 194,
      36,
      76,
      58,
      49,
      (byte) 167,
      (byte) 156,
      127,
      (byte) 131,
      (byte) 142,
      (byte) 248,
      25,
      121,
      (byte) 139,
      109,
      59,
      (byte) 243,
      (byte) 203,
      (byte) 205,
      16,
      (byte) 172,
      115,
      (byte) 143,
      (byte) 254,
      (byte) 236,
      33,
      4,
      43,
      46,
      16,
      (byte) 190,
      46,
      46,
      (byte) 140,
      (byte) 166,
      76,
      39,
      (byte) 249,
      54,
      38,
      27,
      93,
      (byte) 159,
      58,
      (byte) 148,
      67,
      (byte) 198,
      81,
      (byte) 206,
      106,
      (byte) 237,
      91,
      (byte) 131,
      27,
      14,
      (byte) 143,
      (byte) 178,
      (byte) 130,
      2
    };

    describe(
        "crypto",
        () -> {
          describe(
              "generateKeyPair",
              () -> {
                it(
                    "generates an account key pair",
                    () -> {
                      BaseKeyPair keyPair = keypairService.generateBaseKeyPair();
                      assertNotNull(keyPair);
                      assertTrue(EncodingUtils.isAddressValid(keyPair.getPublicKey()));
                      assertTrue(keyPair.getPublicKey().startsWith("ak_"));
                      int length = keyPair.getPublicKey().length();
                      assertTrue(length <= 53 && length >= 51);
                    });
              });

          describe(
              "encryptPassword",
              () -> {
                describe(
                    "generate a password encrypted key pair",
                    () -> {
                      RawKeyPair keyPair = keypairService.generateRawKeyPair();
                      final String password = "verysecret";

                      it(
                          "works for private keys",
                          () -> {
                            final byte[] privateBinary = keyPair.getConcatenatedPrivateKey();
                            final byte[] encryptedBinary =
                                keypairService.encryptPrivateKey(password, privateBinary);
                            final byte[] decryptedBinary =
                                keypairService.decryptPrivateKey(password, encryptedBinary);
                            assertArrayEquals(privateBinary, decryptedBinary);
                          });

                      it(
                          "works for public keys",
                          () -> {
                            final byte[] publicBinary = ((RawKeyPair) keyPair).getPublicKey();
                            final byte[] encryptedBinary =
                                keypairService.encryptPublicKey(password, publicBinary);
                            final byte[] decryptedBinary =
                                keypairService.decryptPublicKey(password, encryptedBinary);
                            assertArrayEquals(publicBinary, decryptedBinary);
                          });
                    });
              });

          describe(
              "encodeBase",
              () -> {
                it(
                    "can be encoded and decoded",
                    () -> {
                      final String input = "helloword010101023";
                      final byte[] inputBinary = input.getBytes(StandardCharsets.UTF_8);
                      final String encoded =
                          EncodingUtils.encodeCheck(inputBinary, EncodingType.BASE58);
                      final byte[] decodedBinary =
                          EncodingUtils.decodeCheck(encoded, EncodingType.BASE58);
                      final String decoded = new String(decodedBinary);
                      assertEquals(input, decoded);
                    });
              });

          describe(
              "recover",
              () -> {
                it(
                    "check for the correct private key for the beneficiary",
                    () -> {
                      final String beneficiaryPub =
                          "ak_twR4h7dEcUtc2iSEDv8kB7UFJJDGiEDQCXr85C3fYF8FdVdyo";
                      final BaseKeyPair keyPair =
                          keypairService.generateBaseKeyPairFromSecret(
                              "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF");
                      assertEquals(beneficiaryPub, keyPair.getPublicKey());
                    });
              });

          describe(
              "sign",
              () -> {
                it(
                    "should produce correct signature",
                    () -> {
                      final byte[] txSignature = SigningUtil.sign(txBinaryAsArray, privateKeyAsHex);
                      assertArrayEquals(txSignature, signatureAsArray);
                    });
              });

          describe(
              "verify",
              () -> {
                it(
                    "should verify tx with correct signature",
                    () -> {
                      final boolean verified =
                          SigningUtil.verify(
                              txBinaryAsArray, signatureAsArray, Hex.toHexString(publicKey));
                      assertTrue(verified);
                    });
              });

          describe(
              "personal messages",
              () -> {
                final String message = "test";
                final String messageSignatureAsHex =
                    "20f779383f3ce0ab7781b7c8ff848e6d80f7f22d5cdc266763cd74d89c5ee0716758e75f56391711957f506d4993ae7dea62bec0f2806e6de66227f52836160a";
                final byte[] messageSignature = Hex.decode(messageSignatureAsHex);

                final String messageNonASCII = "tæst";
                final String messageNonASCIISignatureAsHex =
                    "68d1344c46d9b2ef642490ffade3155c714471dd6d097fc393edc1004031a5492270de77ef8918df7857ea348d8ba4444ed1ff8e84f19e8e685e31174356fa06";
                final byte[] messageNonASCIISignature = Hex.decode(messageNonASCIISignatureAsHex);

                describe(
                    "sign",
                    () -> {
                      it(
                          "should produce correct signature of message",
                          () -> {
                            final byte[] msgSignature =
                                SigningUtil.signMessage(message, privateKeyAsHex);
                            assertArrayEquals(msgSignature, messageSignature);
                          });

                      it(
                          "should produce correct signature of message with non-ASCII chars",
                          () -> {
                            final byte[] msgSignature =
                                SigningUtil.signMessage(messageNonASCII, privateKeyAsHex);
                            assertArrayEquals(msgSignature, messageNonASCIISignature);
                          });
                    });
                describe(
                    "verify",
                    () -> {
                      it(
                          "should verify message",
                          () -> {
                            final boolean verified =
                                SigningUtil.verifyMessage(
                                    message, messageSignature, Hex.toHexString(publicKey));
                            assertTrue(verified);
                          });

                      it(
                          "should verify message with non-ASCII chars",
                          () -> {
                            final boolean verified =
                                SigningUtil.verifyMessage(
                                    messageNonASCII,
                                    messageNonASCIISignature,
                                    Hex.toHexString(publicKey));
                            assertTrue(verified);
                          });
                    });
              });

          it(
              "hashing produces 256 bit blake2b byte buffers",
              () -> {
                final String foobar = "foobar";
                final String foobarHashedHex =
                    "93a0e84a8cdd4166267dbe1263e937f08087723ac24e7dcc35b3d5941775ef47";
                byte[] hash = EncodingUtils.hash(foobar.getBytes(StandardCharsets.UTF_8));
                assertEquals(foobarHashedHex, Hex.toHexString(hash));
              });

          it(
              "convert base58Check address to hex",
              () -> {
                final String address = "ak_Gd6iMVsoonGuTF8LeswwDDN2NF5wYHAoTRtzwdEcfS32LWoxm";
                final String hex = EncodingUtils.addressToHex(address);
                final String fromHexAddress =
                    EncodingUtils.encodeCheck(
                        Hex.decode(hex.substring(2)), ApiIdentifiers.ACCOUNT_PUBKEY);
                assertEquals(fromHexAddress, address);
              });
          it(
              "commitmentHash generation test",
              () -> {
                final String kkNamespaceCommitmentId =
                    "cm_aJBGWWjT65JqviLSexkofr5oEAEuEySNkmghsqxNjGt2WqPW8";
                final String generatedCommitmentId =
                    EncodingUtils.generateCommitmentHash(BaseTest.KK_NAMESPACE, BaseTest.TEST_SALT);
                assertEquals(kkNamespaceCommitmentId, generatedCommitmentId);
              });
          it(
              "formatSalt generation test",
              () -> {
                final byte[] oneSalt =
                    new byte[] {
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      9,
                      110,
                      (byte) 178,
                      (byte) 148,
                      (byte) 244,
                      (byte) 193,
                      91
                    };
                final byte[] formattedSalt = EncodingUtils.formatSalt(BaseTest.TEST_SALT);

                assertArrayEquals(oneSalt, formattedSalt);
              });
          it(
              "nameId generation test",
              () -> {
                final byte[] nameId =
                    new byte[] {
                      (byte) 182,
                      98,
                      49,
                      33,
                      (byte) 170,
                      (byte) 167,
                      (byte) 215,
                      (byte) 180,
                      62,
                      (byte) 190,
                      1,
                      (byte) 241,
                      67,
                      76,
                      100,
                      93,
                      (byte) 243,
                      101,
                      (byte) 162,
                      45,
                      120,
                      34,
                      (byte) 190,
                      119,
                      (byte) 255,
                      (byte) 230,
                      114,
                      (byte) 199,
                      72,
                      36,
                      (byte) 190,
                      (byte) 173
                    };
                final byte[] generatedNameId = EncodingUtils.nameId(BaseTest.KK_NAMESPACE);

                assertArrayEquals(nameId, generatedNameId);
              });
          it(
              "test concat nameId and salt",
              () -> {
                final byte[] nameIdAndSalt =
                    new byte[] {
                      (byte) 182,
                      (byte) 98,
                      (byte) 49,
                      (byte) 33,
                      (byte) 170,
                      (byte) 167,
                      (byte) 215,
                      (byte) 180,
                      (byte) 62,
                      (byte) 190,
                      (byte) 1,
                      (byte) 241,
                      (byte) 67,
                      (byte) 76,
                      (byte) 100,
                      (byte) 93,
                      (byte) 243,
                      (byte) 101,
                      (byte) 162,
                      (byte) 45,
                      (byte) 120,
                      (byte) 34,
                      (byte) 190,
                      (byte) 119,
                      (byte) 255,
                      (byte) 230,
                      (byte) 114,
                      (byte) 199,
                      (byte) 72,
                      (byte) 36,
                      (byte) 190,
                      (byte) 173,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 0,
                      (byte) 9,
                      (byte) 110,
                      (byte) 178,
                      (byte) 148,
                      (byte) 244,
                      (byte) 193,
                      (byte) 91
                    };
                final byte[] generatedNameIdAndSalt =
                    ByteUtils.concatenate(
                        EncodingUtils.nameId(BaseTest.KK_NAMESPACE),
                        EncodingUtils.formatSalt(BaseTest.TEST_SALT));

                assertArrayEquals(nameIdAndSalt, generatedNameIdAndSalt);
              });
          it(
              "test concat nameIdSalt and hash",
              () -> {
                final byte[] nameIdAndSalt =
                    new byte[] {
                      (byte) 75,
                      (byte) 154,
                      (byte) 93,
                      (byte) 216,
                      (byte) 161,
                      (byte) 119,
                      (byte) 45,
                      (byte) 33,
                      (byte) 221,
                      (byte) 255,
                      (byte) 130,
                      (byte) 163,
                      (byte) 136,
                      (byte) 227,
                      (byte) 230,
                      (byte) 90,
                      (byte) 156,
                      (byte) 162,
                      (byte) 245,
                      (byte) 187,
                      (byte) 57,
                      (byte) 193,
                      (byte) 21,
                      (byte) 224,
                      (byte) 40,
                      (byte) 39,
                      (byte) 150,
                      (byte) 225,
                      (byte) 117,
                      (byte) 59,
                      (byte) 167,
                      (byte) 243
                    };
                final byte[] generatedNameIdAndSalt =
                    EncodingUtils.hash(
                        ByteUtils.concatenate(
                            EncodingUtils.nameId(BaseTest.KK_NAMESPACE),
                            EncodingUtils.formatSalt(BaseTest.TEST_SALT)));

                assertArrayEquals(nameIdAndSalt, generatedNameIdAndSalt);
              });
          it(
              "hash domain and namespace",
              () -> {
                byte[] generatedHash = EncodingUtils.hash(BaseTest.DOMAIN.getBytes());
                byte[] kkHash =
                    new byte[] {
                      (byte) 226,
                      (byte) 34,
                      (byte) 173,
                      (byte) 200,
                      (byte) 83,
                      (byte) 245,
                      (byte) 155,
                      (byte) 227,
                      (byte) 178,
                      (byte) 61,
                      (byte) 137,
                      (byte) 129,
                      (byte) 46,
                      (byte) 107,
                      (byte) 56,
                      (byte) 219,
                      (byte) 48,
                      (byte) 231,
                      (byte) 61,
                      (byte) 232,
                      (byte) 212,
                      (byte) 25,
                      (byte) 240,
                      (byte) 132,
                      (byte) 173,
                      (byte) 147,
                      (byte) 145,
                      (byte) 146,
                      (byte) 118,
                      (byte) 88,
                      (byte) 125,
                      (byte) 26
                    };

                assertArrayEquals(kkHash, generatedHash);

                byte[] generatedNS = EncodingUtils.hash(BaseTest.NS.getBytes());
                byte[] nsHash =
                    new byte[] {
                      (byte) 146,
                      (byte) 139,
                      (byte) 32,
                      (byte) 54,
                      (byte) 105,
                      (byte) 67,
                      (byte) 226,
                      (byte) 175,
                      (byte) 209,
                      (byte) 30,
                      (byte) 188,
                      (byte) 14,
                      (byte) 174,
                      (byte) 46,
                      (byte) 83,
                      (byte) 169,
                      (byte) 59,
                      (byte) 241,
                      (byte) 119,
                      (byte) 164,
                      (byte) 252,
                      (byte) 243,
                      (byte) 91,
                      (byte) 204,
                      (byte) 100,
                      (byte) 213,
                      (byte) 3,
                      (byte) 112,
                      (byte) 78,
                      (byte) 101,
                      (byte) 226,
                      (byte) 2
                    };

                assertArrayEquals(nsHash, generatedNS);
              });
        });
  }
}
