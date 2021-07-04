package com.kryptokrauts.aeternity.sdk.service.delegation.impl;

import com.kryptokrauts.aeternity.sdk.constants.AENS;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.delegation.DelegationService;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.SigningUtil;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.encoders.Hex;

@RequiredArgsConstructor
public class DelegationServiceImpl implements DelegationService {

  @Nonnull private ServiceConfiguration config;

  @Override
  public String createAensDelegationSignature(String contractId) {
    byte[] payload =
        ByteUtils.concatenate(
            EncodingUtils.decodeCheckWithIdentifier(this.config.getKeyPair().getAddress()),
            EncodingUtils.decodeCheckWithIdentifier(contractId));
    return signPayloadWithNetworkId(payload);
  }

  @Override
  public String createAensDelegationSignature(String contractId, String name) {
    byte[] payload =
        ByteUtils.concatenate(
            EncodingUtils.decodeCheckWithIdentifier(this.config.getKeyPair().getAddress()),
            EncodingUtils.decodeCheckWithIdentifier(AENS.getNameId(name)),
            EncodingUtils.decodeCheckWithIdentifier(contractId));
    return signPayloadWithNetworkId(payload);
  }

  @Override
  public String createOracleDelegationSignature(String contractId) {
    byte[] payload =
        ByteUtils.concatenate(
            EncodingUtils.decodeCheckWithIdentifier(this.config.getKeyPair().getAddress()),
            EncodingUtils.decodeCheckWithIdentifier(contractId));
    return signPayloadWithNetworkId(payload);
  }

  @Override
  public String createOracleDelegationSignature(String contractId, String queryId) {
    byte[] payload =
        ByteUtils.concatenate(
            EncodingUtils.decodeCheckWithIdentifier(queryId),
            EncodingUtils.decodeCheckWithIdentifier(contractId));
    return signPayloadWithNetworkId(payload);
  }

  private String signPayloadWithNetworkId(byte[] payload) {
    try {
      byte[] signature =
          SigningUtil.sign(
              ByteUtils.concatenate(
                  this.config.getNetwork().getId().getBytes(StandardCharsets.UTF_8), payload),
              this.config.getKeyPair().getEncodedPrivateKey());
      return Hex.toHexString(signature);
    } catch (CryptoException e) {
      throw new AException(String.format("Error during signing: %s", e.getMessage()), e);
    }
  }
}
