package com.kryptokrauts.aeternity.sdk.service;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.ApiClient;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.service.keystore.KeystoreServiceConfiguration;
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
 * The SDKs basic configuration class.
 *
 * <p>Other configuration classes with service parameters should extend this configuration class
 * like
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
 * examples see {@link KeystoreServiceConfiguration}
 */
@SuperBuilder(builderMethodName = "configure", buildMethodName = "compile")
@NoArgsConstructor
public class ServiceConfiguration {

  private static final Logger _logger = LoggerFactory.getLogger(ServiceConfiguration.class);

  @Default @Nonnull protected String baseUrl = BaseConstants.DEFAULT_TESTNET_URL;

  @Default @Nonnull protected String debugBaseUrl = BaseConstants.DEFAULT_TESTNET_URL;

  @Default @Nonnull protected String compilerBaseUrl = BaseConstants.DEFAULT_TESTNET_COMPILER_URL;

  @Default @Nonnull protected String mdwBaseUrl = BaseConstants.DEFAULT_TESTNET_MDW_URL;

  @Getter @Default protected Network network = Network.TESTNET;

  @Getter @Default @Nonnull protected VirtualMachine targetVM = VirtualMachine.FATE;

  private KeyPair keyPair;

  /** the vertx instance */
  protected Vertx vertx;

  public KeyPair getKeyPair() {
    if (keyPair == null) {
      throw new InvalidParameterException(
          "Service call was initiated which needs the keyPair but none is set in ServiceConfiguration.keyPair - check parameters");
    }
    return keyPair;
  }

  /** @return apiClient initialized with default or given values of vertx and baseURL */
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

  public ApiClient getDebugApiClient() {
    if (vertx == null) {
      _logger.debug("Vertx entry point not initialized, creating default");
      vertx = Vertx.vertx();
    }
    if (vertx != null && debugBaseUrl != null) {
      _logger.debug(
          String.format("Initializing Vertx ApiClient using debugBaseUrl %s", debugBaseUrl));
      return new ApiClient(
          vertx,
          new JsonObject(
              new HashMap<String, Object>(
                  ImmutableMap.of(BaseConstants.VERTX_BASE_PATH, debugBaseUrl))));
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

  public com.kryptokrauts.mdw.generated.ApiClient getMdwApiClient() {
    if (vertx == null) {
      _logger.debug("Vertx entry point not initialized, creating default");
      vertx = Vertx.vertx();
    }
    if (vertx != null && mdwBaseUrl != null) {
      _logger.debug(
          String.format("Initializing Vertx MdwApiClient using mdwBaseUrl %s", mdwBaseUrl));
      return new com.kryptokrauts.mdw.generated.ApiClient(
          vertx,
          new JsonObject(
              new HashMap<String, Object>(
                  ImmutableMap.of(BaseConstants.VERTX_BASE_PATH, mdwBaseUrl))));
    } else
      throw new RuntimeException(
          "Cannot instantiate ApiClient due to missing params vertx and or mdwBaseUrl");
  }
}
