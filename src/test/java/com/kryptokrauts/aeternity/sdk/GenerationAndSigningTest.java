package com.kryptokrauts.aeternity.sdk;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import java.nio.charset.StandardCharsets;
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
                      KeyPair keypair = keypairService.generateKeyPair();
                      assertNotNull(keypair);
                      assertTrue(EncodingUtils.isAddressValid(keypair.getAddress()));
                      assertTrue(keypair.getAddress().startsWith("ak_"));
                      int length = keypair.getAddress().length();
                      assertTrue(length <= 53 && length >= 51);
                    });
              });

          describe(
              "encryptPassword",
              () -> {
                describe(
                    "generate a password encrypted key pair",
                    () -> {
                      KeyPair keyPair = keypairService.generateKeyPair();
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
                            final byte[] publicBinary = ((KeyPair) keyPair).getRawPublicKey();
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
                      final KeyPair keyPair =
                          keypairService.generateKeyPairFromSecret(
                              "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF");
                      assertEquals(beneficiaryPub, keyPair.getAddress());
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
                    "c910daedceebb658f49ad862b2032e6b455143ebc1d698e9018ac4cf2d76f65fefda254893b0f56203b4cef1ff549f72ef155d58792fd52d0c1b6e210525e207";
                final byte[] messageSignature = Hex.decode(messageSignatureAsHex);

                final String messageNonASCII = "tæst";
                final String messageNonASCIISignatureAsHex =
                    "faa1bdb8a88c529be904036382705ed207bbdde00ece3bdb541f5986d57aebe7babe315a4d95f5882165c28bf41f6149430509ded1cc7dcd9b134e0e1d73cd0b";
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
                    "cm_27G8vARHeEm3Z1KsDwTZ4kTWwdNorDJryAaT5fjtJDcegfDjBQ";
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
                final byte[] formattedSalt =
                    ByteUtils.leftPad(32, BaseTest.TEST_SALT.toByteArray());

                assertArrayEquals(oneSalt, formattedSalt);
              });
          it(
              "hash name and namespace",
              () -> {
                byte[] generatedHash = EncodingUtils.hash(BaseTest.NAME.getBytes());
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
