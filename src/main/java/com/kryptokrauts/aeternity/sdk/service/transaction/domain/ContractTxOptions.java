package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ContractTxOptions {

  /** a list of params to be passed */
  private List<Object> params;
  /** the amount in ættos to be passed to the contract */
  private BigInteger amount;
  /**
   * the custom gasLimit to be used in the tx instead of the default (CONTRACT_DEFAULT_GAS_LIMIT in
   * {@link com.kryptokrauts.aeternity.sdk.constants.BaseConstants})
   */
  private BigInteger gasLimit;
  /**
   * the custom gasPrice to be used in the tx instead of the default (MINIMAL_GAS_PRICE in {@link
   * com.kryptokrauts.aeternity.sdk.constants.BaseConstants})
   */
  private BigInteger gasPrice;
  /**
   * the custom nonce to be used in the tx. by default the required nonce will be automatically
   * determined by the sdk
   */
  private BigInteger nonce;
  /** the custom ttl to be used in the tx. default: ZERO */
  private BigInteger ttl;
  /**
   * the includes map for the contract (key = include-name, value = source code of the include).
   * only needed if there are custom includes defined in the contract source
   */
  private Map<String, String> filesystem;

  /**
   * by default the {@link com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService}
   * will use the KeyPair configured in the {@link
   * com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration}
   *
   * <p>if a custom KeyPair is provided, it will be used instead
   */
  private KeyPair customKeyPair;
}
