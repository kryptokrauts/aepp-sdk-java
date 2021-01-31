package com.kryptokrauts.aeternity.sdk.service;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.ApiClient;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.service.wallet.WalletServiceConfiguration;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import javax.annotation.Nonnull;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a lomboked class with service parameters should extend this configuration class like
 *
 * <pre>
 *
 * {@literal @}Getter
 * {@literal @}Builder( builderMethodName = "configure", buildMethodName =
 * "compile" )
 * public class [Servicename]ServiceConfiguration implements
 * ServiceConfiguration
 * </pre>
 *
 * parameters should provide default values in the following way
 *
 * <pre>
 * {@literal @}Builder.Default
 * [type] paramName = "value";
 * </pre>
 *
 * examples see {@link WalletServiceConfiguration}
 */
@SuperBuilder(builderMethodName = "configure", buildMethodName = "compile")
@NoArgsConstructor
public class ServiceConfiguration {

  private static final Logger _logger = LoggerFactory.getLogger(ServiceConfiguration.class);

  @Default @Nonnull protected String baseUrl = BaseConstants.DEFAULT_TESTNET_URL;

  @Default @Nonnull protected String compilerBaseUrl = BaseConstants.DEFAULT_TESTNET_COMPILER_URL;

  @Default @Nonnull protected String indaexBaseUrl = BaseConstants.DEFAULT_TESTNET_INDAEX_URL;

  @Getter @Default @Nonnull protected VirtualMachine targetVM = VirtualMachine.FATE;

  private BaseKeyPair baseKeyPair;

  /** the vertx instance */
  protected Vertx vertx;

  /** @return apiClient initalized with default or given values of vertx and baseURL */
  public ApiClient getApiClient() {
    if (vertx == null) {
      _logger.debug("Vertx entry point not initialized, creating default");
      vertx = Vertx.vertx();
    }
    if (vertx != null && baseUrl != null) {
      _logger.debug(String.format("Initializing Vertx ApiClient using baseUrl %s", baseUrl));
      return new ApiClient(
          vertx,
          new JsonObject(
              new HashMap<String, Object>(
                  ImmutableMap.of(BaseConstants.VERTX_BASE_PATH, baseUrl))));
    } else
      throw new RuntimeException(
          "Cannot instantiate ApiClient due to missing params vertx and or baseUrl");
  }

  public com.kryptokrauts.sophia.compiler.generated.ApiClient getCompilerApiClient() {
    if (vertx == null) {
      _logger.debug("Vertx entry point not initialized, creating default");
      vertx = Vertx.vertx();
    }
    if (vertx != null && compilerBaseUrl != null) {
      _logger.debug(
          String.format(
              "Initializing Vertx CompilerApiClient using compilerBaseUrl %s", compilerBaseUrl));
      return new com.kryptokrauts.sophia.compiler.generated.ApiClient(
          vertx,
          new JsonObject(
              new HashMap<String, Object>(
                  ImmutableMap.of(BaseConstants.VERTX_BASE_PATH, compilerBaseUrl))));
    } else
      throw new RuntimeException(
          "Cannot instantiate ApiClient due to missing params vertx and or compilerBaseUrl");
  }

  public com.kryptokrauts.indaex.generated.ApiClient getIndaexApiClient() {
    if (vertx == null) {
      _logger.debug("Vertx entry point not initialized, creating default");
      vertx = Vertx.vertx();
    }
    if (vertx != null && indaexBaseUrl != null) {
      _logger.debug(
          String.format(
              "Initializing Vertx IndaexApiClient using indaexBaseUrl %s", indaexBaseUrl));
      return new com.kryptokrauts.indaex.generated.ApiClient(
          vertx,
          new JsonObject(
              new HashMap<String, Object>(
                  ImmutableMap.of(BaseConstants.VERTX_BASE_PATH, indaexBaseUrl))));
    } else
      throw new RuntimeException(
          "Cannot instantiate ApiClient due to missing params vertx and or indaexBaseUrl");
  }

  public BaseKeyPair getBaseKeyPair() {
    if (baseKeyPair == null) {
      throw new InvalidParameterException(
          "Service call was initiated which needs the baseKeyPair but none is set in ServiceConfiguration.baseKeyPair - check parameters");
    }
    return baseKeyPair;
  }
}
