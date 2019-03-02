package com.kryptokrauts.aeternity.sdk.service.keypair;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(builderMethodName = "configure", buildMethodName = "compile")
public class KeyPairServiceConfiguration extends ServiceConfiguration {
  @Default private String cipherAlgorithm = "AES/ECB/NoPadding";

  @Default private String secretKeySpec = "AES";

  /**
   * this param has direct influence to the number of mnemonic seed words for correlation of entropy
   * bit size and number of words see spec {@linkplain
   * https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki#Generating_the_mnemonic}
   */
  @Default private int entropySizeInByte = 256 / 8;
}
